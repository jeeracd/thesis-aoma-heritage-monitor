import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class OfficerDashboardEmailPassword {

    public JPanel createEmailPasswordTab() {
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
        tabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tabPanel.add(createEmailInfoSection());
        tabPanel.add(Box.createVerticalStrut(40));
        tabPanel.add(createSaveButton());

        return tabPanel;
    }

    private JPanel createEmailInfoSection() {
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

        JLabel infoLabel = new JLabel("Your user account is registered with the email address & password:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel emailLabel = new JLabel("JuanDelaCruz@officer.com");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String[] realPassword = { "dummypassword123" };
        boolean[] isVisible = { false };

        JLabel passwordLabel = new JLabel("********");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton eyeButton = new JButton("👁");
        eyeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eyeButton.setFocusPainted(false);
        eyeButton.setPreferredSize(new Dimension(40, 26));

        eyeButton.addActionListener(e -> {
            if (isVisible[0]) {
                passwordLabel.setText("********");
            } else {
                passwordLabel.setText(realPassword[0]);
            }
            isVisible[0] = !isVisible[0];
        });

        JPanel passwordWrapper = new JPanel(new BorderLayout(5, 0));
        passwordWrapper.setOpaque(false);
        passwordWrapper.add(eyeButton, BorderLayout.WEST);
        passwordWrapper.add(passwordLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton changeEmailBtn = createOutlinedButton("Change Email");
        changeEmailBtn.setEnabled(false);
        JButton changePasswordBtn = createOutlinedButton("Change Password");

        changeEmailBtn.addActionListener(e -> {
            String newEmail = JOptionPane.showInputDialog(
                    panel,
                    "Enter new email address:",
                    emailLabel.getText()
            );

            if (newEmail != null && !newEmail.trim().isEmpty()) {
                emailLabel.setText(newEmail.trim());
            }
        });

        changePasswordBtn.addActionListener(e -> {
            JPasswordField pwdField = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(
                    panel,
                    pwdField,
                    "Enter new password",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newPassword = new String(pwdField.getPassword());
                if (!newPassword.isEmpty()) {
                    realPassword[0] = newPassword;
                    if (isVisible[0]) {
                        passwordLabel.setText(realPassword[0]);
                    } else {
                        passwordLabel.setText("********");
                    }
                }
            }
        });

        buttonPanel.add(changeEmailBtn);
        buttonPanel.add(changePasswordBtn);

        gbc.gridy = 0;
        panel.add(loginMethodLabel, gbc);

        gbc.gridy = 1;
        panel.add(infoLabel, gbc);

        gbc.gridy = 2;
        panel.add(emailLabel, gbc);

        gbc.gridy = 3;
        panel.add(passwordWrapper, gbc);

        gbc.gridy = 4;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JButton createOutlinedButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createSaveButton() {
    JButton saveButton = new JButton("Save Changes & Exit");
    saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    saveButton.setPreferredSize(new Dimension(220, 45));
    saveButton.setFocusPainted(false);

    saveButton.addActionListener(e -> {
        JOptionPane.showMessageDialog(
                null,
                "Changes saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        new OfficerStartingPage().setVisible(true);

        SwingUtilities.getWindowAncestor(saveButton).dispose();
        });

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(saveButton);

        return wrapper;
    }
}
