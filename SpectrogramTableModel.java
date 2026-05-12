import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public final class SpectrogramTableModel extends AbstractTableModel {
    public enum AmplitudeScale { DB, LINEAR }

    private SpectrogramData data;
    private SpectrogramViewWindow window;

    private AmplitudeScale amplitudeScale = AmplitudeScale.DB;
    private int decimals = 3;
    private Double minValue;
    private Double maxValue;
    private boolean anomaliesOnly;
    private String searchText = "";

    private int frameStart;
    private int frameEnd;
    private int binStart;
    private int binEnd;

    private long[] indexMap;

    public void setSpectrogram(SpectrogramData data) {
        this.data = data;
        this.window = SpectrogramViewWindow.full(data);
        rebuild();
    }

    public void setViewWindow(SpectrogramViewWindow window) {
        this.window = window;
        rebuild();
    }

    public void setDecimals(int decimals) {
        this.decimals = Math.max(0, Math.min(9, decimals));
        fireTableDataChanged();
    }

    public void setAmplitudeScale(AmplitudeScale scale) {
        this.amplitudeScale = scale == null ? AmplitudeScale.DB : scale;
        fireTableDataChanged();
    }

    public void setValueFilter(Double minValue, Double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        rebuild();
    }

    public void setAnomaliesOnly(boolean anomaliesOnly) {
        this.anomaliesOnly = anomaliesOnly;
        rebuild();
    }

    public void setSearchText(String text) {
        this.searchText = text == null ? "" : text.trim().toLowerCase();
        rebuild();
    }

    public SpectrogramData getSpectrogram() {
        return data;
    }

    public SpectrogramViewWindow getViewWindow() {
        return window;
    }

    @Override
    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        if (indexMap != null) {
            return indexMap.length;
        }
        int frames = Math.max(0, frameEnd - frameStart);
        int bins = Math.max(0, binEnd - binStart);
        return frames * bins;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Time";
            case 1 -> "Frequency";
            case 2 -> amplitudeScale == AmplitudeScale.DB ? "Amplitude (dB)" : "Amplitude (linear)";
            case 3 -> "Flag";
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data == null) {
            return "";
        }
        Cell cell = cellForRow(rowIndex);
        if (cell == null) {
            return "";
        }
        double tSec = cell.timeSec;
        double fHz = cell.freqHz;
        double db = cell.db;
        String flag = cell.flag;

        return switch (columnIndex) {
            case 0 -> formatTime(tSec);
            case 1 -> formatFreq(fHz);
            case 2 -> formatAmp(db);
            case 3 -> flag;
            default -> "";
        };
    }

    public double getTimeSecAtRow(int rowIndex) {
        Cell c = cellForRow(rowIndex);
        return c == null ? 0 : c.timeSec;
    }

    public double getFreqHzAtRow(int rowIndex) {
        Cell c = cellForRow(rowIndex);
        return c == null ? 0 : c.freqHz;
    }

    public double getDbAtRow(int rowIndex) {
        Cell c = cellForRow(rowIndex);
        return c == null ? Double.NaN : c.db;
    }

    public String getFlagAtRow(int rowIndex) {
        Cell c = cellForRow(rowIndex);
        return c == null ? "" : c.flag;
    }

    private void rebuild() {
        if (data == null) {
            indexMap = null;
            fireTableDataChanged();
            return;
        }

        SpectrogramViewWindow w = window == null ? SpectrogramViewWindow.full(data) : window;
        double fullT = Math.max(0, data.durationSec());
        double maxF = Math.max(0, data.fsHz() / 2.0);

        double ts = clamp(w.timeStartSec(), 0, fullT);
        double te = clamp(w.timeEndSec(), 0, fullT);
        double fs = clamp(w.freqStartHz(), 0, maxF);
        double fe = clamp(w.freqEndHz(), 0, maxF);

        int frames = data.timeFrames();
        int bins = data.frequencyBins();

        frameStart = timeToFrame(ts, frames, fullT);
        frameEnd = Math.max(frameStart + 1, timeToFrame(te, frames, fullT) + 1);
        frameEnd = Math.min(frameEnd, frames);

        binStart = freqToBin(fs, bins, maxF);
        binEnd = Math.max(binStart + 1, freqToBin(fe, bins, maxF) + 1);
        binEnd = Math.min(binEnd, bins);

        boolean needsIndex = minValue != null || maxValue != null || anomaliesOnly || !searchText.isEmpty();
        if (!needsIndex) {
            indexMap = null;
            fireTableDataChanged();
            return;
        }

        ArrayList<Long> idx = new ArrayList<>();
        for (int k = binStart; k < binEnd; k++) {
            for (int f = frameStart; f < frameEnd; f++) {
                double v = data.db()[k][f];
                if (!Double.isFinite(v)) {
                    String flag = flagFor(v, data);
                    if (accept(v, flag)) {
                        idx.add(idxPacked(k, f));
                    }
                    continue;
                }
                String flag = flagFor(v, data);
                if (accept(v, flag)) {
                    idx.add(idxPacked(k, f));
                }
            }
        }
        indexMap = new long[idx.size()];
        for (int i = 0; i < idx.size(); i++) {
            indexMap[i] = idx.get(i);
        }
        fireTableDataChanged();
    }

    private boolean accept(double db, String flag) {
        if (anomaliesOnly && "OK".equals(flag)) {
            return false;
        }
        if (minValue != null && db < minValue) {
            return false;
        }
        if (maxValue != null && db > maxValue) {
            return false;
        }
        if (!searchText.isEmpty()) {
            if (!flag.toLowerCase().contains(searchText)) {
                return false;
            }
        }
        return true;
    }

    private Cell cellForRow(int rowIndex) {
        if (data == null || rowIndex < 0) {
            return null;
        }
        int frames = data.timeFrames();
        int bins = data.frequencyBins();
        if (frames <= 0 || bins <= 0) {
            return null;
        }

        long packed;
        if (indexMap != null) {
            if (rowIndex >= indexMap.length) {
                return null;
            }
            packed = indexMap[rowIndex];
        } else {
            int framesIn = frameEnd - frameStart;
            if (framesIn <= 0) {
                return null;
            }
            int local = rowIndex;
            int localBin = local / framesIn;
            int localFrame = local % framesIn;
            int k = binStart + localBin;
            int f = frameStart + localFrame;
            if (k < 0 || k >= bins || f < 0 || f >= frames) {
                return null;
            }
            packed = idxPacked(k, f);
        }

        int k = (int) (packed >> 32);
        int f = (int) (packed & 0xFFFFFFFFL);
        if (k < 0 || k >= bins || f < 0 || f >= frames) {
            return null;
        }

        double fullT = Math.max(0, data.durationSec());
        double maxF = Math.max(0, data.fsHz() / 2.0);

        double tSec = frames <= 1 ? 0 : (f / (double) (frames - 1)) * fullT;
        double fHz = bins <= 1 ? 0 : (k / (double) (bins - 1)) * maxF;

        double v = data.db()[k][f];
        String flag = flagFor(v, data);

        return new Cell(tSec, fHz, v, flag);
    }

    private static String flagFor(double db, SpectrogramData data) {
        if (!Double.isFinite(db)) {
            return "NonFinite";
        }
        double vmin = data.vminDb();
        double vmax = data.vmaxDb();
        if (db <= vmin + 1.0) {
            return "Floor";
        }
        if (db >= vmax - 1.0) {
            return "Ceiling";
        }
        return "OK";
    }

    private String formatTime(double sec) {
        if (sec < 1.0) {
            return format(sec * 1000.0) + " ms";
        }
        return format(sec) + " s";
    }

    private String formatFreq(double hz) {
        if (hz >= 1000.0) {
            return format(hz / 1000.0) + " kHz";
        }
        return format(hz) + " Hz";
    }

    private String formatAmp(double db) {
        if (!Double.isFinite(db)) {
            return "";
        }
        if (amplitudeScale == AmplitudeScale.DB) {
            return format(db);
        }
        double lin = Math.pow(10.0, db / 20.0);
        return format(lin);
    }

    private String format(double v) {
        return String.format("%." + decimals + "f", v);
    }

    private static int timeToFrame(double tSec, int frames, double fullT) {
        if (frames <= 1 || fullT <= 0) {
            return 0;
        }
        double u = clamp(tSec / fullT, 0, 1);
        return (int) Math.floor(u * (frames - 1));
    }

    private static int freqToBin(double fHz, int bins, double maxF) {
        if (bins <= 1 || maxF <= 0) {
            return 0;
        }
        double u = clamp(fHz / maxF, 0, 1);
        return (int) Math.floor(u * (bins - 1));
    }

    private static long idxPacked(int bin, int frame) {
        return (((long) bin) << 32) | (frame & 0xFFFFFFFFL);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static final class Cell {
        final double timeSec;
        final double freqHz;
        final double db;
        final String flag;

        Cell(double timeSec, double freqHz, double db, String flag) {
            this.timeSec = timeSec;
            this.freqHz = freqHz;
            this.db = db;
            this.flag = flag;
        }
    }
}

