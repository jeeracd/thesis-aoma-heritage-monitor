import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class EngineerDashboardEmailPassword {

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

        JLabel infoLabel = new JLabel("Your user account is registered with the email address:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel emailLabel = new JLabel("JuanDelaCruz@gmail.com");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton changeEmailBtn = createOutlinedButton("Change Email");
        JButton changePasswordBtn = createOutlinedButton("Change Password");

        buttonPanel.add(changeEmailBtn);
        buttonPanel.add(changePasswordBtn);

        gbc.gridy = 0;
        panel.add(loginMethodLabel, gbc);

        gbc.gridy = 1;
        panel.add(infoLabel, gbc);

        gbc.gridy = 2;
        panel.add(emailLabel, gbc);

        gbc.gridy = 3;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JButton createOutlinedButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 36));
        button.setBorder(new LineBorder(Color.BLACK, 1));
        return button;
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
}