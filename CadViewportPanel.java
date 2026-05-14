import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CadViewportPanel extends JPanel {
    public enum Tool {
        SELECT,
        PAN,
        ANNOTATE
    }

    public enum ViewType {
        FREQUENCY,
        DAMPING
    }

    public record Annotation(double x, double y, String text) {}

    public interface ViewportListener {
        void onHover(double x, double y);
        void onSelectMode(int modeIndex);
        void onValidationCount(int issuesCount);
    }

    private OmaResultsModel model = OmaResultsModel.loadFromDirOrEmpty(null);
    private ViewType viewType = ViewType.FREQUENCY;
    private Tool tool = Tool.SELECT;
    private ViewportListener listener;

    private boolean showPoints = true;
    private boolean showLabels = true;
    private boolean showValidation = true;
    private float pointsAlpha = 1.0f;
    private float labelsAlpha = 1.0f;

    private final List<Annotation> annotations = new ArrayList<>();

    private int selectedModeIndex = -1;

    private double panX = 0.0;
    private double panY = 0.0;
    private double zoom = 1.0;

    private Point lastDrag;

    public CadViewportPanel() {
        setBackground(new Color(18, 18, 20));
        setOpaque(true);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point2D world = screenToWorld(e.getPoint());
                if (listener != null) {
                    listener.onHover(world.getX(), world.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                lastDrag = e.getPoint();
                if (SwingUtilities.isRightMouseButton(e)) {
                    showViewportContextMenu(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDrag == null) {
                    lastDrag = e.getPoint();
                    return;
                }
                if (tool != Tool.PAN) {
                    return;
                }
                int dx = e.getX() - lastDrag.x;
                int dy = e.getY() - lastDrag.y;
                panX += dx / zoom;
                panY += dy / zoom;
                lastDrag = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (tool == Tool.SELECT && SwingUtilities.isLeftMouseButton(e)) {
                    int mode = hitTestMode(e.getPoint());
                    if (mode > 0) {
                        selectedModeIndex = mode;
                        if (listener != null) {
                            listener.onSelectMode(mode);
                        }
                        repaint();
                    }
                } else if (tool == Tool.ANNOTATE && SwingUtilities.isLeftMouseButton(e)) {
                    Point2D w = screenToWorld(e.getPoint());
                    String text = JOptionPane.showInputDialog(CadViewportPanel.this, "Annotation text:", "Add Annotation", JOptionPane.PLAIN_MESSAGE);
                    if (text != null && !text.isBlank()) {
                        annotations.add(new Annotation(w.getX(), w.getY(), text.trim()));
                        repaint();
                    }
                }
                lastDrag = null;
            }
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(e -> {
            double factor = e.getPreciseWheelRotation() > 0 ? 0.90 : 1.10;
            Point p = e.getPoint();
            Point2D before = screenToWorld(p);
            zoom = clamp(zoom * factor, 0.2, 10.0);
            Point2D after = screenToWorld(p);
            panX += (after.getX() - before.getX());
            panY += (after.getY() - before.getY());
            repaint();
        });

        setFocusable(true);
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "reset_view");
        getActionMap().put("reset_view", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetView();
            }
        });
    }

    public void setViewportListener(ViewportListener listener) {
        this.listener = listener;
    }

    public void setModel(OmaResultsModel model) {
        this.model = model == null ? OmaResultsModel.loadFromDirOrEmpty(null) : model;
        if (listener != null) {
            listener.onValidationCount(this.model.issuesCount());
        }
        repaint();
    }

    public OmaResultsModel getModel() {
        return model;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType == null ? ViewType.FREQUENCY : viewType;
        repaint();
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setTool(Tool tool) {
        this.tool = tool == null ? Tool.SELECT : tool;
        setCursor(this.tool == Tool.PAN ? Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) : Cursor.getDefaultCursor());
    }

    public Tool getTool() {
        return tool;
    }

    public void setLayerVisibility(boolean showPoints, boolean showLabels, boolean showValidation) {
        this.showPoints = showPoints;
        this.showLabels = showLabels;
        this.showValidation = showValidation;
        repaint();
    }

    public void setLayerAlpha(float pointsAlpha, float labelsAlpha) {
        this.pointsAlpha = clamp(pointsAlpha, 0f, 1f);
        this.labelsAlpha = clamp(labelsAlpha, 0f, 1f);
        repaint();
    }

    public void setSelectedModeIndex(int modeIndex) {
        this.selectedModeIndex = modeIndex;
        repaint();
    }

    public int getSelectedModeIndex() {
        return selectedModeIndex;
    }

    public void resetView() {
        panX = 0;
        panY = 0;
        zoom = 1.0;
        repaint();
    }

    public double getZoom() {
        return zoom;
    }

    public List<Annotation> getAnnotations() {
        return List.copyOf(annotations);
    }

    private void showViewportContextMenu(int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem reset = new JMenuItem("Reset View");
        reset.addActionListener(e -> resetView());
        JMenuItem clearAnn = new JMenuItem("Clear Annotations");
        clearAnn.addActionListener(e -> {
            annotations.clear();
            repaint();
        });
        menu.add(reset);
        menu.addSeparator();
        menu.add(clearAnn);
        menu.show(this, x, y);
    }

    private int hitTestMode(Point p) {
        List<OmaResultsModel.ModeRow> rows = model.modes();
        if (rows.isEmpty()) {
            return -1;
        }
        double best = 14.0;
        int bestMode = -1;
        for (OmaResultsModel.ModeRow r : rows) {
            Point sp = worldToScreen(toWorldPoint(r));
            double d = sp.distance(p);
            if (d < best) {
                best = d;
                bestMode = r.modeIndex();
            }
        }
        return bestMode;
    }

    private Point2D toWorldPoint(OmaResultsModel.ModeRow r) {
        double x = r.modeIndex();
        double y = viewType == ViewType.FREQUENCY ? r.frequencyHz() : r.dampingRatio();
        return new Point2D.Double(x, y);
    }

    private AffineTransform worldToScreenTransform() {
        Insets in = getInsets();
        int w = Math.max(1, getWidth() - in.left - in.right);
        int h = Math.max(1, getHeight() - in.top - in.bottom);

        double margin = 60.0;
        double plotW = Math.max(1, w - margin * 2);
        double plotH = Math.max(1, h - margin * 2);

        double minX = 0.0;
        double maxX = 1.0;
        double minY = 0.0;
        double maxY = 1.0;

        List<OmaResultsModel.ModeRow> rows = model.modes();
        if (!rows.isEmpty()) {
            minX = rows.stream().mapToDouble(OmaResultsModel.ModeRow::modeIndex).min().orElse(0);
            maxX = rows.stream().mapToDouble(OmaResultsModel.ModeRow::modeIndex).max().orElse(1);
            if (viewType == ViewType.FREQUENCY) {
                minY = rows.stream().mapToDouble(OmaResultsModel.ModeRow::frequencyHz).min().orElse(0);
                maxY = rows.stream().mapToDouble(OmaResultsModel.ModeRow::frequencyHz).max().orElse(1);
            } else {
                minY = rows.stream().mapToDouble(OmaResultsModel.ModeRow::dampingRatio).min().orElse(0);
                maxY = rows.stream().mapToDouble(OmaResultsModel.ModeRow::dampingRatio).max().orElse(1);
            }
        }

        if (!Double.isFinite(minY) || !Double.isFinite(maxY) || minY == maxY) {
            minY = 0;
            maxY = 1;
        }
        if (minX == maxX) {
            minX -= 1;
            maxX += 1;
        }

        double xScale = plotW / (maxX - minX);
        double yScale = plotH / (maxY - minY);

        AffineTransform tx = new AffineTransform();
        tx.translate(in.left + margin, in.top + margin + plotH);
        tx.scale(xScale, -yScale);
        tx.translate(-minX, -minY);
        tx.translate(panX, panY);
        tx.scale(zoom, zoom);
        return tx;
    }

    private Point worldToScreen(Point2D world) {
        AffineTransform tx = worldToScreenTransform();
        Point2D out = tx.transform(world, null);
        return new Point((int) Math.round(out.getX()), (int) Math.round(out.getY()));
    }

    private Point2D screenToWorld(Point p) {
        try {
            AffineTransform tx = worldToScreenTransform();
            AffineTransform inv = tx.createInverse();
            return inv.transform(new Point2D.Double(p.x, p.y), null);
        } catch (Exception ex) {
            return new Point2D.Double(0, 0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Insets in = getInsets();
        int w = getWidth() - in.left - in.right;
        int h = getHeight() - in.top - in.bottom;

        g2.setColor(getBackground());
        g2.fillRect(in.left, in.top, w, h);

        drawGrid(g2);
        drawAxes(g2);
        if (showPoints) {
            drawSeries(g2);
        }
        if (showLabels) {
            drawLabels(g2);
        }
        drawAnnotations(g2);

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(40, 40, 46));
        int step = 40;
        for (int x = 0; x < getWidth(); x += step) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += step) {
            g2.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawAxes(Graphics2D g2) {
        g2.setColor(new Color(190, 190, 196));
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String yLabel = viewType == ViewType.FREQUENCY ? "Frequency (Hz)" : "Damping Ratio";
        String xLabel = "Mode Index";

        g2.drawString(xLabel, 12, getHeight() - 12);
        g2.drawString(yLabel, 12, 18);
    }

    private void drawSeries(Graphics2D g2) {
        List<OmaResultsModel.ModeRow> rows = model.modes();
        if (rows.isEmpty()) {
            g2.setColor(new Color(200, 200, 200));
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("No OMA results loaded.", 20, 40);
            return;
        }

        Color ok = new Color(64, 200, 255);
        Color warn = new Color(255, 190, 64);
        Color crit = new Color(255, 90, 90);
        Color line = new Color(120, 120, 140);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp(pointsAlpha, 0f, 1f)));

        Point prev = null;
        for (OmaResultsModel.ModeRow r : rows) {
            Point p = worldToScreen(toWorldPoint(r));
            if (prev != null) {
                g2.setColor(line);
                g2.drawLine(prev.x, prev.y, p.x, p.y);
            }
            prev = p;
        }

        for (OmaResultsModel.ModeRow r : rows) {
            Point p = worldToScreen(toWorldPoint(r));
            Color c = ok;
            if (showValidation) {
                if (r.severity() == OmaResultsModel.Severity.WARNING) {
                    c = warn;
                } else if (r.severity() == OmaResultsModel.Severity.CRITICAL) {
                    c = crit;
                }
            }
            g2.setColor(c);
            int size = (r.modeIndex() == selectedModeIndex) ? 10 : 7;
            g2.fillOval(p.x - size / 2, p.y - size / 2, size, size);
            if (r.modeIndex() == selectedModeIndex) {
                g2.setColor(Color.WHITE);
                g2.drawOval(p.x - size / 2 - 2, p.y - size / 2 - 2, size + 4, size + 4);
            }
        }

        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void drawLabels(Graphics2D g2) {
        List<OmaResultsModel.ModeRow> rows = model.modes();
        if (rows.isEmpty()) {
            return;
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp(labelsAlpha, 0f, 1f)));
        g2.setColor(new Color(230, 230, 235));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        for (OmaResultsModel.ModeRow r : rows) {
            Point p = worldToScreen(toWorldPoint(r));
            String label = viewType == ViewType.FREQUENCY ? formatHz(r.frequencyHz()) : formatRatio(r.dampingRatio());
            g2.drawString(label, p.x + 8, p.y - 8);
        }
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void drawAnnotations(Graphics2D g2) {
        if (annotations.isEmpty()) {
            return;
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        for (Annotation a : annotations) {
            Point p = worldToScreen(new Point2D.Double(a.x(), a.y()));
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillOval(p.x - 4, p.y - 4, 8, 8);
            g2.setColor(new Color(0, 0, 0, 140));
            int boxW = Math.min(260, 12 + g2.getFontMetrics().stringWidth(a.text()));
            int boxH = 22;
            int bx = p.x + 12;
            int by = p.y - boxH - 8;
            g2.fillRoundRect(bx, by, boxW, boxH, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawString(a.text(), bx + 6, by + 15);
            g2.drawLine(p.x, p.y, bx, by + boxH);
        }
    }

    private static String formatHz(double v) {
        if (!Double.isFinite(v)) {
            return "-";
        }
        if (v >= 100) {
            return String.format("%.1f Hz", v);
        }
        if (v >= 10) {
            return String.format("%.2f Hz", v);
        }
        return String.format("%.4f Hz", v);
    }

    private static String formatRatio(double v) {
        if (!Double.isFinite(v)) {
            return "-";
        }
        return String.format("%.4f", v);
    }

    private static float clamp(float v, float min, float max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    private static double clamp(double v, double min, double max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}
