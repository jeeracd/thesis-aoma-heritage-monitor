import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EngineerViewDetailsReject extends JFrame {

    public EngineerViewDetailsReject() {
        setTitle("AOMA-Heritage Monitor");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        add(mainPanel);

        // Title
        JLabel titleLabel = new JLabel("Reject Monitoring Session #20260224-OMA-005");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // 👈 this centers it

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Warning text
        JLabel warningLabel = new JLabel("You are about to flag this dataset as invalid. This action cannot be undone.");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(warningLabel, gbc);

        // Reason label
        gbc.gridy++;
        gbc.gridwidth = 1;
        centerPanel.add(new JLabel("Reason for Rejection:"), gbc);

        // Reason area (multi-line like remarks)
        JTextArea reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        JScrollPane reasonScroll = new JScrollPane(reasonArea);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH; 
        centerPanel.add(reasonScroll, gbc);

        // Engineer remarks label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        centerPanel.add(new JLabel("Engineer Remarks:"), gbc);

        // Remarks area
        JTextArea remarksArea = new JTextArea(3, 20);
        remarksArea.setText("");
        JScrollPane scrollPane = new JScrollPane(remarksArea);

        gbc.gridx = 1;
        gbc.weightx = 1;
        centerPanel.add(scrollPane, gbc);

        // Bottom panel (buttons)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Close button
        JButton closeButton = new JButton("close");
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(closeButton);

        // Confirm button
        JButton confirmButton = new JButton("CONFIRM REJECTION");
        confirmButton.setBackground(new Color(200, 40, 40));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setPreferredSize(new Dimension(200, 35));

        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        confirmButton.setContentAreaFilled(true);

        buttonPanel.add(confirmButton);

        // Actions
        closeButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Redirecting to Details View.",
                    "View Details",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerViewDetails(); // Open the details view again (simulate going back to details)
            dispose();
        });

        confirmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Dataset has been rejected.",
                    "Rejected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerViewDetails(); // Open the details view again (simulate going back to details)
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EngineerViewDetailsReject::new);
    }
}