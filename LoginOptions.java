import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoginOptions extends JFrame {

    public LoginOptions() {

        // JFrame settings
        setTitle("AOMA-Heritage Monitor - Login Options");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);

        // Main panel (vertical layout)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        // Title
        JLabel optionTitleLabel = new JLabel("AOMA-Heritage Monitor");
        optionTitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        optionTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(optionTitleLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        // Subtitle
        JLabel optionSubtitleLabel = new JLabel("By RJ13 Connectors");
        optionSubtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        optionSubtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(optionSubtitleLabel);

        // Account Login label
        JLabel accountLoginLabel = new JLabel("Account Login");
        accountLoginLabel.setFont(new Font("Arial", Font.BOLD, 16));
        accountLoginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(accountLoginLabel);

        // Buttons
        JButton lguHeadBtn = new JButton("LGU Head");
        JButton engineerBtn = new JButton("Structural Engineer");
        JButton officerBtn = new JButton("LGU Officer");

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(lguHeadBtn);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(engineerBtn);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(officerBtn);

        Dimension buttonSize = new Dimension(300, 50);
        lguHeadBtn.setMaximumSize(buttonSize);
        engineerBtn.setMaximumSize(buttonSize);
        officerBtn.setMaximumSize(buttonSize);

        lguHeadBtn.setFont(new Font("Arial", Font.BOLD, 14));
        engineerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        officerBtn.setFont(new Font("Arial", Font.BOLD, 14));

        lguHeadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        engineerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        officerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create account label
        JLabel createAccountLabel = new JLabel("Don't have an account? Create an account here.");
        createAccountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        createAccountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(createAccountLabel);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginOptions();
    }
}