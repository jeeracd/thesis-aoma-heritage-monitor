import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CreateAccountConfirmation extends JFrame {

    public CreateAccountConfirmation() {
        setTitle("Account Created");
        setSize(420, 260);
        setLocationRelativeTo(null); // center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Creation of Account is Successful!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(40, 40, 40));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Click the button below to proceed to the Login.");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(90, 90, 90));

        JButton proceedButton = new JButton("Proceed");
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedButton.setFocusPainted(false);
        proceedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        proceedButton.setBackground(new Color(0, 153, 0));
        proceedButton.setForeground(Color.BLACK);
        proceedButton.setPreferredSize(new Dimension(120, 38));
        proceedButton.setMaximumSize(new Dimension(140, 38));
        proceedButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        proceedButton.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
                )
        );

        proceedButton.addActionListener(e -> {
            new LoginOptions().setVisible(true);
            dispose();
        });

        // Spacing
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(proceedButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CreateAccountConfirmation().setVisible(true);
        });
    }
}