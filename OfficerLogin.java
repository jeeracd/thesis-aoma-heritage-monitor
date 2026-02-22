import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OfficerLogin extends JFrame {

    private JButton btnHead, btnEngineer, btnOfficer;

    public OfficerLogin() {
        setTitle("AOMA-Heritage Monitor - LGU Officer Login");
        setSize(680, 590);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        JLabel title = new JLabel("AOMA-Heritage Monitor");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("By RJ13 Connectors");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("Account Login");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        titlePanel.add(loginLabel);

        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        mainPanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
        rolePanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        btnHead = new JButton("LGU Head");
        btnEngineer = new JButton("Structural Engineer");
        btnOfficer = new JButton("LGU Officer");

        Dimension roleBtnSize = new Dimension(250, 45);
        btnHead.setMaximumSize(roleBtnSize);
        btnEngineer.setMaximumSize(roleBtnSize);
        btnOfficer.setMaximumSize(roleBtnSize);

        // Remove background & default border
        for (JButton btn : new JButton[]{btnHead, btnEngineer, btnOfficer}) {
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(false);

            // subtle default border so it looks clickable
            btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            btn.setBorderPainted(true);

            btn.setForeground(Color.BLACK);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        // Default selected
        setActiveButton(btnOfficer);

        // LGU Head (current page)
        btnHead.addActionListener(e -> {
            setActiveButton(btnHead);
            new HeadLogin();   
            dispose();             
        });

        // Structural Engineer
        btnEngineer.addActionListener(e -> {
            setActiveButton(btnEngineer);
            new EngineerLogin();   
            dispose();             
        });

        // LGU Officer
        btnOfficer.addActionListener(e -> {
            setActiveButton(btnOfficer);
            new OfficerLogin();    
            dispose();             
        });

        rolePanel.add(btnHead);
        rolePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rolePanel.add(btnEngineer);
        rolePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rolePanel.add(btnOfficer);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel roleTitle = new JLabel("Local Government Unit Officer");
        roleTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea description = new JTextArea(
            "Log in to the field monitoring interface for daily heritage preservation tasks." +
            "If you are an LGU Officer, use this portal to document site status, review local sensor alerts," +
            "and report on the cultural preservation of assigned heritage structures."
        );

        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setEditable(false);
        description.setFocusable(false);
        description.setBackground(null);
        description.setFont(new Font("Arial", Font.PLAIN, 12));
        description.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JTextField emailField = new JTextField("your@gmail.com");
        emailField.setPreferredSize(new Dimension(250, 30));
        emailField.setMargin(new Insets(5, 8, 5, 8));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPasswordField passwordField = new JPasswordField("Password");
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setMargin(new Insets(5, 8, 5, 8));

        JButton loginBtn = new JButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(120, 35));

        loginPanel.add(roleTitle);
        loginPanel.add(description);
        loginPanel.add(emailLabel);
        loginPanel.add(emailField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(passLabel);
        loginPanel.add(passwordField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(loginBtn);

        mainPanel.add(rolePanel);
        mainPanel.add(loginPanel);

        add(mainPanel, BorderLayout.CENTER);

        /* =======================
           FOOTER
        ======================= */
        JLabel createAccountLabel = new JLabel("Don't have an account? Create an account here.");
        createAccountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new CreateAccount();
                dispose();
            }
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        footerPanel.add(createAccountLabel);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setActiveButton(JButton active) {
    JButton[] buttons = { btnHead, btnEngineer, btnOfficer };

    for (JButton btn : buttons) {
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        btn.setBorderPainted(true);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.BLACK);
    }

    // active state (strong border)
    active.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
    active.setForeground(Color.BLACK);

    active.repaint();
}

public static void main(String[] args) {
    new OfficerLogin();
    
}
}
