import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class EngineerPreferences {
    public enum AccessLevel {
        VIEWER,
        EDITOR,
        OWNER
    }

    public enum NotificationFrequency {
        RIGHT_AWAY,
        ONCE_A_DAY,
        NEVER
    }

    private static final Preferences P = Preferences.userRoot().node("aoma-heritage-monitor/engineer");

    private static final String KEY_FIRST_NAME = "profile.firstName";
    private static final String KEY_LAST_NAME = "profile.lastName";
    private static final String KEY_PHOTO_PATH = "profile.photoPath";
    private static final String KEY_ACCESS_LEVEL = "access.level";

    private static final String KEY_EMAIL = "auth.email";
    private static final String KEY_PASSWORD_HASH = "auth.passwordHash";

    private static final String KEY_NOTIFY_FREQUENCY = "notify.frequency";
    private static final String KEY_NOTIFY_IN_APP = "notify.route.inApp";
    private static final String KEY_NOTIFY_EMAIL = "notify.route.email";
    private static final String KEY_ALERT_VIBRATION_RMS = "notify.threshold.vibrationRms";
    private static final String KEY_ALERT_FREQ_SHIFT = "notify.threshold.frequencyShift";

    private EngineerPreferences() {}

    public static String getFirstName() {
        return P.get(KEY_FIRST_NAME, "Juan");
    }

    public static void setFirstName(String firstName) {
        P.put(KEY_FIRST_NAME, firstName == null ? "" : firstName.trim());
    }

    public static String getLastName() {
        return P.get(KEY_LAST_NAME, "Dela Cruz");
    }

    public static void setLastName(String lastName) {
        P.put(KEY_LAST_NAME, lastName == null ? "" : lastName.trim());
    }

    public static String getPhotoPath() {
        return P.get(KEY_PHOTO_PATH, "");
    }

    public static void setPhotoPath(String photoPath) {
        P.put(KEY_PHOTO_PATH, photoPath == null ? "" : photoPath.trim());
    }

    public static AccessLevel getAccessLevel() {
        String v = P.get(KEY_ACCESS_LEVEL, AccessLevel.OWNER.name());
        try {
            return AccessLevel.valueOf(v);
        } catch (Exception ex) {
            return AccessLevel.OWNER;
        }
    }

    public static void setAccessLevel(AccessLevel level) {
        P.put(KEY_ACCESS_LEVEL, (level == null ? AccessLevel.VIEWER : level).name());
    }

    public static String getEmail() {
        return P.get(KEY_EMAIL, "JuanDelaCruz@engr.com");
    }

    public static void setEmail(String email) {
        P.put(KEY_EMAIL, email == null ? "" : email.trim());
    }

    public static String getPasswordHash() {
        return P.get(KEY_PASSWORD_HASH, "");
    }

    public static void setPasswordHash(String passwordHash) {
        P.put(KEY_PASSWORD_HASH, passwordHash == null ? "" : passwordHash.trim());
    }

    public static NotificationFrequency getNotificationFrequency() {
        String v = P.get(KEY_NOTIFY_FREQUENCY, NotificationFrequency.RIGHT_AWAY.name());
        try {
            return NotificationFrequency.valueOf(v);
        } catch (Exception ex) {
            return NotificationFrequency.RIGHT_AWAY;
        }
    }

    public static void setNotificationFrequency(NotificationFrequency freq) {
        P.put(KEY_NOTIFY_FREQUENCY, (freq == null ? NotificationFrequency.RIGHT_AWAY : freq).name());
    }

    public static boolean isNotifyInApp() {
        return P.getBoolean(KEY_NOTIFY_IN_APP, true);
    }

    public static void setNotifyInApp(boolean enabled) {
        P.putBoolean(KEY_NOTIFY_IN_APP, enabled);
    }

    public static boolean isNotifyEmail() {
        return P.getBoolean(KEY_NOTIFY_EMAIL, false);
    }

    public static void setNotifyEmail(boolean enabled) {
        P.putBoolean(KEY_NOTIFY_EMAIL, enabled);
    }

    public static double getVibrationRmsThreshold() {
        return P.getDouble(KEY_ALERT_VIBRATION_RMS, 0.50);
    }

    public static void setVibrationRmsThreshold(double v) {
        P.putDouble(KEY_ALERT_VIBRATION_RMS, v);
    }

    public static double getFrequencyShiftThreshold() {
        return P.getDouble(KEY_ALERT_FREQ_SHIFT, 0.10);
    }

    public static void setFrequencyShiftThreshold(double v) {
        P.putDouble(KEY_ALERT_FREQ_SHIFT, v);
    }

    public static void resetAll() {
        try {
            String[] keys = P.keys();
            for (String k : keys) {
                P.remove(k);
            }
            P.flush();
        } catch (BackingStoreException ignored) {
        }
    }
}
