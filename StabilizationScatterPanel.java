import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public final class StabilizationScatterPanel extends JPanel {

    private record Pole(int order, double freqHz, double dampingRatio, String label, boolean stable) {}

    private static final Color STABLE_FILL   = new Color(22, 163, 74);
    private static final Color UNSTABLE_FILL = new Color(180, 180, 180);
    private static final Color MODE_LINE     = new Color(220, 38, 38);
    private static final Color AXIS_COLOR    = new Color(60, 60, 60);
    private static final Color GRID_COLOR    = new Color(220, 220, 220);
    private static final Color BG_COLOR      = Color.WHITE;
    private static final Color MSG_COLOR     = new Color(100, 100, 100);

    private static final Font AXIS_FONT  = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final int PAD_LEFT   = 52;
    private static final int PAD_BOTTOM = 36;
    private static final int PAD_TOP    = 28;
    private static final int PAD_RIGHT  = 16;

    private static final double ZOOM_FACTOR = 1.12;
    private static final int STABLE_R  = 4;
    private static final int UNSTABLE_R = 2;

    private List<Pole> poles = List.of();
    private List<Double> selectedFreqsHz = List.of();
    private String message = "No results yet. Run PyOMA2 to view the stabilization diagram.";

    // View transform: data → screen maps via these ranges
    private double viewMinFreq = 0;
    private double viewMaxFreq = 1;
    private double viewMinOrder = 0;
    private double viewMaxOrder = 1;

    private int dragStartX;
    private int dragStartY;
    private double dragStartMinFreq;
    private double dragStartMaxFreq;
    private double dragStartMinOrder;
    private double dragStartMaxOrder;

    public StabilizationScatterPanel() {
        setBackground(BG_COLOR);
        setMinimumSize(new Dimension(300, 200));
        setPreferredSize(new Dimension(600, 360));
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
                dragStartMinFreq  = viewMinFreq;
                dragStartMaxFreq  = viewMaxFreq;
                dragStartMinOrder = viewMinOrder;
                dragStartMaxOrder = viewMaxOrder;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (poles.isEmpty()) return;
                int pw = plotWidth();
                int ph = plotHeight();
                if (pw <= 0 || ph <= 0) return;
                double dxData = -(e.getX() - dragStartX) * (dragStartMaxFreq - dragStartMinFreq) / pw;
                double dyData =  (e.getY() - dragStartY) * (dragStartMaxOrder - dragStartMinOrder) / ph;
                viewMinFreq  = dragStartMinFreq  + dxData;
                viewMaxFreq  = dragStartMaxFreq  + dxData;
                viewMinOrder = dragStartMinOrder + dyData;
                viewMaxOrder = dragStartMaxOrder + dyData;
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (poles.isEmpty()) return;
                int pw = plotWidth();
                int ph = plotHeight();
                if (pw <= 0 || ph <= 0) return;
                double mx = screenToFreq(e.getX(), pw);
                double my = screenToOrder(e.getY(), ph);
                double factor = e.getWheelRotation() > 0 ? ZOOM_FACTOR : 1.0 / ZOOM_FACTOR;
                viewMinFreq  = mx - (mx - viewMinFreq)  * factor;
                viewMaxFreq  = mx + (viewMaxFreq - mx)  * factor;
                viewMinOrder = my - (my - viewMinOrder) * factor;
                viewMaxOrder = my + (viewMaxOrder - my) * factor;
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    public void loadFromPaths(String polesCsvPath, String modalCsvPath) {
        message = "Loading poles…";
        poles = List.of();
        selectedFreqsHz = List.of();
        repaint();

        new SwingWorker<LoadResult, Void>() {
            @Override
            protected LoadResult doInBackground() {
                List<Pole> ps = readPoles(polesCsvPath);
                List<Double> freqs = readSelectedFreqs(modalCsvPath);
                return new LoadResult(ps, freqs);
            }

            @Override
            protected void done() {
                try {
                    LoadResult r = get();
                    poles = r.poles();
                    selectedFreqsHz = r.freqs();
                    if (poles.isEmpty()) {
                        message = "No pole data found. Check that PyOMA2 completed successfully.";
                    } else {
                        message = null;
                        resetView();
                    }
                } catch (Exception ex) {
                    message = "Failed to load poles: " + ex.getMessage();
                }
                repaint();
            }
        }.execute();
    }

    public void clear() {
        poles = List.of();
        selectedFreqsHz = List.of();
        message = "No results yet. Run PyOMA2 to view the stabilization diagram.";
        repaint();
    }

    private record LoadResult(List<Pole> poles, List<Double> freqs) {}

    private static List<Pole> readPoles(String path) {
        if (path == null || path.isBlank()) return List.of();
        Path p = Path.of(path);
        if (!Files.isRegularFile(p)) return List.of();
        List<Pole> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            if (header == null) return List.of();
            String[] cols = header.split(",", -1);
            int iOrder = idx(cols, "order");
            int iFreq  = idx(cols, "frequency_hz");
            int iXi    = idx(cols, "damping_ratio");
            int iLab   = idx(cols, "label");
            int iStab  = idx(cols, "stable");
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", -1);
                int order = safeInt(f, iOrder, 0);
                double freq = safeDbl(f, iFreq, Double.NaN);
                double xi   = safeDbl(f, iXi, Double.NaN);
                String lab  = safeStr(f, iLab, "U");
                boolean stable = safeInt(f, iStab, 0) == 1;
                if (order > 0 && Double.isFinite(freq) && freq > 0) {
                    out.add(new Pole(order, freq, xi, lab, stable));
                }
            }
        } catch (Exception ignored) {}
        return List.copyOf(out);
    }

    private static List<Double> readSelectedFreqs(String path) {
        if (path == null || path.isBlank()) return List.of();
        Path p = Path.of(path);
        if (!Files.isRegularFile(p)) return List.of();
        List<Double> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            if (header == null) return List.of();
            String[] cols = header.split(",", -1);
            int iFreq = idx(cols, "frequency_hz");
            if (iFreq < 0) iFreq = idx(cols, "fn");
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", -1);
                double freq = safeDbl(f, iFreq, Double.NaN);
                if (Double.isFinite(freq) && freq > 0) out.add(freq);
            }
        } catch (Exception ignored) {}
        return List.copyOf(out);
    }

    private void resetView() {
        if (poles.isEmpty()) return;
        double minF = Double.MAX_VALUE, maxF = -Double.MAX_VALUE;
        int minO = Integer.MAX_VALUE, maxO = Integer.MIN_VALUE;
        for (Pole p : poles) {
            if (p.freqHz() < minF) minF = p.freqHz();
            if (p.freqHz() > maxF) maxF = p.freqHz();
            if (p.order() < minO) minO = p.order();
            if (p.order() > maxO) maxO = p.order();
        }
        double freqSpan = Math.max(maxF - minF, 1.0);
        viewMinFreq  = Math.max(0, minF - freqSpan * 0.05);
        viewMaxFreq  = maxF + freqSpan * 0.05;
        viewMinOrder = Math.max(0, minO - 1);
        viewMaxOrder = maxO + 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int pw = plotWidth();
        int ph = plotHeight();

        if (message != null || poles.isEmpty()) {
            g2.setFont(AXIS_FONT);
            g2.setColor(MSG_COLOR);
            FontMetrics fm = g2.getFontMetrics();
            String m = message != null ? message : "No poles to display.";
            g2.drawString(m, (getWidth() - fm.stringWidth(m)) / 2, getHeight() / 2);
            g2.dispose();
            return;
        }

        drawGrid(g2, pw, ph);
        drawAxes(g2, pw, ph);
        drawModeLines(g2, pw, ph);
        drawPoles(g2, pw, ph);
        drawLegend(g2);
        drawTitle(g2);

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2, int pw, int ph) {
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.5f));
        int nTicksX = Math.max(4, pw / 60);
        for (int i = 0; i <= nTicksX; i++) {
            double f = viewMinFreq + i * (viewMaxFreq - viewMinFreq) / nTicksX;
            int sx = freqToScreen(f, pw);
            g2.drawLine(sx, PAD_TOP, sx, PAD_TOP + ph);
        }
        int nTicksY = Math.max(4, ph / 40);
        for (int i = 0; i <= nTicksY; i++) {
            double o = viewMinOrder + i * (viewMaxOrder - viewMinOrder) / nTicksY;
            int sy = orderToScreen(o, ph);
            g2.drawLine(PAD_LEFT, sy, PAD_LEFT + pw, sy);
        }
    }

    private void drawAxes(Graphics2D g2, int pw, int ph) {
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRect(PAD_LEFT, PAD_TOP, pw, ph);

        g2.setFont(AXIS_FONT);
        FontMetrics fm = g2.getFontMetrics();

        // X axis ticks + labels
        int nTicksX = Math.max(4, pw / 60);
        for (int i = 0; i <= nTicksX; i++) {
            double f = viewMinFreq + i * (viewMaxFreq - viewMinFreq) / nTicksX;
            int sx = freqToScreen(f, pw);
            g2.drawLine(sx, PAD_TOP + ph, sx, PAD_TOP + ph + 4);
            String label = String.format("%.1f", f);
            g2.drawString(label, sx - fm.stringWidth(label) / 2, PAD_TOP + ph + 16);
        }
        String xLabel = "Frequency (Hz)";
        g2.drawString(xLabel, PAD_LEFT + (pw - fm.stringWidth(xLabel)) / 2, getHeight() - 4);

        // Y axis ticks + labels
        int nTicksY = Math.max(4, ph / 40);
        for (int i = 0; i <= nTicksY; i++) {
            double o = viewMinOrder + i * (viewMaxOrder - viewMinOrder) / nTicksY;
            int sy = orderToScreen(o, ph);
            g2.drawLine(PAD_LEFT - 4, sy, PAD_LEFT, sy);
            String label = String.valueOf((int) Math.round(o));
            g2.drawString(label, PAD_LEFT - fm.stringWidth(label) - 6, sy + fm.getAscent() / 2);
        }

        // Y axis title (rotated)
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.setFont(AXIS_FONT);
        g2r.setColor(AXIS_COLOR);
        g2r.rotate(-Math.PI / 2, 12, PAD_TOP + ph / 2);
        String yLabel = "Model Order";
        FontMetrics fmr = g2r.getFontMetrics();
        g2r.drawString(yLabel, 12 - fmr.stringWidth(yLabel) / 2, PAD_TOP + ph / 2 + fmr.getAscent() / 2);
        g2r.dispose();
    }

    private void drawModeLines(Graphics2D g2, int pw, int ph) {
        if (selectedFreqsHz.isEmpty()) return;
        Stroke dashed = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4f, new float[]{5f, 4f}, 0f);
        g2.setColor(MODE_LINE);
        g2.setStroke(dashed);
        g2.setFont(AXIS_FONT);
        FontMetrics fm = g2.getFontMetrics();
        for (Double freq : selectedFreqsHz) {
            if (freq < viewMinFreq || freq > viewMaxFreq) continue;
            int sx = freqToScreen(freq, pw);
            g2.drawLine(sx, PAD_TOP, sx, PAD_TOP + ph);
            String label = String.format("%.2f Hz", freq);
            g2.setColor(new Color(MODE_LINE.getRed(), MODE_LINE.getGreen(), MODE_LINE.getBlue(), 200));
            g2.drawString(label, sx + 3, PAD_TOP + 12);
            g2.setColor(MODE_LINE);
        }
        g2.setStroke(new BasicStroke(1.0f));
    }

    private void drawPoles(Graphics2D g2, int pw, int ph) {
        for (Pole p : poles) {
            if (p.freqHz() < viewMinFreq || p.freqHz() > viewMaxFreq) continue;
            if (p.order() < viewMinOrder || p.order() > viewMaxOrder) continue;
            int sx = freqToScreen(p.freqHz(), pw);
            int sy = orderToScreen(p.order(), ph);
            if (p.stable()) {
                g2.setColor(STABLE_FILL);
                g2.fillOval(sx - STABLE_R, sy - STABLE_R, STABLE_R * 2, STABLE_R * 2);
            } else {
                g2.setColor(UNSTABLE_FILL);
                g2.fillOval(sx - UNSTABLE_R, sy - UNSTABLE_R, UNSTABLE_R * 2, UNSTABLE_R * 2);
            }
        }
    }

    private void drawLegend(Graphics2D g2) {
        int lx = PAD_LEFT + 8;
        int ly = PAD_TOP + 8;
        g2.setFont(AXIS_FONT);
        FontMetrics fm = g2.getFontMetrics();

        g2.setColor(STABLE_FILL);
        g2.fillOval(lx, ly, STABLE_R * 2, STABLE_R * 2);
        g2.setColor(AXIS_COLOR);
        g2.drawString("Stable", lx + STABLE_R * 2 + 4, ly + fm.getAscent() - 1);

        int lx2 = lx + fm.stringWidth("Stable") + STABLE_R * 2 + 20;
        g2.setColor(UNSTABLE_FILL);
        g2.fillOval(lx2, ly + 1, UNSTABLE_R * 2, UNSTABLE_R * 2);
        g2.setColor(AXIS_COLOR);
        g2.drawString("Unstable", lx2 + UNSTABLE_R * 2 + 4, ly + fm.getAscent() - 1);

        if (!selectedFreqsHz.isEmpty()) {
            int lx3 = lx2 + fm.stringWidth("Unstable") + UNSTABLE_R * 2 + 20;
            Stroke saved = g2.getStroke();
            g2.setColor(MODE_LINE);
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4f, new float[]{5f, 4f}, 0f));
            g2.drawLine(lx3, ly + STABLE_R, lx3 + 16, ly + STABLE_R);
            g2.setStroke(saved);
            g2.setColor(AXIS_COLOR);
            g2.drawString("Selected mode", lx3 + 20, ly + fm.getAscent() - 1);
        }
    }

    private void drawTitle(Graphics2D g2) {
        g2.setFont(TITLE_FONT);
        g2.setColor(AXIS_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        long stableCount = poles.stream().filter(Pole::stable).count();
        String title = String.format("Stabilization Diagram  —  %d poles  (%d stable)", poles.size(), stableCount);
        g2.drawString(title, PAD_LEFT + (plotWidth() - fm.stringWidth(title)) / 2, PAD_TOP - 6);
    }

    // --- coordinate helpers ---

    private int plotWidth()  { return Math.max(1, getWidth()  - PAD_LEFT - PAD_RIGHT); }
    private int plotHeight() { return Math.max(1, getHeight() - PAD_TOP  - PAD_BOTTOM); }

    private int freqToScreen(double freq, int pw) {
        double span = viewMaxFreq - viewMinFreq;
        if (span <= 0) return PAD_LEFT;
        return PAD_LEFT + (int) Math.round((freq - viewMinFreq) / span * pw);
    }

    private int orderToScreen(double order, int ph) {
        double span = viewMaxOrder - viewMinOrder;
        if (span <= 0) return PAD_TOP;
        // Y increases downward; high order at bottom
        return PAD_TOP + ph - (int) Math.round((order - viewMinOrder) / span * ph);
    }

    private double screenToFreq(int sx, int pw) {
        return viewMinFreq + (sx - PAD_LEFT) * (viewMaxFreq - viewMinFreq) / pw;
    }

    private double screenToOrder(int sy, int ph) {
        return viewMinOrder + (PAD_TOP + ph - sy) * (viewMaxOrder - viewMinOrder) / ph;
    }

    // --- CSV helpers ---

    private static int idx(String[] cols, String name) {
        for (int i = 0; i < cols.length; i++) {
            if (cols[i].trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    private static double safeDbl(String[] f, int i, double def) {
        if (i < 0 || i >= f.length) return def;
        String s = f[i].trim();
        if (s.isEmpty()) return def;
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }

    private static int safeInt(String[] f, int i, int def) {
        if (i < 0 || i >= f.length) return def;
        String s = f[i].trim();
        if (s.isEmpty()) return def;
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static String safeStr(String[] f, int i, String def) {
        if (i < 0 || i >= f.length) return def;
        String s = f[i].trim();
        return s.isEmpty() ? def : s;
    }
}
