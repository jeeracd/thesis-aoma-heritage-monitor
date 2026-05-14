import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UsersLoginOptions extends JFrame {

    public UsersLoginOptions() {

        setTitle("AOMA-Heritage Monitor - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JLabel optionTitleLabel = new JLabel("AOMA-Heritage Monitor");
        optionTitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        optionTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(optionTitleLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        JLabel optionSubtitleLabel = new JLabel("By RJ13 Connectors");
        optionSubtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        optionSubtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(optionSubtitleLabel);

        mainPanel.add(Box.createVerticalStrut(40));
        JLabel accountLoginLabel = new JLabel("Account Login");
        accountLoginLabel.setFont(new Font("Arial", Font.BOLD, 16));
        accountLoginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(accountLoginLabel);

        mainPanel.add(Box.createVerticalStrut(25));
        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(300, 40));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailField);

        mainPanel.add(Box.createVerticalStrut(15));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordField);

        mainPanel.add(Box.createVerticalStrut(25));
        JButton loginButton = new JButton("Login");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainPanel.add(loginButton);

        loginButton.addActionListener(e -> {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Dummy accounts
        if (email.equals("juandelacruz1@engr.com") && password.equals("dummy123")) {
            JOptionPane.showMessageDialog(this, "Engineer Login Successful!");
            new EngineerBldgStatusOverview();
            dispose();

        } else if (email.equals("juandelacruz2@officer.com") && password.equals("dummy123")) {
            JOptionPane.showMessageDialog(this, "Officer Login Successful!");
            new OfficerBldgStatusOverview();
            dispose();

        } else if (email.equals("juandelacruz3@head.com") && password.equals("dummy123")) {
            JOptionPane.showMessageDialog(this, "Head Login Successful!");
            new HeadBldgStatusOverview();
            dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid Email or Password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    });

        mainPanel.add(Box.createVerticalStrut(40));
        JLabel createAccountLabel = new JLabel("Don't have an account? Create an account here.");
        createAccountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        createAccountLabel.setForeground(Color.BLACK);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new CreateAccount().setVisible(true);
                dispose();
            }
        });

        mainPanel.add(createAccountLabel);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new UsersLoginOptions();
    }
}
