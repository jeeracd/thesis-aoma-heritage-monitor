import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SpectrogramPanel extends JPanel {
    private BufferedImage image;
    private SpectrogramData data;
    private String statusText = "No spectrogram loaded.";
    private SpectrogramViewWindowListener listener;
    private SpectrogramViewWindow viewWindow;

    private Point dragStart;
    private Rectangle selectionPx;

    public SpectrogramPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension(850, 220));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (image == null || data == null) {
                    return;
                }
                if (e.getClickCount() >= 2) {
                    selectionPx = null;
                    viewWindow = SpectrogramViewWindow.full(data);
                    fireViewWindowChanged();
                    repaint();
                    return;
                }
                Rectangle plot = plotRect();
                if (plot.contains(e.getPoint())) {
                    dragStart = e.getPoint();
                    selectionPx = new Rectangle(dragStart);
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart == null || selectionPx == null) {
                    return;
                }
                Rectangle plot = plotRect();
                Point p = e.getPoint();
                int x1 = clamp(p.x, plot.x, plot.x + plot.width);
                int y1 = clamp(p.y, plot.y, plot.y + plot.height);
                int x0 = clamp(dragStart.x, plot.x, plot.x + plot.width);
                int y0 = clamp(dragStart.y, plot.y, plot.y + plot.height);
                int rx = Math.min(x0, x1);
                int ry = Math.min(y0, y1);
                int rw = Math.max(1, Math.abs(x1 - x0));
                int rh = Math.max(1, Math.abs(y1 - y0));
                selectionPx = new Rectangle(rx, ry, rw, rh);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragStart == null || selectionPx == null || data == null) {
                    dragStart = null;
                    return;
                }
                if (selectionPx.width < 5 || selectionPx.height < 5) {
                    selectionPx = null;
                    dragStart = null;
                    repaint();
                    return;
                }
                viewWindow = windowFromSelection(selectionPx, data);
                fireViewWindowChanged();
                dragStart = null;
                repaint();
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void setStatusText(String text) {
        this.statusText = text == null ? "" : text;
        repaint();
    }

    public void setSpectrogram(SpectrogramData data, BufferedImage image) {
        this.data = data;
        this.image = image;
        this.statusText = "";
        this.viewWindow = SpectrogramViewWindow.full(data);
        this.selectionPx = null;
        fireViewWindowChanged();
        repaint();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void setViewWindowListener(SpectrogramViewWindowListener listener) {
        this.listener = listener;
        fireViewWindowChanged();
    }

    public SpectrogramViewWindow getViewWindow() {
        if (viewWindow != null) {
            return viewWindow;
        }
        if (data != null) {
            return SpectrogramViewWindow.full(data);
        }
        return new SpectrogramViewWindow(0, 0, 0, 0);
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
                if (selectionPx != null) {
                    g2.setColor(new Color(255, 255, 255, 90));
                    g2.fill(selectionPx);
                    g2.setColor(new Color(0, 120, 215));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(selectionPx);
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

    private Rectangle plotRect() {
        int w = getWidth();
        int h = getHeight();
        int left = 55;
        int right = 10;
        int top = 8;
        int bottom = 28;
        int pw = Math.max(1, w - left - right);
        int ph = Math.max(1, h - top - bottom);
        return new Rectangle(left, top, pw, ph);
    }

    private SpectrogramViewWindow windowFromSelection(Rectangle sel, SpectrogramData data) {
        double fullT = Math.max(0, data.durationSec());
        double maxF = Math.max(0, data.fsHz() / 2.0);

        Rectangle plot = plotRect();
        double x0 = (sel.x - plot.x) / (double) plot.width;
        double x1 = (sel.x + sel.width - plot.x) / (double) plot.width;
        double y0 = (sel.y - plot.y) / (double) plot.height;
        double y1 = (sel.y + sel.height - plot.y) / (double) plot.height;

        x0 = clamp01(x0);
        x1 = clamp01(x1);
        y0 = clamp01(y0);
        y1 = clamp01(y1);

        double tStart = Math.min(x0, x1) * fullT;
        double tEnd = Math.max(x0, x1) * fullT;

        double fHigh = (1.0 - Math.min(y0, y1)) * maxF;
        double fLow = (1.0 - Math.max(y0, y1)) * maxF;

        return new SpectrogramViewWindow(tStart, tEnd, fLow, fHigh);
    }

    private void fireViewWindowChanged() {
        if (listener == null) {
            return;
        }
        listener.onViewWindowChanged(getViewWindow());
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
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

