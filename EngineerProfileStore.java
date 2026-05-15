import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public final class EngineerProfileStore {
    private static final Object LOCK = new Object();
    private static final List<Runnable> listeners = new ArrayList<>();

    private static volatile String firstName = EngineerPreferences.getFirstName();
    private static volatile String lastName = EngineerPreferences.getLastName();

    private EngineerProfileStore() {}

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getFullName() {
        String f = firstName == null ? "" : firstName.trim();
        String l = lastName == null ? "" : lastName.trim();
        String out = (f + " " + l).trim();
        return out.isBlank() ? "Engineer" : out;
    }

    public static boolean setName(String newFirstName, String newLastName) {
        String f = newFirstName == null ? "" : newFirstName.trim();
        String l = newLastName == null ? "" : newLastName.trim();
        if (!isValidName(f) || !isValidName(l)) {
            return false;
        }

        boolean changed = false;
        synchronized (LOCK) {
            if (!f.equals(firstName)) {
                firstName = f;
                EngineerPreferences.setFirstName(f);
                changed = true;
            }
            if (!l.equals(lastName)) {
                lastName = l;
                EngineerPreferences.setLastName(l);
                changed = true;
            }
        }
        if (changed) {
            notifyListeners();
        }
        return true;
    }

    public static boolean setFirstName(String newFirstName) {
        return setName(newFirstName, lastName);
    }

    public static boolean setLastName(String newLastName) {
        return setName(firstName, newLastName);
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

    public static boolean isValidName(String v) {
        if (v == null) {
            return false;
        }
        String s = v.trim();
        if (s.length() < 2 || s.length() > 50) {
            return false;
        }
        return s.matches("[A-Za-z][A-Za-z .'-]*");
    }
}

