
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Report extends JFrame{
    
    public Report() {
        this.setTitle("AOMA-Heritage Monitor - Report");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.add("Setup & Connection", new JPanel());
        reportTabs.add("Analysis", new JPanel());
        reportTabs.add("Report", new JPanel());
        add(reportTabs);

        setVisible(true);


    }
    public static void main(String[] args) {
        new Report();
    }
}
