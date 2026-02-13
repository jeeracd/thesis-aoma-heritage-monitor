import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SetupConnection extends JFrame {

    public SetupConnection() {
        this.setTitle("AOMA-Heritage Monitor");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 850);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Setup & Connection", new JPanel());
        tabs.add("Analysis", new JPanel());
        tabs.add("Report", new JPanel());
        add(tabs);

        setVisible(true);
        setLocationRelativeTo(null);

    }

    public static void main(String[] args) {
        new SetupConnection();
    }
}
