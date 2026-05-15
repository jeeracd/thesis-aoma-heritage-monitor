import java.util.ArrayList;
import java.util.List;

public final class EngineerNotificationRouter {
    private static final EngineerNotificationRouter INSTANCE = new EngineerNotificationRouter(new DesktopEmailService());
    private static final Object LOCK = new Object();

    private final EmailService emailService;
    private final List<EngineerNotificationCenter.Notification> pendingDigest = new ArrayList<>();
    private volatile boolean started = false;
    private Runnable removeListener = () -> {};

    private EngineerNotificationRouter(EmailService emailService) {
        this.emailService = emailService;
    }

    public static EngineerNotificationRouter get() {
        INSTANCE.start();
        return INSTANCE;
    }

    public void start() {
        if (started) {
            return;
        }
        synchronized (LOCK) {
            if (started) {
                return;
            }
            started = true;
            removeListener = EngineerNotificationCenter.get().addListener(this::route);
        }
    }

    public void stop() {
        synchronized (LOCK) {
            removeListener.run();
            removeListener = () -> {};
            pendingDigest.clear();
            started = false;
        }
    }

    public int getPendingDigestCount() {
        synchronized (LOCK) {
            return pendingDigest.size();
        }
    }

    public boolean sendDigestNow() {
        String to = EngineerPreferences.getEmail();
        if (to == null || to.isBlank() || !EngineerPreferences.isNotifyEmail()) {
            synchronized (LOCK) {
                pendingDigest.clear();
            }
            return false;
        }

        List<EngineerNotificationCenter.Notification> items;
        synchronized (LOCK) {
            items = new ArrayList<>(pendingDigest);
            pendingDigest.clear();
        }
        if (items.isEmpty()) {
            return false;
        }

        StringBuilder body = new StringBuilder();
        for (EngineerNotificationCenter.Notification n : items) {
            body.append("- ").append(n.getSeverity().name()).append(": ").append(n.getTitle());
            if (!n.getMessage().isBlank()) {
                body.append(" (").append(n.getMessage()).append(")");
            }
            body.append("\n");
        }

        return emailService.send(to, "AOMA-Heritage Monitor - Daily Notification Digest", body.toString());
    }

    private void route(EngineerNotificationCenter.Notification n) {
        if (n == null) {
            return;
        }
        if (!EngineerPreferences.isNotifyEmail()) {
            return;
        }
        EngineerPreferences.NotificationFrequency freq = EngineerPreferences.getNotificationFrequency();
        if (freq == EngineerPreferences.NotificationFrequency.NEVER) {
            return;
        }

        boolean urgent = n.getSeverity() == EngineerNotificationCenter.Severity.ALERT;
        if (urgent || freq == EngineerPreferences.NotificationFrequency.RIGHT_AWAY) {
            String to = EngineerPreferences.getEmail();
            if (to == null || to.isBlank()) {
                return;
            }
            emailService.send(to, "AOMA-Heritage Monitor - " + n.getTitle(), n.getMessage());
            return;
        }

        synchronized (LOCK) {
            pendingDigest.add(n);
            if (pendingDigest.size() > 1000) {
                pendingDigest.remove(0);
            }
        }
    }
}

