public class HeadOMAAnalysisResultWindow extends CadOmaAnalysisResultsWindow {
    public HeadOMAAnalysisResultWindow() {
        super(RoleMenuBar.Role.HEAD);
        setVisible(true);
    }

    public static void main(String[] args) {
        new HeadOMAAnalysisResultWindow();
    }
}

