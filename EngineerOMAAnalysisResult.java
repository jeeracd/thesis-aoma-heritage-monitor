public class EngineerOMAAnalysisResult extends CadOmaAnalysisResultsWindow {
    public EngineerOMAAnalysisResult() {
        super(RoleMenuBar.Role.ENGINEER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new EngineerOMAAnalysisResult();
    }
}

