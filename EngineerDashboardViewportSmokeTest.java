import javax.swing.SwingUtilities;

public final class EngineerDashboardViewportSmokeTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            EngineerStartingPage start = new EngineerStartingPage();
            start.setSize(360, 640);
            start.validate();
            start.setSize(1400, 850);
            start.validate();
            start.dispose();

            EngineerDashboardUserSettings settings = new EngineerDashboardUserSettings();
            settings.setSize(360, 640);
            settings.validate();
            settings.setSize(1400, 850);
            settings.validate();
            settings.dispose();
        });
        System.out.println("ALL TESTS PASSED");
    }
}

