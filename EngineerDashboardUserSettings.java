import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EngineerDashboardUserSettings extends JFrame {
    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JLabel profileStatusLabel = new JLabel(" ");
    private final JLabel profileErrorLabel = new JLabel(" ");
    private final AvatarPanel avatarPanel = new AvatarPanel();
    private final JButton uploadButton = new JButton("Upload Photo");
    private final JButton saveButton = new JButton(EngineerUiNames.ACTION_SAVE_CHANGES_EXIT);
    private final JButton resetButton = new JButton("Reset Preferences");
    private final SwingDebouncer autosaveDebouncer = new SwingDebouncer(650, this::autosaveProfile);
    private final Runnable removeProfileListener;
    private volatile boolean applyingExternalProfileUpdate = false;
    private volatile String photoPath = "";
    private volatile boolean saving = false;

    public EngineerDashboardUserSettings() {
        setTitle(EngineerUiNames.windowTitle(EngineerUiNames.MENU_USER_SETTINGS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.ENGINEER);

        setLayout(new BorderLayout());
        add(createMainPanel(), BorderLayout.CENTER);

        removeProfileListener = EngineerProfileStore.addListener(this::applyProfileFromStore);
        applyProfileFromStore();

        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel(EngineerUiNames.USER_SETTINGS_HEADER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        JLabel accessLabel = new JLabel("Access: " + EngineerPreferences.getAccessLevel().name());
        accessLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accessLabel.setForeground(Color.DARK_GRAY);
        header.add(accessLabel, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab(EngineerUiNames.USER_SETTINGS_TAB_NAME_PHOTO, UiErrorBoundary.wrap(EngineerUiNames.USER_SETTINGS_TAB_NAME_PHOTO, this::createNamePhotoTab));
        tabs.addTab(EngineerUiNames.USER_SETTINGS_TAB_EMAIL_PASSWORD, UiErrorBoundary.wrap(EngineerUiNames.USER_SETTINGS_TAB_EMAIL_PASSWORD, () -> new EngineerDashboardEmailPassword().createEmailPasswordTab()));
        tabs.addTab(EngineerUiNames.USER_SETTINGS_TAB_NOTIFICATIONS, UiErrorBoundary.wrap(EngineerUiNames.USER_SETTINGS_TAB_NOTIFICATIONS, () -> new EngineerDashboardNotifications().createNotificationsTab()));

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

        tabPanel.add(createProfileSection());
        tabPanel.add(Box.createVerticalStrut(20));
        tabPanel.add(createPhotoSection());
        tabPanel.add(Box.createVerticalStrut(18));
        tabPanel.add(createProfileStatusSection());
        tabPanel.add(Box.createVerticalStrut(18));
        tabPanel.add(createActionsSection());

        return tabPanel;
    }

    private JPanel createProfileSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(20, 20, 20, 20)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        firstNameField.setText(EngineerProfileStore.getFirstName());
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        firstNameField.setBorder(titledBorder("First Name", Color.BLACK));

        lastNameField.setText(EngineerProfileStore.getLastName());
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lastNameField.setBorder(titledBorder("Last Name", Color.BLACK));

        attachAutosave(firstNameField);
        attachAutosave(lastNameField);

        gbc.gridy = 0;
        panel.add(firstNameField, gbc);

        gbc.gridy = 1;
        panel.add(lastNameField, gbc);

        applyRbac();
        validateProfile();

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

    String savedPhotoPath = EngineerPreferences.getPhotoPath();
    if (savedPhotoPath != null && !savedPhotoPath.isBlank()) {
        loadPhoto(savedPhotoPath);
    }
    content.add(avatarPanel, BorderLayout.WEST);

    uploadButton.setPreferredSize(new Dimension(160, 40));
    uploadButton.setFocusPainted(false);

    uploadButton.addActionListener(e -> {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Photo");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                Image image = ImageIO.read(f);
                avatarPanel.setAvatarImage(image);
                photoPath = f.getAbsolutePath();
                autosaveDebouncer.call();
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

    private JPanel createProfileStatusSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        profileStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        profileStatusLabel.setForeground(Color.DARK_GRAY);
        profileErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        profileErrorLabel.setForeground(new Color(160, 0, 0));
        p.add(profileStatusLabel, BorderLayout.WEST);
        p.add(profileErrorLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel createActionsSection() {
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(220, 45));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveAndExit());

        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setPreferredSize(new Dimension(220, 45));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetAllPreferences());

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        wrapper.setOpaque(false);
        wrapper.add(resetButton);
        wrapper.add(saveButton);

        return wrapper;
    }

    private void saveAndExit() {
        try {
            persistProfileNow();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please fix validation errors before saving.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, EngineerUiNames.DIALOG_CHANGES_SAVED, EngineerUiNames.DIALOG_SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
        new EngineerStartingPage();
        dispose();
    }

    private void resetAllPreferences() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reset all saved preferences (profile, notifications, and authentication settings)?",
                "Reset Preferences",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        EngineerPreferences.resetAll();
        dispose();
        new EngineerDashboardUserSettings();
    }

    private void attachAutosave(JTextField field) {
        if (field == null) {
            return;
        }
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onChange(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange(); }
            private void onChange() {
                if (applyingExternalProfileUpdate) {
                    return;
                }
                validateProfile();
                autosaveDebouncer.call();
            }
        });
    }

    private void autosaveProfile() {
        if (saving) {
            return;
        }
        saving = true;
        try {
            if (!validateProfile()) {
                setStatus("Not saved");
                return;
            }
            if (EngineerPreferences.getAccessLevel() == EngineerPreferences.AccessLevel.VIEWER) {
                setStatus("Read-only");
                return;
            }
            try {
                persistProfileNow();
                setStatus("Saved");
            } catch (Exception ex) {
                setStatus("Not saved");
            }
        } finally {
            saving = false;
        }
    }

    private void persistProfileNow() {
        boolean ok = EngineerProfileStore.setName(firstNameField.getText(), lastNameField.getText());
        if (!ok) {
            throw new IllegalStateException("Invalid name");
        }
        EngineerPreferences.setPhotoPath(photoPath);
    }

    private boolean validateProfile() {
        boolean okFirst = validateName(firstNameField.getText());
        boolean okLast = validateName(lastNameField.getText());

        firstNameField.setBorder(titledBorder("First Name", okFirst ? Color.BLACK : new Color(160, 0, 0)));
        lastNameField.setBorder(titledBorder("Last Name", okLast ? Color.BLACK : new Color(160, 0, 0)));

        if (!okFirst) {
            profileErrorLabel.setText("Invalid first name");
        } else if (!okLast) {
            profileErrorLabel.setText("Invalid last name");
        } else {
            profileErrorLabel.setText(" ");
        }

        boolean ok = okFirst && okLast;
        saveButton.setEnabled(ok);
        resetButton.setEnabled(true);
        if (EngineerPreferences.getAccessLevel() == EngineerPreferences.AccessLevel.VIEWER) {
            saveButton.setEnabled(false);
        }
        return ok;
    }

    private void applyRbac() {
        boolean canEdit = EngineerPreferences.getAccessLevel() != EngineerPreferences.AccessLevel.VIEWER;
        firstNameField.setEnabled(canEdit);
        lastNameField.setEnabled(canEdit);
        uploadButton.setEnabled(canEdit);
    }

    private void loadPhoto(String path) {
        try {
            File f = new File(path);
            if (!f.exists() || !f.isFile()) {
                return;
            }
            Image img = ImageIO.read(f);
            if (img == null) {
                return;
            }
            photoPath = f.getAbsolutePath();
            avatarPanel.setAvatarImage(img);
        } catch (Exception ignored) {
        }
    }

    private void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> {
            profileStatusLabel.setText(msg == null || msg.isBlank() ? " " : msg);
        });
    }

    private static boolean validateName(String v) {
        return EngineerProfileStore.isValidName(v);
    }

    private void applyProfileFromStore() {
        applyingExternalProfileUpdate = true;
        try {
            String f = EngineerProfileStore.getFirstName();
            String l = EngineerProfileStore.getLastName();
            if (f != null && !f.equals(firstNameField.getText())) {
                firstNameField.setText(f);
            }
            if (l != null && !l.equals(lastNameField.getText())) {
                lastNameField.setText(l);
            }
            validateProfile();
        } finally {
            applyingExternalProfileUpdate = false;
        }
    }

    @Override
    public void dispose() {
        removeProfileListener.run();
        super.dispose();
    }

    private static Border titledBorder(String title, Color color) {
        Color c = color == null ? Color.BLACK : color;
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(c),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12)
        );
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
        new EngineerDashboardUserSettings();    
}
}
