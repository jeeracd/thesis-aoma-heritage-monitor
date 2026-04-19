import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class EngineerViewDetailsApprove extends JFrame {

    private JButton certifyButton;
    private JCheckBox certifyCheckBox;

    public EngineerViewDetailsApprove() {
        setTitle("AOMA-Heritage Monitor");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        add(mainPanel);

        // ===== TITLE (CENTERED) =====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Certify Monitoring Session #20260224");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);

        mainPanel.add(Box.createVerticalStrut(15));

        // ===== CONTENT PANEL (LEFT BLOCK, NOT STRETCHED) =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setOpaque(false);

        // Status
        JLabel statusLabel = new JLabel("Status: SAFE / SERVICEABLE");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description
        JLabel desc1 = new JLabel("By approving, this report will be finalized and sent to the LGU Head");
        JLabel desc2 = new JLabel("as an official record.");
        desc1.setFont(new Font("Arial", Font.PLAIN, 12));
        desc2.setFont(new Font("Arial", Font.PLAIN, 12));
        desc1.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Checkbox + text
        certifyCheckBox = new JCheckBox("I certify that I have reviewed the Spectrograms and Modal");
        certifyCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel line2 = new JLabel("Assurance Criterion (MAC) values and confirm the automated");
        JLabel line3 = new JLabel("analysis is accurate.");
        line2.setFont(new Font("Arial", Font.PLAIN, 12));
        line3.setFont(new Font("Arial", Font.PLAIN, 12));
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);
        line3.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Align lines with checkbox text (not the box)
        int indent = 20;
        line2.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));
        line3.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

        // Add to content panel
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(desc1);
        contentPanel.add(desc2);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(certifyCheckBox);
        contentPanel.add(line2);
        contentPanel.add(line3);

        // ===== WRAPPER (THIS PREVENTS CENTER LOOK) =====
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapperPanel.setOpaque(false);

        contentPanel.setPreferredSize(new Dimension(520, contentPanel.getPreferredSize().height));

        wrapperPanel.add(contentPanel);
        mainPanel.add(wrapperPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // ===== BUTTONS (CENTERED) =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton closeButton = new JButton("close");
        closeButton.setForeground(Color.RED);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        certifyButton = new JButton("CERTIFY & PUBLISH");
        certifyButton.setEnabled(false);

        certifyButton.setOpaque(true);
        certifyButton.setBorderPainted(false);
        certifyButton.setFocusPainted(false);

        buttonPanel.add(closeButton);
        buttonPanel.add(certifyButton);
        mainPanel.add(buttonPanel);

        //LOGIC
        certifyCheckBox.addItemListener(e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
        certifyButton.setEnabled(true);
        certifyButton.setBackground(new Color(0, 153, 0));
        certifyButton.setForeground(Color.WHITE);
        certifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    } else {
        certifyButton.setEnabled(false);
        certifyButton.setBackground(Color.LIGHT_GRAY);
        certifyButton.setForeground(Color.BLACK);
        certifyButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    });
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

        certifyButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Report certified and published successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE      
            );
            new EngineerViewDetails(); // Open the details view again (simulate going back to details)
            this.dispose(); // Close the approval window
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EngineerViewDetailsApprove::new);
    }
}