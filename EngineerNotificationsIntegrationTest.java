public final class EngineerNotificationsIntegrationTest {
    public static void main(String[] args) {
        EngineerPreferences.setNotifyInApp(true);
        EngineerPreferences.setNotifyEmail(false);
        EngineerPreferences.setNotificationFrequency(EngineerPreferences.NotificationFrequency.RIGHT_AWAY);

        EngineerNotificationCenter center = EngineerNotificationCenter.get();
        int before = center.snapshot().size();
        center.push(EngineerNotificationCenter.Severity.INFO, "Integration Test", "Hello");
        int after = center.snapshot().size();
        assertTrue(after == before + 1, "Notification feed should grow");

        center.markAllRead();
        for (EngineerNotificationCenter.Notification n : center.snapshot()) {
            assertTrue(n.isRead(), "All notifications should be marked read");
        }

        System.out.println("ALL TESTS PASSED");
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

