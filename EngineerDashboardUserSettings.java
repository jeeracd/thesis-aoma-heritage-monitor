import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class EngineerDashboardUserSettings extends JFrame {

    public EngineerDashboardUserSettings() {
        setTitle("AOMA-Heritage Monitor - User Settings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

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
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("Name & Photo", createNamePhotoTab());
        tabs.addTab("Email & Password", new EngineerDashboardEmailPassword().createEmailPasswordTab());
        tabs.addTab("Notifications", new EngineerDashboardNotifications().createNotificationsTab());

        // Colors
        Color selectedColor = new Color(30, 144, 255); // nice blue
        Color defaultColor = UIManager.getColor("TabbedPane.background");

        // Initialize colors
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

        content.add(new AvatarPanel(), BorderLayout.WEST);

        JButton uploadButton = new JButton("Upload Photo");
        uploadButton.setPreferredSize(new Dimension(160, 40));
        uploadButton.setFocusPainted(false);

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

    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    wrapper.setOpaque(false); 
    wrapper.add(saveButton);

    return wrapper;
    }
    static class AvatarPanel extends JPanel {
        AvatarPanel() {
            setPreferredSize(new Dimension(200, 200));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawOval(20, 20, 160, 160);
        }
    }

    public static void main(String[] args) {
        new EngineerDashboardUserSettings();    
}
}