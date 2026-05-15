import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class EngineerNotificationCenter {
    public enum Severity {
        INFO,
        WARNING,
        ALERT
    }

    public static final class Notification {
        private final long id;
        private final long createdAtMillis;
        private final Severity severity;
        private final String title;
        private final String message;
        private volatile boolean read;

        Notification(long id, long createdAtMillis, Severity severity, String title, String message) {
            this.id = id;
            this.createdAtMillis = createdAtMillis;
            this.severity = severity == null ? Severity.INFO : severity;
            this.title = title == null ? "" : title;
            this.message = message == null ? "" : message;
        }

        public long getId() { return id; }
        public long getCreatedAtMillis() { return createdAtMillis; }
        public Severity getSeverity() { return severity; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }

        @Override
        public String toString() {
            return title;
        }
    }

    private static final EngineerNotificationCenter INSTANCE = new EngineerNotificationCenter();
    private static final Object LOCK = new Object();

    private final List<Notification> feed = new ArrayList<>();
    private final List<Consumer<Notification>> listeners = new ArrayList<>();
    private final Random random = new Random();
    private volatile long nextId = 1;
    private volatile boolean started = false;
    private ScheduledExecutorService executor;
    private Runnable removeCsvListener = () -> {};

    private EngineerNotificationCenter() {}

    public static EngineerNotificationCenter get() {
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
            executor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "EngineerNotificationCenter");
                t.setDaemon(true);
                return t;
            });
            removeCsvListener = AppSession.addLastUploadedCsvListener(() -> push(Severity.INFO, "Sensor Data Imported", "New sensor data was imported."));
            executor.scheduleAtFixedRate(this::simulateAlertTick, 8, 10, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        synchronized (LOCK) {
            if (executor != null) {
                executor.shutdownNow();
                executor = null;
            }
            removeCsvListener.run();
            removeCsvListener = () -> {};
            started = false;
        }
    }

    public Runnable addListener(Consumer<Notification> listener) {
        if (listener == null) {
            return () -> {};
        }
        synchronized (LOCK) {
            listeners.add(listener);
        }
        return () -> {
            synchronized (LOCK) {
                listeners.remove(listener);
            }
        };
    }

    public List<Notification> snapshot() {
        synchronized (LOCK) {
            return new ArrayList<>(feed);
        }
    }

    public Notification push(Severity severity, String title, String message) {
        Notification n;
        synchronized (LOCK) {
            n = new Notification(nextId++, System.currentTimeMillis(), severity, title, message);
            feed.add(0, n);
            if (feed.size() > 200) {
                feed.remove(feed.size() - 1);
            }
        }
        notifyListeners(n);
        return n;
    }

    public void markAllRead() {
        synchronized (LOCK) {
            for (Notification n : feed) {
                n.setRead(true);
            }
        }
    }

    private void notifyListeners(Notification n) {
        List<Consumer<Notification>> snapshot;
        synchronized (LOCK) {
            snapshot = new ArrayList<>(listeners);
        }
        for (Consumer<Notification> l : snapshot) {
            try {
                l.accept(n);
            } catch (Exception ignored) {
            }
        }
    }

    private void simulateAlertTick() {
        double vibrationRms = random.nextDouble();
        double freqShift = random.nextDouble() * 0.20;

        double vibThreshold = EngineerPreferences.getVibrationRmsThreshold();
        double shiftThreshold = EngineerPreferences.getFrequencyShiftThreshold();

        if (vibrationRms >= vibThreshold) {
            push(Severity.ALERT, "Vibration Threshold Exceeded", "RMS=" + format2(vibrationRms) + " (threshold " + format2(vibThreshold) + ")");
        }
        if (freqShift >= shiftThreshold) {
            push(Severity.WARNING, "Frequency Shift Detected", "Shift=" + format2(freqShift) + " (threshold " + format2(shiftThreshold) + ")");
        }
    }

    private static String format2(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }
}

