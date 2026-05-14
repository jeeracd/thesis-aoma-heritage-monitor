import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class OfficerDashboardUserSettings extends JFrame {

    public OfficerDashboardUserSettings() {
        setTitle("AOMA-Heritage Monitor - Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.OFFICER);

        setLayout(new BorderLayout());
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("User Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("Name & Photo", createNamePhotoTab());
        tabs.addTab("Email & Password", new OfficerDashboardEmailPassword().createEmailPasswordTab());
        tabs.addTab("Notifications", new OfficerDashboardNotifications().createNotificationsTab());

        Color selectedColor = new Color(30, 144, 255); 
        Color defaultColor = UIManager.getColor("TabbedPane.background");

        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setBackgroundAt(i, defaultColor);
        }
        tabs.setBackgroundAt(tabs.getSelectedIndex(), selectedColor);

        // Change color on click
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                tabs.setBackgroundAt(i, defaultColor);
            }
            tabs.setBackgroundAt(tabs.getSelectedIndex(), selectedColor);
        });

        mainPanel.add(tabs, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createNamePhotoTab() {
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
        tabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tabPanel.add(createNameSection());
        tabPanel.add(Box.createVerticalStrut(20));
        tabPanel.add(createPhotoSection());
        tabPanel.add(Box.createVerticalStrut(30));
        tabPanel.add(createSaveButton());

        return tabPanel;
    }
        private JPanel createNameSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JTextField firstNameField = new JTextField("Juan");
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        firstNameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "First Name",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12)
        ));

        JTextField lastNameField = new JTextField("Dela Cruz");
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lastNameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Last Name",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12)
        ));

        gbc.gridy = 0;
        panel.add(firstNameField, gbc);

        gbc.gridy = 1;
        panel.add(lastNameField, gbc);

        return panel;
    }

    private JPanel createPhotoSection() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new CompoundBorder(
            new LineBorder(Color.BLACK, 2),
            new EmptyBorder(20, 20, 20, 20)
    ));
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

    JLabel label = new JLabel("Photo");
    label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    panel.add(label, BorderLayout.NORTH);

    JPanel content = new JPanel(new BorderLayout());

    AvatarPanel avatarPanel = new AvatarPanel();
    content.add(avatarPanel, BorderLayout.WEST);

    JButton uploadButton = new JButton("Upload Photo");
    uploadButton.setPreferredSize(new Dimension(160, 40));
    uploadButton.setFocusPainted(false);

    uploadButton.addActionListener(e -> {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Photo");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Image image = ImageIO.read(chooser.getSelectedFile());
                avatarPanel.setAvatarImage(image);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid image file",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(uploadButton);

    content.add(buttonPanel, BorderLayout.CENTER);
    panel.add(content, BorderLayout.CENTER);

    return panel;
}
    private JPanel createSaveButton() {
    JButton saveButton = new JButton("Save Changes & Exit");
    saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    saveButton.setPreferredSize(new Dimension(220, 45));
    saveButton.setFocusPainted(false);
    saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    saveButton.addActionListener(e -> {
        JOptionPane.showMessageDialog(
                OfficerDashboardUserSettings.this,
                "Changes saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        new OfficerStartingPage();
        dispose();
    });

    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    wrapper.setOpaque(false);
    wrapper.add(saveButton);

    return wrapper;
}
    static class AvatarPanel extends JPanel {

        private Image avatarImage;

        AvatarPanel() {
            setPreferredSize(new Dimension(200, 200));
            setOpaque(false);
        }

        public void setAvatarImage(Image image) {
            this.avatarImage = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int diameter = 160;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            // Draw circular border
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, diameter, diameter);

            if (avatarImage != null) {
                Shape circle = new java.awt.geom.Ellipse2D.Double(x, y, diameter, diameter);
                g2.setClip(circle);

                g2.drawImage(
                    avatarImage,
                    x,
                    y,
                    diameter,
                    diameter,
                    this
                );
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new OfficerDashboardUserSettings();    
}
}
