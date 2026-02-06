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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;

class UserLogin {
    public static void main(String[] args) {

        //centered panel for login 
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setPreferredSize(new Dimension(500, 700));
        loginPanel.setMaximumSize(new Dimension(500, 700));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new RoundBorderPanel(50));

        //para maging round ung bg panel
        RoundBackgroundPanel backgroundPanel = new RoundBackgroundPanel(50);
        backgroundPanel.setBackground(Color.LIGHT_GRAY);
        backgroundPanel.setPreferredSize(new Dimension(500, 700));
        backgroundPanel.setMaximumSize(new Dimension(500, 700));
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.add(loginPanel);
        
        // create and customize JLabels for title
        JLabel loginTitleLabel = new JLabel();
        loginTitleLabel.setText("AOMA-Heritage Monitor"); 
        loginTitleLabel.setFont(new Font("Arial", Font.BOLD,25)); 
        loginTitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); 
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(loginTitleLabel);
        loginPanel.add(Box.createVerticalStrut(50)); //para may space between title and subtitle

        //customize JLabels for subtitle
        JLabel loginSubtitleLabel = new JLabel();
        loginSubtitleLabel.setText("Login Account"); 
        loginSubtitleLabel.setFont(new Font("Arial", Font.BOLD,20)); 
        loginSubtitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); 
        loginPanel.add(loginSubtitleLabel);
        loginPanel.add(Box.createVerticalStrut(40));

        //label for email
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD,15));
        emailLabel.setAlignmentX(JLabel.LEFT);

        //textfield for email
        JTextField emailTextField = new JTextField();
        emailTextField.setMaximumSize(new Dimension(400, 40)); 
        emailTextField.setFont(new Font("Arial", Font.BOLD,20));
        emailTextField.setText("your@domain.com");

        //emaillabel and emailtextfield section part
        loginPanel.add(emailLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(emailTextField);

        //spacing onlty between email and password
        loginPanel.add(Box.createVerticalStrut(20));

        //label for password
        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD,15));
        passwordLabel.setAlignmentX(JLabel.LEFT);

        //textfield for password
        JTextField passwordTextField = new JTextField();
        passwordTextField.setMaximumSize(new Dimension(400, 40)); 
        passwordTextField.setFont(new Font("Arial", Font.BOLD,20));
        passwordTextField.setText("Password");

        //passwordlabel and passwordtextfield section part
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordTextField);

        JCheckBox loginCheckBox = new JCheckBox();
        loginCheckBox.setText("Agree to our Terms and Conditions");
        loginCheckBox.setFont(new Font("Arial", Font.BOLD,13));
        loginCheckBox.setOpaque(false);
        loginCheckBox.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
        loginPanel.add(loginCheckBox);

        //login button
        JButton loginButton = new JButton("Login Account");
        loginButton.addActionListener(null);

        //spacing before button
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(loginButton);

        //centering the login panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(backgroundPanel);

        //instantiate JFrame for user login
        JFrame loginFrame = new JFrame();
        loginFrame.setTitle("AOMA-Heritage Monitor - Login Account");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(1220, 1080);

        //shows the outputs on the frame
        loginFrame.setLayout(new BoxLayout(loginFrame.getContentPane(), BoxLayout.Y_AXIS));
        loginFrame.add(Box.createVerticalGlue());
        loginFrame.add(centerPanel);
        loginFrame.add(Box.createVerticalGlue());

        //to prevent the frame/display from not showing up dito ko nilagay sa dulo...
        loginFrame.setVisible(true); //this will get the frame to show up

    }
}

class RoundBackgroundPanel extends JPanel {

    private int radius;

    RoundBackgroundPanel(int radius) {
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

class RoundBorderPanel extends AbstractBorder{
    private int radius;

    RoundBorderPanel(int radius) {
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