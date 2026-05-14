import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public final class FddGenerator {
    private static final Pattern NUMBER = Pattern.compile("[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?");
 
    private FddGenerator() {}
 
    public static FddResult generateFromCsv(File csv) throws IOException {
        if (csv == null) {
            throw new IOException("No CSV provided.");
        }
        CsvFileValidator.ValidationResult vr = CsvFileValidator.validate(csv);
        if (!vr.valid()) {
            throw new IOException(vr.message());
        }
 
        ParsedRows parsed = readNumericRows(csv, 40000);
        if (parsed.rows.isEmpty()) {
            throw new IOException("No numeric data found.");
        }
 
        int ncolMax = 0;
        for (double[] r : parsed.rows) {
            ncolMax = Math.max(ncolMax, r.length);
        }
        if (ncolMax <= 0) {
            throw new IOException("No numeric columns found.");
        }
 
        int timeCol = detectMonotonicTimeColumn(parsed.rows, ncolMax);
        int[] channels = selectChannelColumns(parsed.rows, ncolMax, timeCol, 4);
        if (channels.length == 0) {
            channels = new int[]{0};
        }

        String[] channelLabels = inferChannelLabels(csv, ncolMax, timeCol, channels);
 
        int n = parsed.rows.size();
        double[][] sig = new double[channels.length][n];
        double[] time = timeCol >= 0 ? new double[n] : null;
        for (int i = 0; i < n; i++) {
            double[] r = parsed.rows.get(i);
            if (time != null && timeCol < r.length) {
                time[i] = r[timeCol];
            }
            for (int c = 0; c < channels.length; c++) {
                int col = channels[c];
                sig[c][i] = col < r.length ? r[col] : 0.0;
            }
        }
 
        double fsHz = inferFsHz(time);
        if (!(fsHz > 0)) {
            fsHz = 100.0;
        }
        return computeSingularValues(sig, fsHz, channelLabels);
    }
 
    public static FddResult generateFromSignals(double[][] signals, double fsHz) throws IOException {
        if (signals == null || signals.length == 0) {
            throw new IOException("No signals provided.");
        }
        int n = signals[0] == null ? 0 : signals[0].length;
        if (n < 128) {
            throw new IOException("Not enough samples for FDD.");
        }
        for (double[] s : signals) {
            if (s == null || s.length != n) {
                throw new IOException("All signals must have the same length.");
            }
        }
        double fs = fsHz > 0 ? fsHz : 100.0;
        String[] labels = new String[signals.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = "Ch" + (i + 1);
        }
        return computeSingularValues(signals, fs, labels);
    }
 
    private static FddResult computeSingularValues(double[][] x, double fsHz, String[] channelLabels) {
        int ch = x.length;
        int n = x[0].length;
 
        int win = 2048;
        while (win > n) {
            win /= 2;
        }
        win = Math.max(256, win);
        int hop = Math.max(1, win / 2);
        int nfft = nextPow2(win);
        int bins = nfft / 2;
        int frames = 1 + Math.max(0, (n - win) / hop);
        frames = Math.max(1, frames);
 
        double[] window = hann(win);
 
        double[][][] re = new double[ch][ch][bins];
        double[][][] im = new double[ch][ch][bins];
 
        double[][] xr = new double[ch][nfft];
        double[][] xi = new double[ch][nfft];
 
        for (int f = 0; f < frames; f++) {
            int start = f * hop;
            for (int c = 0; c < ch; c++) {
                Arrays.fill(xr[c], 0);
                Arrays.fill(xi[c], 0);
                double mean = 0.0;
                for (int i = 0; i < win; i++) {
                    mean += x[c][start + i];
                }
                mean /= win;
                for (int i = 0; i < win; i++) {
                    xr[c][i] = (x[c][start + i] - mean) * window[i];
                }
                fftInPlace(xr[c], xi[c]);
            }
 
            for (int k = 0; k < bins; k++) {
                for (int i = 0; i < ch; i++) {
                    for (int j = i; j < ch; j++) {
                        double ar = xr[i][k];
                        double ai = xi[i][k];
                        double br = xr[j][k];
                        double bi = xi[j][k];
                        double pr = ar * br + ai * bi;
                        double pi = ai * br - ar * bi;
                        re[i][j][k] += pr;
                        im[i][j][k] += pi;
                        if (i != j) {
                            re[j][i][k] += pr;
                            im[j][i][k] -= pi;
                        }
                    }
                }
            }
        }
 
        double[] freq = new double[bins];
        for (int k = 0; k < bins; k++) {
            freq[k] = (k * fsHz) / nfft;
        }
 
        int lines = Math.min(ch, 4);
        double[][] svDb = new double[lines][bins];
        double[][] modeShape = new double[bins][ch];
        double eps = 1e-12;
        for (int k = 0; k < bins; k++) {
            double[][] m = new double[ch][ch];
            for (int i = 0; i < ch; i++) {
                for (int j = 0; j < ch; j++) {
                    double rr = re[i][j][k] / frames;
                    double ii = im[i][j][k] / frames;
                    m[i][j] = Math.hypot(rr, ii);
                }
            }
            double[] evals = jacobiEigenvalues(m);
            for (int i = 0; i < lines; i++) {
                double v = Math.max(eps, evals[i]);
                svDb[i][k] = 10.0 * Math.log10(v);
            }

            double[] v = principalEigenvector(m);
            double max = 0;
            for (int i = 0; i < v.length; i++) {
                max = Math.max(max, Math.abs(v[i]));
            }
            if (!(max > 0)) {
                max = 1.0;
            }
            for (int i = 0; i < ch; i++) {
                modeShape[k][i] = Math.abs(v[i]) / max;
            }
        }

        String[] labels = channelLabels;
        if (labels == null || labels.length != ch) {
            labels = new String[ch];
            for (int i = 0; i < ch; i++) {
                labels[i] = "Ch" + (i + 1);
            }
        }

        return new FddResult(freq, svDb, labels, modeShape);
    }
 
    private record ParsedRows(List<double[]> rows) {}
 
    private static ParsedRows readNumericRows(File csv, int maxSamples) throws IOException {
        ArrayList<double[]> rows = new ArrayList<>();
        ArrayList<Double> record = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                if (t.contains("####")) {
                    flushRecord(rows, record);
                    continue;
                }
                List<Double> nums = extractNumbers(t);
                if (nums.isEmpty()) {
                    continue;
                }
                if (nums.size() >= 2) {
                    flushRecord(rows, record);
                    rows.add(toArray(nums));
                } else {
                    record.add(nums.get(0));
                }
                if (rows.size() >= maxSamples) {
                    break;
                }
            }
        }
        flushRecord(rows, record);
        return new ParsedRows(rows);
    }
 
    private static void flushRecord(List<double[]> rows, List<Double> record) {
        if (record.isEmpty()) {
            return;
        }
        if (record.size() >= 2) {
            rows.add(toArray(record));
        }
        record.clear();
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
 
    private static int[] selectChannelColumns(List<double[]> rows, int ncolMax, int timeCol, int maxChannels) {
        int cols = Math.min(ncolMax, 8);
        int sampleN = Math.min(rows.size(), 5000);
        ArrayList<ColScore> scores = new ArrayList<>();
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
            if (std > 0) {
                scores.add(new ColScore(c, std));
            }
        }
        scores.sort(Comparator.comparingDouble(ColScore::std).reversed());
        int k = Math.min(maxChannels, scores.size());
        int[] out = new int[k];
        for (int i = 0; i < k; i++) {
            out[i] = scores.get(i).col;
        }
        if (out.length == 0 && timeCol == 0 && ncolMax >= 2) {
            return new int[]{1};
        }
        return out;
    }
 
    private record ColScore(int col, double std) {}
 
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
 
    private static double[] jacobiEigenvalues(double[][] a) {
        int n = a.length;
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, m[i], 0, n);
        }
 
        for (int iter = 0; iter < 50; iter++) {
            int p = 0;
            int q = 1;
            double max = 0;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double v = Math.abs(m[i][j]);
                    if (v > max) {
                        max = v;
                        p = i;
                        q = j;
                    }
                }
            }
            if (max < 1e-12) {
                break;
            }
            double app = m[p][p];
            double aqq = m[q][q];
            double apq = m[p][q];
            double phi = 0.5 * Math.atan2(2.0 * apq, (aqq - app));
            double c = Math.cos(phi);
            double s = Math.sin(phi);
 
            for (int i = 0; i < n; i++) {
                if (i == p || i == q) {
                    continue;
                }
                double mip = m[i][p];
                double miq = m[i][q];
                m[i][p] = c * mip - s * miq;
                m[p][i] = m[i][p];
                m[i][q] = s * mip + c * miq;
                m[q][i] = m[i][q];
            }
 
            double app2 = c * c * app - 2.0 * s * c * apq + s * s * aqq;
            double aqq2 = s * s * app + 2.0 * s * c * apq + c * c * aqq;
            m[p][p] = app2;
            m[q][q] = aqq2;
            m[p][q] = 0.0;
            m[q][p] = 0.0;
        }
 
        double[] eval = new double[n];
        for (int i = 0; i < n; i++) {
            eval[i] = m[i][i];
        }
        Arrays.sort(eval);
        for (int i = 0; i < n / 2; i++) {
            double t = eval[i];
            eval[i] = eval[n - 1 - i];
            eval[n - 1 - i] = t;
        }
        return eval;
    }

    private static double[] principalEigenvector(double[][] a) {
        int n = a.length;
        double[] v = new double[n];
        Arrays.fill(v, 1.0 / Math.sqrt(n));
        double[] y = new double[n];
        for (int iter = 0; iter < 30; iter++) {
            Arrays.fill(y, 0.0);
            for (int i = 0; i < n; i++) {
                double s = 0;
                for (int j = 0; j < n; j++) {
                    s += a[i][j] * v[j];
                }
                y[i] = s;
            }
            double norm = 0;
            for (int i = 0; i < n; i++) {
                norm += y[i] * y[i];
            }
            norm = Math.sqrt(norm);
            if (!(norm > 0) || !Double.isFinite(norm)) {
                break;
            }
            for (int i = 0; i < n; i++) {
                v[i] = y[i] / norm;
            }
        }
        return v;
    }

    private static String[] inferChannelLabels(File csv, int ncolMax, int timeCol, int[] channels) {
        try {
            CsvModalParameters mp = CsvModalParametersGenerator.generate(csv, 2000);
            if (mp == null || mp.fields() == null || mp.fields().isEmpty()) {
                return defaultLabels(channels.length);
            }
            ArrayList<String> headers = new ArrayList<>();
            for (CsvModalParameters.Field f : mp.fields()) {
                headers.add(f.csvHeader());
            }

            if (headers.size() == ncolMax + 1 && looksLikeTime(headers.get(0))) {
                headers = new ArrayList<>(headers.subList(1, headers.size()));
            }

            String[] labels = new String[channels.length];
            for (int i = 0; i < channels.length; i++) {
                int col = channels[i];
                String name;
                if (col >= 0 && col < headers.size()) {
                    name = headers.get(col);
                } else {
                    name = "Ch" + (i + 1);
                }
                labels[i] = name == null || name.isBlank() ? ("Ch" + (i + 1)) : name.trim();
            }
            if (timeCol >= 0) {
                boolean anyTime = false;
                for (String s : labels) {
                    if (looksLikeTime(s)) {
                        anyTime = true;
                        break;
                    }
                }
                if (anyTime) {
                    for (int i = 0; i < labels.length; i++) {
                        if (looksLikeTime(labels[i])) {
                            labels[i] = "Ch" + (i + 1);
                        }
                    }
                }
            }
            return labels;
        } catch (Exception ex) {
            return defaultLabels(channels.length);
        }
    }

    private static boolean looksLikeTime(String s) {
        if (s == null) {
            return false;
        }
        String t = s.trim().toLowerCase();
        return t.contains("time") || t.contains("timestamp");
    }

    private static String[] defaultLabels(int n) {
        String[] labels = new String[n];
        for (int i = 0; i < n; i++) {
            labels[i] = "Ch" + (i + 1);
        }
        return labels;
    }
}

