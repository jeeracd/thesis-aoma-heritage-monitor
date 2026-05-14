public final class FddGeneratorTest {
    public static void main(String[] args) throws Exception {
        testPeaksNearKnownFrequencies();
        System.out.println("ALL TESTS PASSED");
    }
 
    private static void testPeaksNearKnownFrequencies() throws Exception {
        double fs = 100.0;
        int n = 8192;
        double[][] x = new double[3][n];
        for (int i = 0; i < n; i++) {
            double t = i / fs;
            double s = Math.sin(2.0 * Math.PI * 2.0 * t) + 0.6 * Math.sin(2.0 * Math.PI * 6.0 * t);
            x[0][i] = s;
            x[1][i] = Math.sin(2.0 * Math.PI * 2.0 * t + 0.7) + 0.6 * Math.sin(2.0 * Math.PI * 6.0 * t - 0.2);
            x[2][i] = 0.2 * Math.sin(2.0 * Math.PI * 6.0 * t + 1.1);
        }
 
        FddResult r = FddGenerator.generateFromSignals(x, fs);
        assertTrue(r.freqHz().length > 10, "freq axis expected");
        assertTrue(r.singularValuesDb().length >= 1, "at least one singular value line expected");
 
        int[] top = topKIndices(r.singularValuesDb()[0], 20, 1);
        boolean has2 = hasPeakNear(r.freqHz(), top, 2.0, 0.25);
        boolean has6 = hasPeakNear(r.freqHz(), top, 6.0, 0.25);
        assertTrue(has2, "expected a strong peak near 2 Hz");
        assertTrue(has6, "expected a strong peak near 6 Hz");
    }
 
    private static int[] topKIndices(double[] v, int k, int startIndex) {
        int n = v.length;
        int kk = Math.min(k, Math.max(0, n - startIndex));
        int[] idx = new int[kk];
        double[] val = new double[kk];
        for (int i = 0; i < kk; i++) {
            idx[i] = startIndex;
            val[i] = Double.NEGATIVE_INFINITY;
        }
        for (int i = startIndex; i < n; i++) {
            double x = v[i];
            if (!Double.isFinite(x)) {
                continue;
            }
            int minPos = 0;
            for (int j = 1; j < kk; j++) {
                if (val[j] < val[minPos]) {
                    minPos = j;
                }
            }
            if (x > val[minPos]) {
                val[minPos] = x;
                idx[minPos] = i;
            }
        }
        return idx;
    }
 
    private static boolean hasPeakNear(double[] freq, int[] indices, double targetHz, double tolHz) {
        for (int i : indices) {
            if (i < 0 || i >= freq.length) {
                continue;
            }
            if (Math.abs(freq[i] - targetHz) <= tolHz) {
                return true;
            }
        }
        return false;
    }
 
    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

