import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EngineerDashboardEmailPassword {
    private final JLabel emailLabel = new JLabel();
    private final JButton changeEmailButton = createOutlinedButton("Change Email");

    private final JLabel passwordStatusLabel = new JLabel();
    private final JButton changePasswordButton = createOutlinedButton("Change Password");
    private Runnable removeCredentialListener = () -> {};

    public JPanel createEmailPasswordTab() {
        EngineerCredentialStore.getEmail();

        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
        tabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tabPanel.add(createEmailSection());
        tabPanel.add(Box.createVerticalStrut(16));
        tabPanel.add(createPasswordSection());
        tabPanel.add(Box.createVerticalStrut(22));
        tabPanel.add(createSaveButton());

        refreshEmailUi();
        removeCredentialListener = EngineerCredentialStore.addListener(this::refreshEmailUi);
        tabPanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!tabPanel.isDisplayable()) {
                    removeCredentialListener.run();
                    removeCredentialListener = () -> {};
                }
            }
        });

        return tabPanel;
    }

    private JPanel createEmailSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel loginMethodLabel = new JLabel("You logged in via Email & Password");
        loginMethodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel infoLabel = new JLabel("Your user account is registered with the email address:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        changeEmailButton.addActionListener(e -> changeEmail());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        buttons.setOpaque(false);
        buttons.add(changeEmailButton);

        gbc.gridy = 0;
        panel.add(loginMethodLabel, gbc);

        gbc.gridy = 1;
        panel.add(infoLabel, gbc);

        gbc.gridy = 2;
        panel.add(emailLabel, gbc);

        gbc.gridy = 3;
        panel.add(buttons, gbc);

        return panel;
    }

    private JButton createOutlinedButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createPasswordSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(20, 20, 20, 20)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel header = new JLabel("Password");
        header.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        passwordStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordStatusLabel.setForeground(Color.DARK_GRAY);
        refreshPasswordUi();

        changePasswordButton.addActionListener(e -> changePasswordWithStrengthMeter());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        buttons.setOpaque(false);
        buttons.add(changePasswordButton);

        gbc.gridy = 0;
        panel.add(header, gbc);
        gbc.gridy = 1;
        panel.add(passwordStatusLabel, gbc);
        gbc.gridy = 2;
        panel.add(buttons, gbc);

        return panel;
    }

    private JPanel createSaveButton() {
        JButton saveButton = new JButton(EngineerUiNames.ACTION_SAVE_CHANGES_EXIT);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(220, 45));
        saveButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
        JOptionPane.showMessageDialog(
                null,
                EngineerUiNames.DIALOG_CHANGES_SAVED,
                EngineerUiNames.DIALOG_SUCCESS_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );

        new EngineerStartingPage().setVisible(true);

        SwingUtilities.getWindowAncestor(saveButton).dispose();
        });

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(saveButton);

        return wrapper;
    }

    private void refreshEmailUi() {
        emailLabel.setText(EngineerCredentialStore.getEmail());
    }

    private void refreshPasswordUi() {
        passwordStatusLabel.setText("Password is stored securely (hashing enabled).");
    }

    private void changeEmail() {
        String current = EngineerCredentialStore.getEmail();
        String newEmail = JOptionPane.showInputDialog(null, "Enter new email:", current);
        if (newEmail == null) {
            return;
        }
        String e = newEmail.trim();
        if (!EngineerCredentialStore.isValidEmail(e)) {
            JOptionPane.showMessageDialog(null, "Invalid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long ver = EngineerCredentialStore.getVersion();
        boolean ok = EngineerCredentialStore.updateEmail(e, ver);
        if (!ok) {
            JOptionPane.showMessageDialog(null, "Email update failed. Please retry.", "Sync Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Email updated.", "Email Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void changePasswordWithStrengthMeter() {
        JPasswordField currentField = new JPasswordField();
        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JProgressBar strength = new JProgressBar(0, 100);
        strength.setStringPainted(true);
        JLabel rules = new JLabel(" ");
        rules.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel("Current password"));
        p.add(currentField);
        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("New password"));
        p.add(newField);
        p.add(Box.createVerticalStrut(6));
        p.add(strength);
        p.add(rules);
        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Confirm new password"));
        p.add(confirmField);

        Runnable update = () -> {
            char[] pass = newField.getPassword();
            Strength s = strength(pass);
            strength.setValue(s.score());
            strength.setString(s.label());
            rules.setText(s.hint());
        };

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { update.run(); }
            @Override public void removeUpdate(DocumentEvent e) { update.run(); }
            @Override public void changedUpdate(DocumentEvent e) { update.run(); }
        };
        newField.getDocument().addDocumentListener(dl);
        confirmField.getDocument().addDocumentListener(dl);
        update.run();

        int ok = JOptionPane.showConfirmDialog(null, p, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) {
            return;
        }

        boolean verified = EngineerCredentialStore.verifyCredentials(EngineerCredentialStore.getEmail(), currentField.getPassword());
        if (!verified) {
            JOptionPane.showMessageDialog(null, "Current password is incorrect.", "Change Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        char[] newPass = newField.getPassword();
        char[] conf = confirmField.getPassword();
        Strength s = strength(newPass);
        if (s.score() < 70) {
            JOptionPane.showMessageDialog(null, "Password is too weak.", "Change Password", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!java.util.Arrays.equals(newPass, conf)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match.", "Change Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long ver = EngineerCredentialStore.getVersion();
        boolean updated = EngineerCredentialStore.updatePassword(newPass, ver);
        if (!updated) {
            JOptionPane.showMessageDialog(null, "Password update failed. Please retry.", "Sync Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        refreshPasswordUi();
        JOptionPane.showMessageDialog(null, "Password updated.", "Change Password", JOptionPane.INFORMATION_MESSAGE);
    }

    private record Strength(int score, String label, String hint) {}

    private static Strength strength(char[] pass) {
        if (pass == null) {
            pass = new char[0];
        }
        String s = new String(pass);
        int score = 0;
        boolean lower = s.matches(".*[a-z].*");
        boolean upper = s.matches(".*[A-Z].*");
        boolean digit = s.matches(".*\\d.*");
        boolean sym = s.matches(".*[^A-Za-z0-9].*");
        int len = s.length();

        if (len >= 8) score += 25;
        if (len >= 12) score += 15;
        if (lower) score += 15;
        if (upper) score += 15;
        if (digit) score += 15;
        if (sym) score += 15;
        if (len < 8) score = Math.min(score, 35);

        String label;
        if (score >= 85) label = "Strong";
        else if (score >= 70) label = "Good";
        else if (score >= 50) label = "Weak";
        else label = "Very weak";

        StringBuilder hint = new StringBuilder();
        if (len < 12) hint.append("Use 12+ characters. ");
        if (!upper) hint.append("Add uppercase. ");
        if (!lower) hint.append("Add lowercase. ");
        if (!digit) hint.append("Add digits. ");
        if (!sym) hint.append("Add symbols. ");
        String h = hint.isEmpty() ? "Meets recommended complexity." : hint.toString().trim();

        return new Strength(Math.min(100, score), label, h);
    }
}
