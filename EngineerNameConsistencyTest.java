import javax.swing.*;
import java.awt.*;

public final class EngineerNameConsistencyTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            EngineerStartingPage start = new EngineerStartingPage();
            assertTrue(EngineerUiNames.ENGINEER_ACCOUNT_WINDOW_TITLE.equals(start.getTitle()), "EngineerStartingPage window title mismatch");
            JTabbedPane startTabs = findTabbedPane(start.getRootPane(), 3);
            assertTrue(startTabs != null, "EngineerStartingPage must contain a JTabbedPane");
            assertTrue(EngineerUiNames.TAB_PROJECTS.equals(startTabs.getTitleAt(0)), "EngineerStartingPage tab[0] mismatch");
            assertTrue(EngineerUiNames.TAB_VIEW.equals(startTabs.getTitleAt(1)), "EngineerStartingPage tab[1] mismatch");
            assertTrue(EngineerUiNames.TAB_HELP.equals(startTabs.getTitleAt(2)), "EngineerStartingPage tab[2] mismatch");
            start.dispose();

            EngineerDashboardUserSettings settings = new EngineerDashboardUserSettings();
            assertTrue(EngineerUiNames.windowTitle(EngineerUiNames.MENU_USER_SETTINGS).equals(settings.getTitle()), "EngineerDashboardUserSettings window title mismatch");
            JTabbedPane settingsTabs = findTabbedPane(settings.getRootPane(), 3);
            assertTrue(settingsTabs != null, "EngineerDashboardUserSettings must contain a JTabbedPane");
            assertTrue(EngineerUiNames.USER_SETTINGS_TAB_NAME_PHOTO.equals(settingsTabs.getTitleAt(0)), "UserSettings tab[0] mismatch");
            assertTrue(EngineerUiNames.USER_SETTINGS_TAB_EMAIL_PASSWORD.equals(settingsTabs.getTitleAt(1)), "UserSettings tab[1] mismatch");
            assertTrue(EngineerUiNames.USER_SETTINGS_TAB_NOTIFICATIONS.equals(settingsTabs.getTitleAt(2)), "UserSettings tab[2] mismatch");
            assertHasButtonText(settings.getContentPane(), EngineerUiNames.ACTION_SAVE_CHANGES_EXIT);
            settings.dispose();

            JPanel emailTab = new EngineerDashboardEmailPassword().createEmailPasswordTab();
            assertHasButtonText(emailTab, EngineerUiNames.ACTION_SAVE_CHANGES_EXIT);

            JPanel notifyTab = new EngineerDashboardNotifications().createNotificationsTab();
            assertHasButtonText(notifyTab, EngineerUiNames.ACTION_SAVE_CHANGES_EXIT);
        });
        System.out.println("ALL TESTS PASSED");
    }

    private static JTabbedPane findTabbedPane(Container c, int minTabs) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JTabbedPane tp) {
                if (tp.getTabCount() >= minTabs) {
                    return tp;
                }
            }
            if (child instanceof Container cc) {
                JTabbedPane found = findTabbedPane(cc, minTabs);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void assertHasButtonText(Container c, String text) {
        if (findButtonByText(c, text) == null) {
            throw new AssertionError("Expected a JButton with text: " + text);
        }
    }

    private static JButton findButtonByText(Container c, String text) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JButton b) {
                if (text != null && text.equals(b.getText())) {
                    return b;
                }
            }
            if (child instanceof Container cc) {
                JButton found = findButtonByText(cc, text);
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
