import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpectrogramGenerator {
    private static final Pattern NUMBER = Pattern.compile("[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?");
    private SpectrogramGenerator() {}

    public static BufferedImage generateFromCsv(File csv) throws IOException {
        return renderImage(generateDataFromCsv(csv));
    }

    public static SpectrogramData generateDataFromCsv(File csv) throws IOException {
        if (csv == null) {
            throw new IOException("No CSV selected for spectrogram generation.");
        }
        ParsedSignal parsed = readSignalFromCsv(csv);
        if (parsed.signal.length < 64) {
            throw new IOException("Not enough samples to generate a spectrogram.");
        }
        double fsHz = parsed.fsHz > 0 ? parsed.fsHz : 100.0;
        return stftToData(parsed.signal, fsHz);
    }

    public static SpectrogramData generateDataFromSignal(double[] signal, double fsHz) throws IOException {
        if (signal == null || signal.length < 64) {
            throw new IOException("Not enough samples to generate a spectrogram.");
        }
        double fs = fsHz > 0 ? fsHz : 100.0;
        return stftToData(signal, fs);
    }

    public static BufferedImage renderImage(SpectrogramData data) {
        int frames = data.timeFrames();
        int bins = data.frequencyBins();
        BufferedImage img = new BufferedImage(frames, bins, BufferedImage.TYPE_INT_RGB);

        double vmin = data.vminDb();
        double vmax = data.vmaxDb();
        if (!(vmax > vmin)) {
            vmin = -120;
            vmax = 0;
        }

        double[][] db = data.db();
        for (int k = 0; k < bins; k++) {
            for (int f = 0; f < frames; f++) {
                double v = db[k][f];
                if (!Double.isFinite(v)) {
                    v = vmin;
                }
                double t = (v - vmin) / (vmax - vmin);
                double tt = clamp01(t);
                tt = Math.pow(tt, 0.55);
                int rgb = ColorMap.rgb(tt);
                img.setRGB(f, bins - 1 - k, rgb);
            }
        }
        return img;
    }

    public static BufferedImage renderImageLegacyMinMax(SpectrogramData data) {
        int frames = data.timeFrames();
        int bins = data.frequencyBins();
        BufferedImage img = new BufferedImage(frames, bins, BufferedImage.TYPE_INT_RGB);

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double[][] db = data.db();
        for (int k = 0; k < bins; k++) {
            for (int f = 0; f < frames; f++) {
                double v = db[k][f];
                if (!Double.isFinite(v)) {
                    continue;
                }
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
        }
        if (!(max > min)) {
            min = -120;
            max = 0;
        }

        for (int k = 0; k < bins; k++) {
            for (int f = 0; f < frames; f++) {
                double v = db[k][f];
                if (!Double.isFinite(v)) {
                    v = min;
                }
                double t = (v - min) / (max - min);
                double tt = clamp01(t);
                tt = Math.pow(tt, 0.55);
                int rgb = ColorMap.rgb(tt);
                img.setRGB(f, bins - 1 - k, rgb);
            }
        }
        return img;
    }

    private record ParsedSignal(double[] signal, double fsHz) {}

    private static ParsedSignal readSignalFromCsv(File csv) throws IOException {
        List<double[]> rows = new ArrayList<>();
        List<Double> currentRecord = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                if (t.contains("####")) {
                    flushRecord(rows, currentRecord);
                    continue;
                }

                List<Double> nums = extractNumbers(t);
                if (nums.isEmpty()) {
                    continue;
                }

                if (nums.size() >= 2) {
                    flushRecord(rows, currentRecord);
                    rows.add(toArray(nums));
                    continue;
                }

                currentRecord.add(nums.get(0));
            }
        }
        flushRecord(rows, currentRecord);

        if (rows.isEmpty()) {
            return new ParsedSignal(new double[0], -1);
        }

        int ncolMax = 0;
        for (double[] r : rows) {
            ncolMax = Math.max(ncolMax, r.length);
        }

        int timeCol = detectMonotonicTimeColumn(rows, ncolMax);
        int sigCol = selectSignalColumn(rows, ncolMax, timeCol);

        int maxSamples = 30000;
        int count = Math.min(rows.size(), maxSamples);
        double[] signal = new double[count];
        double[] time = timeCol == 0 ? new double[count] : null;
        for (int i = 0; i < count; i++) {
            double[] r = rows.get(i);
            if (time != null && timeCol >= 0 && timeCol < r.length) {
                time[i] = r[timeCol];
            }
            signal[i] = sigCol < r.length ? r[sigCol] : r[0];
        }

        if (rows.size() > maxSamples) {
            int step = Math.max(1, rows.size() / maxSamples);
            int outN = Math.min(maxSamples, rows.size() / step);
            double[] down = new double[outN];
            double[] downT = timeCol >= 0 ? new double[outN] : null;
            int idx = 0;
            for (int i = 0; i < rows.size() && idx < outN; i += step) {
                double[] r = rows.get(i);
                down[idx] = sigCol < r.length ? r[sigCol] : r[0];
                if (downT != null && timeCol < r.length) {
                    downT[idx] = r[timeCol];
                }
                idx++;
            }
            signal = down;
            time = downT;
        }

        double fsHz = inferFsHz(time);
        return new ParsedSignal(signal, fsHz);
    }

    private static void flushRecord(List<double[]> rows, List<Double> currentRecord) {
        if (currentRecord.isEmpty()) {
            return;
        }
        if (currentRecord.size() >= 2) {
            rows.add(toArray(currentRecord));
        }
        currentRecord.clear();
    }

    private static List<Double> extractNumbers(String text) {
        ArrayList<Double> out = new ArrayList<>();
        Matcher m = NUMBER.matcher(text);
        while (m.find()) {
            String s = m.group();
            try {
                out.add(Double.parseDouble(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return out;
    }

    private static double[] toArray(List<Double> nums) {
        double[] arr = new double[nums.size()];
        for (int i = 0; i < nums.size(); i++) {
            arr[i] = nums.get(i);
        }
        return arr;
    }

    private static int detectMonotonicTimeColumn(List<double[]> rows, int ncolMax) {
        int colsToCheck = Math.min(ncolMax, 4);
        int sampleN = Math.min(rows.size(), 2000);
        for (int c = 0; c < colsToCheck; c++) {
            boolean ok = true;
            double prev = Double.NaN;
            boolean havePrev = false;
            for (int i = 0; i < sampleN; i++) {
                double[] r = rows.get(i);
                if (c >= r.length) {
                    ok = false;
                    break;
                }
                double v = r[c];
                if (!Double.isFinite(v)) {
                    ok = false;
                    break;
                }
                if (havePrev && !(v > prev)) {
                    ok = false;
                    break;
                }
                prev = v;
                havePrev = true;
            }
            if (ok) {
                return c;
            }
        }
        return -1;
    }

    private static int selectSignalColumn(List<double[]> rows, int ncolMax, int timeCol) {
        int cols = Math.min(ncolMax, 8);
        int sampleN = Math.min(rows.size(), 5000);
        double bestStd = -1;
        int bestCol = 0;
        for (int c = 0; c < cols; c++) {
            if (c == timeCol) {
                continue;
            }
            double mean = 0;
            double m2 = 0;
            int n = 0;
            for (int i = 0; i < sampleN; i++) {
                double[] r = rows.get(i);
                if (c >= r.length) {
                    continue;
                }
                double v = r[c];
                if (!Double.isFinite(v)) {
                    continue;
                }
                n++;
                double delta = v - mean;
                mean += delta / n;
                m2 += delta * (v - mean);
            }
            if (n < 10) {
                continue;
            }
            double var = m2 / Math.max(1, n - 1);
            double std = Math.sqrt(Math.max(0, var));
            if (std > bestStd) {
                bestStd = std;
                bestCol = c;
            }
        }
        if (bestStd <= 0 && timeCol == 0 && ncolMax >= 2) {
            return 1;
        }
        return bestCol;
    }

    private static double inferFsHz(double[] time) {
        if (time == null || time.length < 3) {
            return -1;
        }
        int n = Math.min(time.length, 2000);
        double[] dt = new double[n - 1];
        int m = 0;
        double prev = time[0];
        for (int i = 1; i < n; i++) {
            double t = time[i];
            double d = t - prev;
            if (Double.isFinite(d) && d > 0) {
                dt[m++] = d;
            }
            prev = t;
        }
        if (m < 2) {
            return -1;
        }
        Arrays.sort(dt, 0, m);
        double median = dt[m / 2];
        if (!(median > 0)) {
            return -1;
        }
        double fs = 1.0 / median;
        if (!Double.isFinite(fs) || fs <= 0) {
            return -1;
        }
        return fs;
    }

    private static SpectrogramData stftToData(double[] x, double fsHz) {
        int win = 512;
        while (win > x.length) {
            win /= 2;
        }
        win = Math.max(64, win);
        int hop = Math.max(1, win / 4);
        int nfft = nextPow2(win);
        int bins = nfft / 2;
        int frames = 1 + Math.max(0, (x.length - win) / hop);
        frames = Math.max(frames, 1);

        double[] window = hann(win);
        double[][] db = new double[bins][frames];

        double[] re = new double[nfft];
        double[] im = new double[nfft];

        for (int f = 0; f < frames; f++) {
            int start = f * hop;
            Arrays.fill(re, 0);
            Arrays.fill(im, 0);
            double mean = 0.0;
            for (int i = 0; i < win; i++) {
                mean += x[start + i];
            }
            mean /= win;
            for (int i = 0; i < win; i++) {
                re[i] = (x[start + i] - mean) * window[i];
            }
            fftInPlace(re, im);
            for (int k = 0; k < bins; k++) {
                double m = Math.sqrt(re[k] * re[k] + im[k] * im[k]) / win;
                double dbv = 20.0 * Math.log10(m + 1e-12);
                if (!Double.isFinite(dbv)) {
                    dbv = -300;
                }
                db[k][f] = dbv;
            }
        }

        double[] vminmax = robustMinMax(db);
        double vmin = vminmax[0];
        double vmax = vminmax[1];

        double durationSec = Math.max(0, (frames - 1) * (hop / fsHz));
        return new SpectrogramData(db, bins, frames, nfft, win, hop, fsHz, durationSec, vmin, vmax);
    }

    private static double[] robustMinMax(double[][] db) {
        int bins = db.length;
        int frames = db[0].length;
        int total = bins * frames;
        int maxSamples = Math.min(total, 120000);
        double[] sample = new double[maxSamples];
        int step = Math.max(1, total / maxSamples);
        int idx = 0;
        int flat = 0;
        while (idx < maxSamples && flat < total) {
            int k = flat / frames;
            int f = flat % frames;
            double v = db[k][f];
            if (Double.isFinite(v)) {
                sample[idx++] = v;
            }
            flat += step;
        }
        if (idx < 10) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (int k = 0; k < bins; k++) {
                for (int f = 0; f < frames; f++) {
                    double v = db[k][f];
                    if (!Double.isFinite(v)) {
                        continue;
                    }
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
            }
            if (!Double.isFinite(min) || !Double.isFinite(max)) {
                return new double[]{-120, 0};
            }
            if (!(max > min)) {
                return new double[]{min - 1.0, max + 1.0};
            }
            return new double[]{min, max};
        }
        Arrays.sort(sample, 0, idx);
        double p05 = sample[(int) Math.floor(0.05 * (idx - 1))];
        double p95 = sample[(int) Math.floor(0.95 * (idx - 1))];
        if (!Double.isFinite(p95)) {
            p95 = sample[idx - 1];
        }
        if (!Double.isFinite(p05)) {
            p05 = sample[0];
        }
        double vmin = p05;
        double vmax = p95;
        if (!(vmax > vmin)) {
            double min = sample[0];
            double max = sample[idx - 1];
            if (!(max > min)) {
                return new double[]{min - 1.0, max + 1.0};
            }
            return new double[]{min, max};
        }
        return new double[]{vmin, vmax};
    }

    private static double[] hann(int n) {
        double[] w = new double[n];
        for (int i = 0; i < n; i++) {
            w[i] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * i / (n - 1));
        }
        return w;
    }

    private static int nextPow2(int n) {
        int p = 1;
        while (p < n) {
            p <<= 1;
        }
        return p;
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }

    private static void fftInPlace(double[] re, double[] im) {
        int n = re.length;
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (i < j) {
                double tr = re[i];
                re[i] = re[j];
                re[j] = tr;
                double ti = im[i];
                im[i] = im[j];
                im[j] = ti;
            }
            int m = n >> 1;
            while (j >= m && m >= 2) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }

        for (int len = 2; len <= n; len <<= 1) {
            double ang = -2.0 * Math.PI / len;
            double wlenr = Math.cos(ang);
            double wleni = Math.sin(ang);
            for (int i = 0; i < n; i += len) {
                double wr = 1.0;
                double wi = 0.0;
                for (int k = 0; k < len / 2; k++) {
                    int u = i + k;
                    int v = i + k + len / 2;
                    double vr = re[v] * wr - im[v] * wi;
                    double vi = re[v] * wi + im[v] * wr;
                    re[v] = re[u] - vr;
                    im[v] = im[u] - vi;
                    re[u] += vr;
                    im[u] += vi;
                    double nwr = wr * wlenr - wi * wleni;
                    wi = wr * wleni + wi * wlenr;
                    wr = nwr;
                }
            }
        }
    }
}

