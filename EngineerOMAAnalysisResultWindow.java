public class EngineerOMAAnalysisResultWindow extends CadOmaAnalysisResultsWindow {
    public EngineerOMAAnalysisResultWindow() {
        super(RoleMenuBar.Role.ENGINEER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new EngineerOMAAnalysisResultWindow();
    }
}

