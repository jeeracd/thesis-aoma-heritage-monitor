import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public final class EngineerProfileSyncTest {
    public static void main(String[] args) throws Exception {
        EngineerProfileStore.setName("Juan", "Dela Cruz");

        EngineerStartingPage[] startRef = new EngineerStartingPage[1];
        EngineerDashboardUserSettings[] settingsRef = new EngineerDashboardUserSettings[1];

        SwingUtilities.invokeAndWait(() -> {
            startRef[0] = new EngineerStartingPage();
            settingsRef[0] = new EngineerDashboardUserSettings();
        });

        EngineerStartingPage start = startRef[0];
        EngineerDashboardUserSettings settings = settingsRef[0];

        JTextField first = findTitledField(settings.getRootPane(), "First Name");
        JTextField last = findTitledField(settings.getRootPane(), "Last Name");
        assertTrue(first != null, "First Name field not found");
        assertTrue(last != null, "Last Name field not found");

        SwingUtilities.invokeAndWait(() -> {
            first.setText("Maria");
            last.setText("Reyes");
        });

        Thread.sleep(900);

        SwingUtilities.invokeAndWait(() -> {
            JLabel greeting = findGreetingLabel(start.getRootPane());
            assertTrue(greeting != null, "Greeting label not found");
            assertTrue(greeting.getText().contains("Maria Reyes"), "StartingPage greeting did not update from UserSettings");
        });

        EngineerProfileStore.setName("Ana", "Santos");
        Thread.sleep(150);

        SwingUtilities.invokeAndWait(() -> {
            assertTrue("Ana".equals(first.getText()), "UserSettings first name did not update from store");
            assertTrue("Santos".equals(last.getText()), "UserSettings last name did not update from store");
        });

        SwingUtilities.invokeAndWait(() -> {
            settings.dispose();
            start.dispose();
        });

        System.out.println("ALL TESTS PASSED");
    }

    private static JTextField findTitledField(Container c, String title) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JTextField tf) {
                if (tf.getBorder() instanceof TitledBorder tb) {
                    if (title != null && title.equals(tb.getTitle())) {
                        return tf;
                    }
                }
            }
            if (child instanceof Container cc) {
                JTextField found = findTitledField(cc, title);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static JLabel findGreetingLabel(Container c) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JLabel l) {
                String t = l.getText();
                if (t != null && t.startsWith("Welcome, Engr.")) {
                    return l;
                }
            }
            if (child instanceof Container cc) {
                JLabel found = findGreetingLabel(cc);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

