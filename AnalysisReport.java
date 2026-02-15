import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnalysisReport extends JPanel {

    public AnalysisReport() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Analysis Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        add(title, BorderLayout.NORTH);


    }
}
