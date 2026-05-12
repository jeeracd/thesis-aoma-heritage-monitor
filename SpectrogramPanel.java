import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class SpectrogramPanel extends JPanel {
    private BufferedImage image;
    private SpectrogramData data;
    private String statusText = "No spectrogram loaded.";

    public SpectrogramPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(850, 220));
        setMaximumSize(new Dimension(850, 220));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void setStatusText(String text) {
        this.statusText = text == null ? "" : text;
        repaint();
    }

    public void setSpectrogram(SpectrogramData data, BufferedImage image) {
        this.data = data;
        this.image = image;
        this.statusText = "";
        repaint();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (image != null) {
                int left = 55;
                int right = 10;
                int top = 8;
                int bottom = 28;
                int pw = Math.max(1, w - left - right);
                int ph = Math.max(1, h - top - bottom);

                g2.setColor(Color.WHITE);
                g2.fillRect(1, 1, w - 2, h - 2);

                g2.drawImage(image, left, top, pw, ph, null);

                if (data != null) {
                    drawAxes(g2, left, top, pw, ph, data);
                }
                return;
            }

            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(1, 1, w - 2, h - 2);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            FontMetrics fm = g2.getFontMetrics();
            String text = statusText == null ? "" : statusText;
            int tx = Math.max(10, (w - fm.stringWidth(text)) / 2);
            int ty = (h + fm.getAscent()) / 2;
            g2.drawString(text, tx, ty);
        } finally {
            g2.dispose();
        }
    }

    private static void drawAxes(Graphics2D g2, int x, int y, int w, int h, SpectrogramData data) {
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1f));

        g2.drawRect(x, y, w, h);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        String xLabel = "Time (s)";
        String yLabel = "Frequency (Hz)";

        FontMetrics fm = g2.getFontMetrics();
        int xlabelX = x + (w - fm.stringWidth(xLabel)) / 2;
        int xlabelY = y + h + 22;
        g2.drawString(xLabel, Math.max(x, xlabelX), xlabelY);

        int maxHz = (int) Math.round(data.fsHz() / 2.0);
        String yMin = "0";
        String yMax = String.valueOf(Math.max(0, maxHz));
        g2.drawString(yMax, 6, y + fm.getAscent());
        g2.drawString(yMin, 10, y + h);

        String t0 = "0";
        String t1 = String.format("%.2f", Math.max(0, data.durationSec()));
        g2.drawString(t0, x, y + h + 14);
        g2.drawString(t1, x + w - fm.stringWidth(t1), y + h + 14);

        Graphics2D gRot = (Graphics2D) g2.create();
        try {
            gRot.rotate(-Math.PI / 2);
            int yLabelX = -(y + (h + fm.stringWidth(yLabel)) / 2);
            int yLabelY = 18;
            gRot.drawString(yLabel, yLabelX, yLabelY);
        } finally {
            gRot.dispose();
        }
    }
}

