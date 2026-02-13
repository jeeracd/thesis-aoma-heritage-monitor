import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;

public class UserRegistration extends JFrame {
      
    public UserRegistration() {
        this.setTitle("AOMA-Heritage Monitor - Register Account");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 850);

        JPanel registrationPanel = new JPanel();
        registrationPanel.setOpaque(false);
        registrationPanel.setPreferredSize(new Dimension(500, 700));
        registrationPanel.setMaximumSize(new Dimension(500, 700));
        registrationPanel.setLayout(new BoxLayout(registrationPanel, BoxLayout.Y_AXIS));
        registrationPanel.setBorder(new RegisterRoundBorderPanel(50));

        RegisterRoundBackgroundPanel backgroundPanel = new RegisterRoundBackgroundPanel(50);
        backgroundPanel.setBackground(Color.LIGHT_GRAY);
        backgroundPanel.setPreferredSize(new Dimension(500, 700));
        backgroundPanel.setMaximumSize(new Dimension(500, 700));
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.add(registrationPanel);

        JLabel registrationTitleLabel = new JLabel();
        registrationTitleLabel.setText("AOMA-Heritage Monitor");
        registrationTitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        registrationTitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        registrationPanel.add(Box.createVerticalStrut(30));
        registrationPanel.add(registrationTitleLabel);
        registrationPanel.add(Box.createVerticalStrut(50));

        JLabel registrationSubtitleLabel = new JLabel();
        registrationSubtitleLabel.setText("Register Account");
        registrationSubtitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        registrationSubtitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        registrationPanel.add(registrationSubtitleLabel);
        registrationPanel.add(Box.createVerticalStrut(40));

        JLabel nameRegLabel = new JLabel();
        nameRegLabel.setText("Name");
        nameRegLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameRegLabel.setAlignmentX(JLabel.LEFT);

        JTextField nameRegTextField = new JTextField();
        nameRegTextField.setMaximumSize(new Dimension(400, 40));
        nameRegTextField.setFont(new Font("Arial", Font.BOLD, 20));
        nameRegTextField.setText("Name");

        registrationPanel.add(nameRegLabel);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(nameRegTextField);

        registrationPanel.add(Box.createVerticalStrut(20));

        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 15));
        emailLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT); 

        JTextField emailTextField = new JTextField();
        emailTextField.setMaximumSize(new Dimension(400, 40));
        emailTextField.setFont(new Font("Arial", Font.BOLD, 20));
        emailTextField.setText("your@domain.com");

        registrationPanel.add(emailLabel);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(emailTextField);

        registrationPanel.add(Box.createVerticalStrut(20));

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 15));
        passwordLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JTextField passwordTextField = new JTextField();
        passwordTextField.setMaximumSize(new Dimension(400, 40));
        passwordTextField.setFont(new Font("Arial", Font.BOLD, 20));
        passwordTextField.setText("Password");

        registrationPanel.add(passwordLabel);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(passwordTextField);

        JButton registerButton = new JButton("Register Account");
        registerButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.addActionListener(null);

        registerButton.addActionListener(e -> {
            this.dispose();
            new LandingPage();
        });

        registrationPanel.add(Box.createVerticalStrut(30));
        registrationPanel.add(registerButton);

        JLabel haveAccountLabel = new JLabel();
        haveAccountLabel.setText("Already have an account?");
        haveAccountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        haveAccountLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        registrationPanel.add(Box.createVerticalStrut(20));
        registrationPanel.add(haveAccountLabel);

        JButton loginButton = new JButton();
        loginButton.setText("Login Account");
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        registrationPanel.add(Box.createVerticalStrut(10));
        registrationPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            this.dispose();
            new UserLogin();
        });
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(backgroundPanel);

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.add(centerPanel);
        this.add(Box.createVerticalGlue());

        this.setVisible(true);
        this.setLocationRelativeTo(null);


}
    public static void main(String[] args) {
        new UserRegistration();
    }
}

class RegisterRoundBackgroundPanel extends JPanel {

    private int radius;

    RegisterRoundBackgroundPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}

class RegisterRoundBorderPanel extends AbstractBorder{
    private int radius;

    RegisterRoundBorderPanel(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
}
}
