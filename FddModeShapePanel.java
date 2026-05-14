import javax.swing.*;
import java.awt.*;

public final class FddModeShapePanel extends JPanel {
    private final JLabel title = new JLabel("Select a peak to view mode shape");

    private volatile String[] labels = new String[0];
    private volatile double[] values = new double[0];
    private volatile double freqHz = Double.NaN;

    public FddModeShapePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        title.setFont(UiControlMetrics.CONTROL_FONT);
        title.setForeground(Color.DARK_GRAY);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, UiControlMetrics.HGAP, UiControlMetrics.VGAP));
        header.setOpaque(false);
        UiControlMetrics.setRowMaxHeight(header);
        header.add(title);
        add(header, BorderLayout.NORTH);
    }

    public void clear() {
        freqHz = Double.NaN;
        labels = new String[0];
        values = new double[0];
        title.setText("Select a peak to view mode shape");
        repaint();
    }

    public void setModeShape(double freqHz, String[] channelLabels, double[] normalizedMagnitudes) {
        this.freqHz = freqHz;
        this.labels = channelLabels == null ? new String[0] : channelLabels.clone();
        this.values = normalizedMagnitudes == null ? new double[0] : normalizedMagnitudes.clone();
        if (Double.isFinite(freqHz)) {
            title.setText("Mode shape at " + formatHz(freqHz) + " Hz (normalized)");
        } else {
            title.setText("Mode shape (normalized)");
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            int top = UiControlMetrics.ROW_HEIGHT;
            int left = 60;
            int right = 20;
            int bottom = 24;
            int plotW = Math.max(10, w - left - right);
            int plotH = Math.max(10, h - top - bottom);

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            int n = Math.min(labels.length, values.length);
            if (n <= 0) {
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(UiControlMetrics.CONTROL_FONT);
                g2.drawString("No mode shape available.", left, top + 20);
                return;
            }

            double max = 0;
            for (int i = 0; i < n; i++) {
                if (Double.isFinite(values[i])) {
                    max = Math.max(max, values[i]);
                }
            }
            if (!(max > 0)) {
                max = 1.0;
            }

            int barGap = 10;
            int barW = Math.max(14, (plotW - (n - 1) * barGap) / n);
            int totalW = n * barW + (n - 1) * barGap;
            int startX = left + Math.max(0, (plotW - totalW) / 2);

            g2.setColor(new Color(210, 210, 210));
            g2.drawLine(left, top + plotH, left + plotW, top + plotH);
            g2.drawLine(left, top, left, top + plotH);

            g2.setFont(UiControlMetrics.CONTROL_FONT);
            for (int i = 0; i <= 4; i++) {
                double t = i / 4.0;
                int y = top + plotH - (int) Math.round(t * plotH);
                g2.setColor(new Color(235, 235, 235));
                g2.drawLine(left, y, left + plotW, y);
                g2.setColor(new Color(90, 90, 90));
                String lbl = String.format("%.0f%%", t * 100);
                g2.drawString(lbl, 10, y + 4);
            }

            for (int i = 0; i < n; i++) {
                double v = Double.isFinite(values[i]) ? values[i] : 0;
                double nv = Math.max(0, Math.min(1.0, v / max));
                int bh = (int) Math.round(nv * plotH);
                int x = startX + i * (barW + barGap);
                int y = top + plotH - bh;

                g2.setColor(new Color(0, 102, 204));
                g2.fillRect(x, y, barW, bh);
                g2.setColor(new Color(40, 40, 40));
                g2.drawRect(x, y, barW, bh);

                String name = labels[i] == null ? ("Ch" + (i + 1)) : labels[i];
                int tx = x;
                int ty = top + plotH + 18;
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(truncate(name, barW, g2.getFontMetrics()), tx, ty);
            }
        } finally {
            g2.dispose();
        }
    }

    private static String truncate(String s, int maxPx, FontMetrics fm) {
        if (s == null) {
            return "";
        }
        if (fm.stringWidth(s) <= maxPx) {
            return s;
        }
        String ell = "…";
        int ellW = fm.stringWidth(ell);
        String t = s;
        while (!t.isEmpty() && fm.stringWidth(t) + ellW > maxPx) {
            t = t.substring(0, t.length() - 1);
        }
        return t.isEmpty() ? ell : t + ell;
    }

    private static String formatHz(double hz) {
        if (hz >= 100) {
            return String.format("%.0f", hz);
        }
        if (hz >= 10) {
            return String.format("%.1f", hz);
        }
        if (hz >= 1) {
            return String.format("%.2f", hz);
        }
        return String.format("%.3f", hz);
    }
}

