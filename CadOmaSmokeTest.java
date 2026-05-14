import javax.swing.SwingUtilities;

public class CadOmaSmokeTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            EngineerOMAAnalysisResultWindow e = new EngineerOMAAnalysisResultWindow();
            e.dispose();
            HeadOMAAnalysisResultWindow h = new HeadOMAAnalysisResultWindow();
            h.dispose();
        });
    }
}

