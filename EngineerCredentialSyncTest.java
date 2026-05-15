import javax.swing.*;
import java.awt.*;

public final class EngineerCredentialSyncTest {
    public static void main(String[] args) throws Exception {
        JFrame[] frameRef = new JFrame[1];
        JPanel[] tabRef = new JPanel[1];
        JLabel[] emailLabelRef = new JLabel[1];
        String[] initialRef = new String[1];

        SwingUtilities.invokeAndWait(() -> {
            tabRef[0] = new EngineerDashboardEmailPassword().createEmailPasswordTab();
            frameRef[0] = new JFrame();
            frameRef[0].setContentPane(tabRef[0]);
            frameRef[0].pack();
            frameRef[0].setVisible(true);

            initialRef[0] = EngineerCredentialStore.getEmail();
            emailLabelRef[0] = findLabelWithText(tabRef[0], initialRef[0]);
            assertTrue(emailLabelRef[0] != null, "Email label not found");
        });

        long ver = EngineerCredentialStore.getVersion();
        boolean ok = EngineerCredentialStore.updateEmail("sync-test@engr.com", ver);
        assertTrue(ok, "Failed to update email in store");

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            assertTrue(emailLabelRef[0].getText().equals("sync-test@engr.com"), "Email did not sync to EngineerDashboardEmailPassword");
            frameRef[0].dispose();
        });
        System.out.println("ALL TESTS PASSED");
    }

    private static JLabel findLabelWithText(Container c, String text) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JLabel l) {
                String t = l.getText();
                if (t != null && text != null && t.equals(text)) {
                    return l;
                }
            }
            if (child instanceof Container cc) {
                JLabel found = findLabelWithText(cc, text);
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
