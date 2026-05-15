import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class ModeTimelinePanel extends JPanel {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private long[] tEpochMillis = new long[0];
    private int[] mode = new int[0];
    private boolean[] anomaly = new boolean[0];
    private String[] event = new String[0];
    private int selectedIndex = -1;
    private int hoverIndex = -1;

    private long viewStart = Long.MIN_VALUE;
    private long viewEnd = Long.MIN_VALUE;
    private int dragStartX;
    private long dragViewStart;
    private long dragViewEnd;

    private static final Color[] MODE_COLORS = new Color[] {
            new Color(148, 163, 184),
            new Color(34, 197, 94),
            new Color(59, 130, 246),
            new Color(168, 85, 247),
            new Color(249, 115, 22),
            new Color(14, 165, 233),
            new Color(99, 102, 241),
            new Color(220, 38, 38)
    };

    public ModeTimelinePanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverIndex = findNearestIndexForX(e.getX());
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragViewStart = viewStart;
                dragViewEnd = viewEnd;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (tEpochMillis.length < 2 || viewStart == Long.MIN_VALUE || viewEnd == Long.MIN_VALUE) {
                    return;
                }
                int w = getWidth();
                int left = 60;
                int right = 14;
                int pw = Math.max(1, w - left - right);
                long span = Math.max(1L, dragViewEnd - dragViewStart);
                double dx = (e.getX() - dragStartX) / (double) pw;
                long shift = (long) (-dx * span);
                setViewWindow(dragViewStart + shift, dragViewEnd + shift);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (tEpochMillis.length < 2) {
                    return;
                }
                int rot = e.getWheelRotation();
                if (rot == 0) {
                    return;
                }
                ensureDefaultWindow();
                long span = Math.max(1L, viewEnd - viewStart);
                long center = viewStart + (span / 2);
                double factor = rot > 0 ? 1.25 : 0.80;
                long newSpan = (long) Math.max(60_000L, Math.min((tEpochMillis[tEpochMillis.length - 1] - tEpochMillis[0]), span * factor));
                setViewWindow(center - newSpan / 2, center + newSpan / 2);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    public void setTimeline(long[] tEpochMillis, int[] mode, boolean[] anomaly, String[] event) {
        this.tEpochMillis = tEpochMillis == null ? new long[0] : tEpochMillis;
        this.mode = mode == null ? new int[0] : mode;
        this.anomaly = anomaly == null ? new boolean[0] : anomaly;
        this.event = event == null ? new String[0] : event;
        this.selectedIndex = -1;
        this.hoverIndex = -1;
        this.viewStart = Long.MIN_VALUE;
        this.viewEnd = Long.MIN_VALUE;
        repaint();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        repaint();
    }

    private void ensureDefaultWindow() {
        if (tEpochMillis.length < 2) {
            return;
        }
        if (viewStart != Long.MIN_VALUE && viewEnd != Long.MIN_VALUE) {
            return;
        }
        viewStart = tEpochMillis[0];
        viewEnd = tEpochMillis[tEpochMillis.length - 1];
    }

    private void setViewWindow(long start, long end) {
        if (tEpochMillis.length < 2) {
            return;
        }
        long min = tEpochMillis[0];
        long max = tEpochMillis[tEpochMillis.length - 1];
        long span = Math.max(60_000L, end - start);
        long s = start;
        long e = start + span;
        if (s < min) {
            long d = min - s;
            s += d;
            e += d;
        }
        if (e > max) {
            long d = e - max;
            s -= d;
            e -= d;
        }
        s = Math.max(min, s);
        e = Math.min(max, e);
        if (e <= s) {
            return;
        }
        viewStart = s;
        viewEnd = e;
        repaint();
    }

    private int findNearestIndexForX(int x) {
        if (tEpochMillis.length == 0) {
            return -1;
        }
        ensureDefaultWindow();
        int w = getWidth();
        int left = 60;
        int right = 14;
        int pw = Math.max(1, w - left - right);
        double p = (x - left) / (double) pw;
        p = Math.max(0.0, Math.min(1.0, p));
        long t = viewStart + (long) ((viewEnd - viewStart) * p);
        int lo = 0;
        int hi = tEpochMillis.length - 1;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (tEpochMillis[mid] < t) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        int idx = lo;
        if (idx > 0) {
            long a = Math.abs(tEpochMillis[idx] - t);
            long b = Math.abs(tEpochMillis[idx - 1] - t);
            if (b <= a) {
                idx = idx - 1;
            }
        }
        return idx;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            if (tEpochMillis.length == 0 || mode.length == 0) {
                g2.setColor(new Color(90, 90, 90));
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString("No timeline data.", 12, 20);
                return;
            }

            ensureDefaultWindow();

            int left = 60;
            int right = 14;
            int top = 12;
            int bottom = 18;
            int pw = Math.max(1, w - left - right);
            int ph = Math.max(1, h - top - bottom);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(left, top, pw, ph);

            int n = Math.min(tEpochMillis.length, mode.length);
            int prevX = -1;
            int prevMode = -1;
            int segStartX = -1;
            for (int i = 0; i < n; i++) {
                long t = tEpochMillis[i];
                if (t < viewStart || t > viewEnd) {
                    continue;
                }
                int x = left + (int) Math.round(((t - viewStart) / (double) (viewEnd - viewStart)) * pw);
                int m = mode[i];
                if (prevX < 0) {
                    segStartX = x;
                    prevX = x;
                    prevMode = m;
                    continue;
                }
                if (m != prevMode) {
                    fillSeg(g2, segStartX, top + 1, x - segStartX, ph - 1, prevMode);
                    segStartX = x;
                    prevMode = m;
                }
                prevX = x;
            }
            if (segStartX >= 0) {
                fillSeg(g2, segStartX, top + 1, (left + pw) - segStartX, ph - 1, prevMode);
            }

            g2.setStroke(new BasicStroke(1.0f));
            for (int i = 0; i < n; i++) {
                long t = tEpochMillis[i];
                if (t < viewStart || t > viewEnd) {
                    continue;
                }
                boolean a = i < anomaly.length && anomaly[i];
                String ev = i < event.length ? event[i] : "";
                if (!a && (ev == null || ev.isBlank() || "NONE".equalsIgnoreCase(ev.trim()))) {
                    continue;
                }
                int x = left + (int) Math.round(((t - viewStart) / (double) (viewEnd - viewStart)) * pw);
                if (a) {
                    g2.setColor(new Color(220, 38, 38));
                } else {
                    g2.setColor(new Color(180, 120, 10));
                }
                g2.drawLine(x, top, x, top + ph);
            }

            drawMarker(g2, left, top, pw, ph, selectedIndex, new Color(20, 184, 166));
            drawMarker(g2, left, top, pw, ph, hoverIndex, new Color(148, 163, 184));

            if (hoverIndex >= 0 && hoverIndex < tEpochMillis.length && hoverIndex < mode.length) {
                long t = tEpochMillis[hoverIndex];
                if (t >= viewStart && t <= viewEnd) {
                    String label = TS.format(Instant.ofEpochMilli(t)) + "  mode=" + mode[hoverIndex];
                    drawTooltip(g2, label, left + 6, top + 6);
                }
            }
        } finally {
            g2.dispose();
        }
    }

    private void fillSeg(Graphics2D g2, int x, int y, int w, int h, int mode) {
        if (w <= 0 || h <= 0) {
            return;
        }
        Color c = modeColor(mode);
        g2.setColor(c);
        g2.fillRect(x, y, w, h);
    }

    private static Color modeColor(int m) {
        if (m <= 0) {
            return MODE_COLORS[0];
        }
        int idx = Math.min(MODE_COLORS.length - 1, Math.max(0, m - 1));
        return MODE_COLORS[idx];
    }

    private void drawMarker(Graphics2D g2, int left, int top, int pw, int ph, int idx, Color c) {
        if (idx < 0 || idx >= tEpochMillis.length) {
            return;
        }
        long t = tEpochMillis[idx];
        if (viewStart != Long.MIN_VALUE && viewEnd != Long.MIN_VALUE) {
            if (t < viewStart || t > viewEnd) {
                return;
            }
        }
        int x = left + (int) Math.round(((t - viewStart) / (double) (viewEnd - viewStart)) * pw);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(x, top, x, top + ph);
    }

    private void drawTooltip(Graphics2D g2, String text, int x, int y) {
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text) + 10;
        int h = fm.getHeight() + 6;
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fillRoundRect(x, y, w, h, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x + 5, y + 4 + fm.getAscent());
    }
}

