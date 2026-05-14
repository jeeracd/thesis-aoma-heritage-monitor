import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FddPeakAnalysis {
    private FddPeakAnalysis() {}

    public static List<FddPeak> pickPeaks(FddResult r, int maxPeaks, double minProminenceDb, double minSpacingHz) {
        if (r == null || r.freqHz() == null || r.singularValuesDb() == null || r.singularValuesDb().length == 0) {
            return List.of();
        }
        double[] f = r.freqHz();
        double[] y = r.singularValuesDb()[0];
        if (f.length < 3 || y.length != f.length) {
            return List.of();
        }

        ArrayList<Candidate> cands = new ArrayList<>();
        for (int i = 1; i < y.length - 1; i++) {
            double a = y[i - 1];
            double b = y[i];
            double c = y[i + 1];
            if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(c)) {
                continue;
            }
            if (!(b > a && b >= c)) {
                continue;
            }
            double prom = estimateProminenceDb(y, i);
            if (prom < minProminenceDb) {
                continue;
            }
            cands.add(new Candidate(i, b, prom));
        }

        cands.sort(Comparator.comparingDouble(Candidate::prominenceDb).reversed());

        ArrayList<Integer> chosen = new ArrayList<>();
        for (Candidate cand : cands) {
            if (chosen.size() >= Math.max(1, maxPeaks)) {
                break;
            }
            int idx = cand.index;
            double fi = f[idx];
            boolean ok = true;
            for (int j : chosen) {
                if (Math.abs(fi - f[j]) < minSpacingHz) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                chosen.add(idx);
            }
        }

        chosen.sort(Comparator.comparingDouble(i -> f[i]));

        ArrayList<FddPeak> peaks = new ArrayList<>();
        int pi = 1;
        for (int idx : chosen) {
            double bw = estimateHalfPowerBandwidthHz(f, y, idx, 3.0);
            double zetaPct = Double.NaN;
            if (Double.isFinite(bw) && bw > 0 && f[idx] > 0) {
                zetaPct = (bw / (2.0 * f[idx])) * 100.0;
            }
            peaks.add(new FddPeak(pi++, idx, f[idx], y[idx], bw, zetaPct));
        }
        return peaks;
    }

    private static double estimateProminenceDb(double[] y, int idx) {
        double peak = y[idx];

        double leftMin = peak;
        for (int i = idx; i >= 0; i--) {
            if (!Double.isFinite(y[i])) {
                break;
            }
            leftMin = Math.min(leftMin, y[i]);
            if (i > 0 && y[i - 1] > y[i]) {
                break;
            }
        }

        double rightMin = peak;
        for (int i = idx; i < y.length; i++) {
            if (!Double.isFinite(y[i])) {
                break;
            }
            rightMin = Math.min(rightMin, y[i]);
            if (i < y.length - 1 && y[i + 1] > y[i]) {
                break;
            }
        }

        double base = Math.max(leftMin, rightMin);
        return peak - base;
    }

    private static double estimateHalfPowerBandwidthHz(double[] f, double[] y, int idx, double dropDb) {
        double peakDb = y[idx];
        double target = peakDb - dropDb;
        double left = findCrossing(f, y, idx, -1, target);
        double right = findCrossing(f, y, idx, +1, target);
        if (!Double.isFinite(left) || !Double.isFinite(right) || !(right > left)) {
            return Double.NaN;
        }
        return right - left;
    }

    private static double findCrossing(double[] f, double[] y, int startIdx, int dir, double targetDb) {
        int i = startIdx;
        while (true) {
            int j = i + dir;
            if (j < 0 || j >= y.length) {
                return Double.NaN;
            }
            double yi = y[i];
            double yj = y[j];
            if (!Double.isFinite(yi) || !Double.isFinite(yj)) {
                return Double.NaN;
            }
            if ((yi >= targetDb && yj <= targetDb) || (yi <= targetDb && yj >= targetDb)) {
                double fi = f[i];
                double fj = f[j];
                if (!(fj > fi) && !(fi > fj)) {
                    return fi;
                }
                double t = (targetDb - yi) / (yj - yi);
                if (!Double.isFinite(t)) {
                    return Double.NaN;
                }
                t = Math.max(0, Math.min(1, t));
                return fi + t * (fj - fi);
            }
            i = j;
        }
    }

    private record Candidate(int index, double valueDb, double prominenceDb) {}
}

