import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class HelpWindowsSmokeTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame[] frames = new JFrame[] {
                    new EngineerSensorSetupGuide(),
                    new EngineerUserDocumentation(),
                    new EngineerAboutAOMA(),
                    new EngineerContactSupport(),
                    new HeadSensorSetupGuide(),
                    new HeadUserDocumentation(),
                    new HeadAboutAOMA(),
                    new HeadContactSupport(),
                    new OfficerSensorSetupGuide(),
                    new OfficerUserDocumentation(),
                    new OfficerAboutAOMA(),
                    new OfficerContactSupport()
            };

            for (JFrame frame : frames) {
                frame.dispose();
            }
        });
    }
}
