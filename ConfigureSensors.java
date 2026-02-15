import java.awt.*;
import javax.swing.*;

public class ConfigureSensors extends JFrame {

    public ConfigureSensors() {
        setTitle("Configure Sensors");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Configure Sensors Screen", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(100, 30, 400, 30);
        mainPanel.add(titleLabel);

        add(mainPanel);
        setVisible(true);
    }
    public static void main(String[] args) {
        new ConfigureSensors();
    }
}
