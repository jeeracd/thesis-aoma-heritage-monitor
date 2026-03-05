import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CreateAccountConfirmation extends JFrame {

    public CreateAccountConfirmation() {
        setTitle("Account Created");
        setSize(420, 260);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Creation of Account is Successful!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(40, 40, 40));

        JLabel subtitleLabel = new JLabel("Click the button below to proceed to the Login.");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(90, 90, 90));

        JButton proceedButton = new JButton("Proceed");
        proceedButton.setMaximumSize(new Dimension(100, 45));
        proceedButton.setFont(new Font("Arial", Font.BOLD, 14));
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        proceedButton.setFocusPainted(false);

        proceedButton.addActionListener(e -> {
            new UsersLoginOptions().setVisible(true);
            dispose();
        });

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