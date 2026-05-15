import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class OfficerUserDocumentation extends JFrame {

    public OfficerUserDocumentation() {
        setTitle("AOMA-Heritage Monitor - User Documentation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JPanel userDocumentationPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.OFFICER);

        JPanel centerPanelDescription = new JPanel(new BorderLayout());
        centerPanelDescription.setBounds(10, 20, 1380, 40);
        Border firstBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanelDescription.setBorder(firstBorder);

        JLabel centerTitleLabel = new JLabel(
                "Automated - Operational Modal Analysis to Monitor the Safety and Serviceability of Heritage Buildings",JLabel.CENTER
        );
        centerTitleLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
        centerTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ImageIcon userIcon = new ImageIcon("usericon.png");
        Image userImgScaled = userIcon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
        JLabel userIconLabel = new JLabel(new ImageIcon(userImgScaled));
        userIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            dispose();
            new OfficerDashboardUserSettings();
        });

        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    OfficerUserDocumentation.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new UsersLoginOptions();
            }
        });

        userMenu.add(userSettings);
        userMenu.addSeparator();
        userMenu.add(logout);
        userIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        userIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                userMenu.show(userIconLabel, 0, userIconLabel.getHeight());
            }
        });

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);

        userDocumentationPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 760);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        userDocumentationPanel.add(centerPanel);

        JLabel greetingLabel = new JLabel("User Documentation", JLabel.LEFT);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        greetingLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        centerPanel.add(greetingLabel, BorderLayout.NORTH);

        JTextArea documentationArea = new JTextArea();
        documentationArea.setFont(new Font("Arial", Font.PLAIN, 14));
        documentationArea.setEditable(false);
        documentationArea.setLineWrap(true);
        documentationArea.setWrapStyleWord(true);
        documentationArea.setMargin(new Insets(15, 15, 15, 15));

        documentationArea.setText(
                "1. System Features\n\n" +
                "The AOMA Heritage Monitor provides real-time vibration monitoring, structural health monitoring, " +
                "automated modal analysis, project management, sensor management, report generation, and data visualization.\n\n" +
                "2. Getting Started\n\n" +
                "Login with your credentials and navigate to the dashboard to begin monitoring heritage structures.\n\n" +
                "3. Support\n\n" +
                "For technical support, contact the development team through the Contact Support page."
        );

        JScrollPane scrollPane = new JScrollPane(documentationArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setPreferredSize(new java.awt.Dimension(1400, 45));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(120, 120, 120)));

        JLabel footerLabel = new JLabel("Status: ESP32 Hub Not Connected");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        footerLabel.setForeground(Color.RED);
        footerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        footerPanel.add(footerLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(userDocumentationPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        new OfficerUserDocumentation();
    }
}
