import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
 
public final class FddPlotPanel extends JPanel {
    private FddResult result;
    private String statusText = "No data.";
 
    private Double freqMinHz;
    private Double freqMaxHz;
 
    private final boolean[] lineVisible = new boolean[]{true, true, true, true};
    private Rectangle legendRect;
    private Rectangle[] legendItemRects;

    private List<FddPeak> peaks = List.of();
    private int selectedPeakBinIndex = -1;
    private String cursorText;

    public interface PeakSelectionListener {
        void onPeakBinSelected(int binIndex);
    }

    private volatile PeakSelectionListener peakSelectionListener;
 
    public FddPlotPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
 
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (legendItemRects != null) {
                    for (int i = 0; i < legendItemRects.length; i++) {
                        Rectangle r = legendItemRects[i];
                        if (r != null && r.contains(e.getPoint())) {
                            lineVisible[i] = !lineVisible[i];
                            repaint();
                            return;
                        }
                    }
                }

                int bin = findNearestPeakBinForPoint(e.getX(), e.getY());
                if (bin >= 0) {
                    selectedPeakBinIndex = bin;
                    PeakSelectionListener l = peakSelectionListener;
                    if (l != null) {
                        l.onPeakBinSelected(bin);
                    }
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateCursorText(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cursorText = null;
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
 
    public void setResult(FddResult result) {
        this.result = result;
        this.statusText = "";
        repaint();
    }

    public void setPeaks(List<FddPeak> peaks) {
        this.peaks = peaks == null ? List.of() : peaks;
        repaint();
    }

    public void setSelectedPeakBinIndex(int binIndex) {
        this.selectedPeakBinIndex = binIndex;
        repaint();
    }

    public void setPeakSelectionListener(PeakSelectionListener listener) {
        this.peakSelectionListener = listener;
    }
 
    public void setFrequencyBounds(Double minHz, Double maxHz) {
        if (minHz != null && maxHz != null && !(maxHz > minHz)) {
            return;
        }
        this.freqMinHz = minHz;
        this.freqMaxHz = maxHz;
        repaint();
    }
 
    public Double getFrequencyMinHz() {
        return freqMinHz;
    }
 
    public Double getFrequencyMaxHz() {
        return freqMaxHz;
    }
 
    boolean[] getLineVisibleForTesting() {
        return lineVisible;
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
 
            if (result == null || result.freqHz() == null || result.singularValuesDb() == null || result.freqHz().length == 0) {
                drawStatus(g2, w, h);
                return;
            }
 
            int left = 70;
            int right = 12;
            int top = 10;
            int bottom = 26;
            int pw = Math.max(1, w - left - right);
            int ph = Math.max(1, h - top - bottom);
 
            double[] freq = result.freqHz();
            double[][] sv = result.singularValuesDb();
 
            double fMin = 0.0;
            double fMax = freq[freq.length - 1];
            if (freqMinHz != null) {
                fMin = Math.max(0.0, freqMinHz);
            }
            if (freqMaxHz != null) {
                fMax = freqMaxHz;
            }
            fMax = Math.min(fMax, freq[freq.length - 1]);
            if (!(fMax > fMin)) {
                drawStatus(g2, w, h);
                return;
            }
 
            int start = 0;
            while (start < freq.length - 2 && freq[start] < fMin) {
                start++;
            }
            int end = freq.length - 1;
            while (end > 1 && freq[end] > fMax) {
                end--;
            }
            if (end <= start) {
                drawStatus(g2, w, h);
                return;
            }
 
            double vMin = -100.0;
            double vMax = 0.0;
 
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
 
            g2.setColor(new Color(235, 235, 235));
            int xTicks = 10;
            for (int i = 0; i <= xTicks; i++) {
                int x = left + (int) Math.round((i / (double) xTicks) * pw);
                g2.drawLine(x, top, x, top + ph);
            }
 
            for (int db = -100; db <= 0; db += 10) {
                int y = top + ph - (int) Math.round(((db - vMin) / (vMax - vMin)) * ph);
                g2.drawLine(left, y, left + pw, y);
                String lbl = db + " dB";
                int tx = Math.max(2, left - 8 - fm.stringWidth(lbl));
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(lbl, tx, y + fm.getAscent() / 2);
                g2.setColor(new Color(235, 235, 235));
            }
 
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(left, top, pw, ph);
 
            Color[] colors = new Color[]{
                    new Color(0, 80, 200),
                    new Color(220, 80, 80),
                    new Color(70, 160, 70),
                    new Color(140, 140, 140)
            };
            Stroke[] strokes = new Stroke[]{
                    new BasicStroke(2.0f),
                    new BasicStroke(1.6f),
                    new BasicStroke(1.6f),
                    new BasicStroke(1.2f)
            };
 
            int lines = Math.min(sv.length, colors.length);
            for (int li = 0; li < lines; li++) {
                if (!lineVisible[li]) {
                    continue;
                }
                g2.setColor(colors[li]);
                g2.setStroke(strokes[li]);
                int prevX = -1;
                int prevY = -1;
                boolean clippedLow = false;
                boolean clippedHigh = false;
                for (int i = start; i <= end; i++) {
                    double f = freq[i];
                    double v = sv[li][i];
                    if (!Double.isFinite(v)) {
                        continue;
                    }
                    int x = left + (int) Math.round(((f - fMin) / (fMax - fMin)) * pw);
                    double vv = v;
                    if (vv < vMin) {
                        vv = vMin;
                        clippedLow = true;
                    } else if (vv > vMax) {
                        vv = vMax;
                        clippedHigh = true;
                    }
                    int y = top + ph - (int) Math.round(((vv - vMin) / (vMax - vMin)) * ph);
                    if (prevX >= 0) {
                        g2.drawLine(prevX, prevY, x, y);
                    }
                    prevX = x;
                    prevY = y;
                }
                if (clippedHigh) {
                    drawClipMarker(g2, left + pw - 10, top + 8, true, colors[li]);
                }
                if (clippedLow) {
                    drawClipMarker(g2, left + pw - 10, top + ph - 8, false, colors[li]);
                }
            }
 
            g2.setColor(Color.DARK_GRAY);
            String xLabel = "Frequency (Hz)";
            int xLabelX = left + (pw - fm.stringWidth(xLabel)) / 2;
            g2.drawString(xLabel, Math.max(left, xLabelX), top + ph + 20);
 
            String yLabel = "Magnitude (dB)";
            Graphics2D gRot = (Graphics2D) g2.create();
            try {
                gRot.rotate(-Math.PI / 2);
                int yLabelX = -(top + (ph + fm.stringWidth(yLabel)) / 2);
                int yLabelY = 16;
                gRot.drawString(yLabel, yLabelX, yLabelY);
            } finally {
                gRot.dispose();
            }
 
            String f0 = formatHz(fMin);
            String f1 = formatHz(fMax);
            g2.drawString(f0, left, top + ph + 12);
            g2.drawString(f1, left + pw - fm.stringWidth(f1), top + ph + 12);
 
            drawLegend(g2, left, top, pw, ph, lines, colors, strokes, freq, sv, start, end, fMin, fMax, vMin, vMax);
            drawPeaks(g2, left, top, pw, ph, fMin, fMax);
            drawCursor(g2, left, top);
        } finally {
            g2.dispose();
        }
    }

    private void drawPeaks(Graphics2D g2, int left, int top, int pw, int ph, double fMin, double fMax) {
        if (peaks == null || peaks.isEmpty()) {
            return;
        }
        Stroke prev = g2.getStroke();
        for (FddPeak p : peaks) {
            double f = p.frequencyHz();
            if (!(f >= fMin && f <= fMax)) {
                continue;
            }
            int x = left + (int) Math.round(((f - fMin) / (fMax - fMin)) * pw);
            if (p.binIndex() == selectedPeakBinIndex) {
                g2.setColor(new Color(200, 0, 0, 180));
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(new Color(120, 120, 120, 140));
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawLine(x, top, x, top + ph);
        }
        g2.setStroke(prev);
    }

    private void drawCursor(Graphics2D g2, int left, int top) {
        if (cursorText == null || cursorText.isBlank()) {
            return;
        }
        g2.setFont(UiControlMetrics.CONTROL_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int pad = 6;
        int w = fm.stringWidth(cursorText) + pad * 2;
        int h = fm.getHeight() + pad;
        int bx = left + 6;
        int by = top + 6;
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRect(bx, by, w, h);
        g2.setColor(new Color(120, 120, 120, 200));
        g2.drawRect(bx, by, w, h);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(cursorText, bx + pad, by + pad + fm.getAscent());
    }

    private int findNearestPeakBinForPoint(int px, int py) {
        if (result == null || peaks == null || peaks.isEmpty()) {
            return -1;
        }
        int w = getWidth();
        int h = getHeight();
        int left = 70;
        int right = 12;
        int top = 10;
        int bottom = 26;
        int pw = Math.max(1, w - left - right);
        int ph = Math.max(1, h - top - bottom);
        Rectangle plot = new Rectangle(left, top, pw, ph);
        if (!plot.contains(px, py)) {
            return -1;
        }

        double[] freq = result.freqHz();
        if (freq == null || freq.length == 0) {
            return -1;
        }
        double fMin = 0.0;
        double fMax = freq[freq.length - 1];
        if (freqMinHz != null) {
            fMin = Math.max(0.0, freqMinHz);
        }
        if (freqMaxHz != null) {
            fMax = Math.min(freqMaxHz, freq[freq.length - 1]);
        }
        if (!(fMax > fMin)) {
            return -1;
        }

        double fx = fMin + ((px - left) / (double) pw) * (fMax - fMin);
        double bestDist = Double.POSITIVE_INFINITY;
        int bestBin = -1;
        double tolHz = Math.max(0.05, (fMax - fMin) * 0.01);
        for (FddPeak p : peaks) {
            double d = Math.abs(p.frequencyHz() - fx);
            if (d < bestDist) {
                bestDist = d;
                bestBin = p.binIndex();
            }
        }
        if (bestBin >= 0 && bestDist <= tolHz) {
            return bestBin;
        }
        return -1;
    }

    private void updateCursorText(int px, int py) {
        if (result == null || result.freqHz() == null || result.singularValuesDb() == null || result.singularValuesDb().length == 0) {
            if (cursorText != null) {
                cursorText = null;
                repaint();
            }
            return;
        }
        int w = getWidth();
        int h = getHeight();
        int left = 70;
        int right = 12;
        int top = 10;
        int bottom = 26;
        int pw = Math.max(1, w - left - right);
        int ph = Math.max(1, h - top - bottom);
        Rectangle plot = new Rectangle(left, top, pw, ph);
        if (!plot.contains(px, py)) {
            if (cursorText != null) {
                cursorText = null;
                repaint();
            }
            return;
        }

        double[] freq = result.freqHz();
        double[] svd1 = result.singularValuesDb()[0];
        double fMin = 0.0;
        double fMax = freq[freq.length - 1];
        if (freqMinHz != null) {
            fMin = Math.max(0.0, freqMinHz);
        }
        if (freqMaxHz != null) {
            fMax = Math.min(freqMaxHz, freq[freq.length - 1]);
        }
        if (!(fMax > fMin)) {
            return;
        }
        double fx = fMin + ((px - left) / (double) pw) * (fMax - fMin);
        int idx = nearestIndex(freq, fx);
        if (idx < 0 || idx >= svd1.length) {
            return;
        }
        double v = svd1[idx];
        if (!Double.isFinite(v)) {
            return;
        }
        String txt = "Cursor: " + formatHz(freq[idx]) + " Hz, " + String.format("%.2f", v) + " dB";
        if (!txt.equals(cursorText)) {
            cursorText = txt;
            repaint();
        }
    }

    private static int nearestIndex(double[] a, double x) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            double v = a[mid];
            if (v < x) {
                lo = mid + 1;
            } else if (v > x) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        if (lo <= 0) {
            return 0;
        }
        if (lo >= a.length) {
            return a.length - 1;
        }
        double d0 = Math.abs(a[lo - 1] - x);
        double d1 = Math.abs(a[lo] - x);
        return d0 <= d1 ? (lo - 1) : lo;
    }
 
    private void drawStatus(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(245, 245, 245));
        g2.fillRect(1, 1, w - 2, h - 2);
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        FontMetrics fm = g2.getFontMetrics();
        String text = statusText == null ? "" : statusText;
        int tx = Math.max(10, (w - fm.stringWidth(text)) / 2);
        int ty = (h + fm.getAscent()) / 2;
        g2.drawString(text, tx, ty);
    }
 
    private static String formatHz(double hz) {
        if (hz >= 100.0) {
            return String.format("%.0f", hz);
        }
        if (hz >= 10.0) {
            return String.format("%.1f", hz);
        }
        if (hz >= 1.0) {
            return String.format("%.2f", hz);
        }
        return String.format("%.3f", hz);
    }
 
    private static void drawClipMarker(Graphics2D g2, int x, int y, boolean up, Color c) {
        g2.setColor(c);
        Polygon p = new Polygon();
        if (up) {
            p.addPoint(x, y);
            p.addPoint(x - 6, y + 6);
            p.addPoint(x + 6, y + 6);
        } else {
            p.addPoint(x, y);
            p.addPoint(x - 6, y - 6);
            p.addPoint(x + 6, y - 6);
        }
        g2.fillPolygon(p);
        g2.setColor(Color.DARK_GRAY);
        g2.drawPolygon(p);
    }
 
    private void drawLegend(
            Graphics2D g2,
            int left,
            int top,
            int pw,
            int ph,
            int lines,
            Color[] colors,
            Stroke[] strokes,
            double[] freq,
            double[][] sv,
            int start,
            int end,
            double fMin,
            double fMax,
            double vMin,
            double vMax
    ) {
        Font font = new Font("Arial", Font.PLAIN, 11);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
 
        int pad = 8;
        int rowH = fm.getHeight() + 4;
        int sampleW = 22;
        int maxTextW = 0;
        String[] labels = new String[]{"SVD line 1", "SVD line 2", "SVD line 3", "SVD line 4"};
        for (int i = 0; i < lines; i++) {
            maxTextW = Math.max(maxTextW, fm.stringWidth(labels[i]));
        }
        int boxW = pad * 2 + sampleW + 8 + maxTextW;
        int boxH = pad * 2 + rowH * lines;
 
        Rectangle plot = new Rectangle(left, top, pw, ph);
        Rectangle[] candidates = new Rectangle[]{
                new Rectangle(plot.x + plot.width - boxW - 8, plot.y + 8, boxW, boxH),
                new Rectangle(plot.x + 8, plot.y + 8, boxW, boxH),
                new Rectangle(plot.x + plot.width - boxW - 8, plot.y + plot.height - boxH - 8, boxW, boxH),
                new Rectangle(plot.x + 8, plot.y + plot.height - boxH - 8, boxW, boxH)
        };
        Rectangle best = candidates[0];
        int bestScore = Integer.MAX_VALUE;
        int stride = Math.max(1, (end - start) / 120);
        for (Rectangle cand : candidates) {
            int score = 0;
            for (int li = 0; li < lines; li++) {
                if (!lineVisible[li]) {
                    continue;
                }
                for (int i = start; i <= end; i += stride) {
                    double f = freq[i];
                    double v = sv[li][i];
                    if (!Double.isFinite(v)) {
                        continue;
                    }
                    double vv = Math.max(vMin, Math.min(vMax, v));
                    int x = left + (int) Math.round(((f - fMin) / (fMax - fMin)) * pw);
                    int y = top + ph - (int) Math.round(((vv - vMin) / (vMax - vMin)) * ph);
                    if (cand.contains(x, y)) {
                        score++;
                    }
                }
            }
            if (score < bestScore) {
                bestScore = score;
                best = cand;
            }
        }
 
        legendRect = best;
        legendItemRects = new Rectangle[4];
 
        g2.setColor(new Color(255, 255, 255, 210));
        g2.fillRect(best.x, best.y, best.width, best.height);
        g2.setColor(new Color(120, 120, 120));
        g2.drawRect(best.x, best.y, best.width, best.height);
 
        int y = best.y + pad + fm.getAscent();
        for (int li = 0; li < lines; li++) {
            int rowYTop = best.y + pad + li * rowH;
            legendItemRects[li] = new Rectangle(best.x, rowYTop, best.width, rowH);
 
            int sx0 = best.x + pad;
            int sx1 = sx0 + sampleW;
            int sy = rowYTop + rowH / 2;
 
            g2.setStroke(strokes[li]);
            g2.setColor(colors[li]);
            if (lineVisible[li]) {
                g2.drawLine(sx0, sy, sx1, sy);
            } else {
                g2.setColor(new Color(170, 170, 170));
                g2.drawLine(sx0, sy, sx1, sy);
            }
 
            g2.setColor(Color.DARK_GRAY);
            String txt = labels[li];
            g2.drawString(txt, sx1 + 8, y);
            y += rowH;
        }
    }
}

