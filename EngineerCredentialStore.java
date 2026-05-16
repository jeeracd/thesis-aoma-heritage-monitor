import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EngineerCredentialStore {
    private static final Logger LOG = Logger.getLogger(EngineerCredentialStore.class.getName());
    private static final Object LOCK = new Object();
    private static final List<Runnable> listeners = new ArrayList<>();

    private static volatile String email = normalizeEmail(EngineerPreferences.getEmail());
    private static volatile String passwordHash = EngineerPreferences.getPasswordHash();
    private static volatile long version = 1;

    static {
        if (email.isBlank()) {
            email = normalizeEmail("JuanDelaCruz@engr.com");
            EngineerPreferences.setEmail(email);
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            passwordHash = PasswordHasher.hash("dummypassword123".toCharArray());
            EngineerPreferences.setPasswordHash(passwordHash);
        }
    }

    private EngineerCredentialStore() {}

    public static String getEmail() {
        return email;
    }

    public static long getVersion() {
        return version;
    }

    public static boolean verifyCredentials(String inputEmail, char[] inputPassword) {
        String e = normalizeEmail(inputEmail);
        if (!isValidEmail(e)) {
            return false;
        }
        String curEmail = email;
        String curHash = passwordHash;
        if (!e.equalsIgnoreCase(curEmail)) {
            return false;
        }
        return PasswordHasher.verify(inputPassword, curHash);
    }

    public static boolean updateEmail(String newEmail, long expectedVersion) {
        String e = normalizeEmail(newEmail);
        if (!isValidEmail(e)) {
            return false;
        }
        String oldEmail;
        synchronized (LOCK) {
            if (expectedVersion != version) {
                return false;
            }
            oldEmail = email;
            if (e.equalsIgnoreCase(oldEmail)) {
                return true;
            }
            try {
                EngineerPreferences.setEmail(e);
                email = e;
                version++;
                LOG.log(Level.INFO, "Engineer credential email updated.");
            } catch (Exception ex) {
                try {
                    EngineerPreferences.setEmail(oldEmail);
                } catch (Exception ignored) {
                }
                LOG.log(Level.WARNING, "Engineer credential email update failed.", ex);
                return false;
            }
        }
        UserStore.updateEmail(oldEmail, e);
        notifyListeners();
        return true;
    }

    public static boolean updatePassword(char[] newPassword, long expectedVersion) {
        if (!isValidPassword(newPassword)) {
            return false;
        }
        String currentEmail;
        synchronized (LOCK) {
            if (expectedVersion != version) {
                return false;
            }
            currentEmail = email;
            String oldHash = passwordHash;
            String newHash = PasswordHasher.hash(newPassword);
            try {
                EngineerPreferences.setPasswordHash(newHash);
                passwordHash = newHash;
                version++;
                LOG.log(Level.INFO, "Engineer credential password updated.");
            } catch (Exception ex) {
                try {
                    EngineerPreferences.setPasswordHash(oldHash);
                } catch (Exception ignored) {
                }
                LOG.log(Level.WARNING, "Engineer credential password update failed.", ex);
                return false;
            }
        }
        UserStore.updatePassword(currentEmail, newPassword);
        notifyListeners();
        return true;
    }

    public static boolean updateCredentials(String newEmail, char[] newPassword, long expectedVersion) {
        String e = normalizeEmail(newEmail);
        if (!isValidEmail(e) || !isValidPassword(newPassword)) {
            return false;
        }
        String oldEmail;
        synchronized (LOCK) {
            if (expectedVersion != version) {
                return false;
            }
            oldEmail = email;
            String oldHash = passwordHash;
            String newHash = PasswordHasher.hash(newPassword);
            try {
                EngineerPreferences.setEmail(e);
                EngineerPreferences.setPasswordHash(newHash);
                email = e;
                passwordHash = newHash;
                version++;
                LOG.log(Level.INFO, "Engineer credentials updated.");
            } catch (Exception ex) {
                try {
                    EngineerPreferences.setEmail(oldEmail);
                    EngineerPreferences.setPasswordHash(oldHash);
                } catch (Exception ignored) {
                }
                LOG.log(Level.WARNING, "Engineer credential update failed.", ex);
                return false;
            }
        }
        UserStore.updateEmail(oldEmail, e);
        UserStore.updatePassword(e, newPassword);
        notifyListeners();
        return true;
    }

    public static Runnable addListener(Runnable listener) {
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

    private static void notifyListeners() {
        List<Runnable> snapshot;
        synchronized (LOCK) {
            snapshot = new ArrayList<>(listeners);
        }
        for (Runnable r : snapshot) {
            try {
                if (SwingUtilities.isEventDispatchThread()) {
                    r.run();
                } else {
                    SwingUtilities.invokeLater(r);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String s = email.trim();
        if (s.length() < 6 || s.length() > 254) {
            return false;
        }
        return s.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public static boolean isValidPassword(char[] pass) {
        if (pass == null) {
            return false;
        }
        if (pass.length < 6) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : pass) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        return hasLetter && hasDigit;
    }

    private static String normalizeEmail(String v) {
        if (v == null) {
            return "";
        }
        return v.trim();
    }
}

