public final class EngineerNotificationLoadTest {
    public static void main(String[] args) {
        EngineerPreferences.setNotifyEmail(false);
        EngineerNotificationCenter center = EngineerNotificationCenter.get();

        long t0 = System.nanoTime();
        int n = 20_000;
        for (int i = 0; i < n; i++) {
            center.push(EngineerNotificationCenter.Severity.INFO, "Load " + i, "m");
        }
        long t1 = System.nanoTime();

        int size = center.snapshot().size();
        assertTrue(size <= 200, "Feed must be capped to 200 items");

        double ms = (t1 - t0) / 1_000_000.0;
        System.out.println("PUSHED=" + n + " TIME_MS=" + String.format(java.util.Locale.US, "%.1f", ms));
        System.out.println("ALL TESTS PASSED");
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

