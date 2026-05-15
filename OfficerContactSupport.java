import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class OfficerContactSupport extends JFrame {

    public OfficerContactSupport() {
        setTitle("AOMA-Heritage Monitor - Contact Support");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JPanel contactSupportPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.OFFICER);
        contactSupportPanel.setBackground(Color.WHITE);

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
                    OfficerContactSupport.this,
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

        contactSupportPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 760);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        contactSupportPanel.add(centerPanel);

        JLabel greetingLabel = new JLabel("Contact Support", JLabel.LEFT);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        greetingLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        );
        centerPanel.add(greetingLabel, BorderLayout.NORTH);

        JPanel supportContainer = new JPanel();
        supportContainer.setBackground(Color.WHITE);
        supportContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 45, 35));

        supportContainer.add(createSupportCard(
                "JOHN RAFAEL A. ALEJANDRINO",
                "202310450",
                "09561260465",
                "Balangkas, Valenzuela City",
                "alejandrino.johnrafael@gmail.com",
                "FEU TECH"
        ));

        supportContainer.add(createSupportCard(
                "JUDINELE LORENZ P. PINZA",
                "202310630",
                "09613511301",
                "Lias, Marilao, Bulacan",
                "pinzajud@gmail.com",
                "FEU TECH"
        ));

        supportContainer.add(createSupportCard(
                "JEROME M. DEL ROSARIO",
                "202311234",
                "09763028338",
                "Corazon de Jesus, San Juan City",
                "delrosariomrj@gmail.com",
                "FEU TECH"
        ));

        supportContainer.add(createSupportCard(
                "RALPH CHRISTIAN A. DEL MUNDO ",
                "202211430",
                "09569534569",
                "Brgy. Bagbag Novaliches, Quezon City",
                "delmundo.rc@gmail.com",
                "FEU TECH"
        ));

        centerPanel.add(supportContainer, BorderLayout.CENTER);

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
        add(contactSupportPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JPanel createSupportCard(String name, String sn, String phone, String address, String email, String school) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(240, 500));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setLayout(null);

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBounds(65, 20, 100, 100);

        try {
            ImageIcon icon = new ImageIcon("usericon.png");
            Image scaled = icon.getImage().getScaledInstance(85, 85, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            iconLabel.setText("USER");
        }

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFocusable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 15));
        infoArea.setBackground(Color.WHITE);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText(
                "NAME: " + name + "\n\n" +
                "SN: " + sn + "\n\n" +
                "Phone: " + phone + "\n\n" +
                "Residential Address:\n" + address + "\n\n" +
                "Email Address:\n" + email + "\n\n" +
                "School: " + school
        );

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(null);
        scrollPane.setBounds(15, 120, 205, 360);

        card.add(iconLabel);
        card.add(scrollPane);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OfficerContactSupport());
    }
}
