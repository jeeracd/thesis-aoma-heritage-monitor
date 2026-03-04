import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class OfficerDashboardNotifications {

    public JPanel createNotificationsTab() {
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
        tabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tabPanel.add(createNotificationSection());
        tabPanel.add(Box.createVerticalStrut(50));
        tabPanel.add(createSaveButton());

        return tabPanel;
    }

    private JPanel createNotificationSection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel label = new JLabel("Email me about edits, comments, sharing changes...");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        optionsPanel.setBorder(new LineBorder(Color.BLACK, 2));

        JToggleButton rightAway = createToggleOption("Right away", true);
        JToggleButton onceADay = createToggleOption("Once a day", false);
        JToggleButton never = createToggleOption("Never", false);

        ButtonGroup group = new ButtonGroup();
        group.add(rightAway);
        group.add(onceADay);
        group.add(never);

        optionsPanel.add(rightAway);
        optionsPanel.add(onceADay);
        optionsPanel.add(never);

        wrapper.add(label);
        wrapper.add(optionsPanel);

        return wrapper;
    }

    private JToggleButton createToggleOption(String text, boolean selected) {
        JToggleButton button = new JToggleButton(text, selected);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
        button.setBorder(new LineBorder(Color.BLACK, 1));

        button.setBackground(selected ? new Color(150, 190, 240) : Color.WHITE);
        button.setOpaque(true);

        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setBackground(new Color(150, 190, 240));
            } else {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createSaveButton() {
        JButton saveButton = new JButton("Save Changes & Exit");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(220, 45));
        saveButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
        // Optional: Show confirmation message
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