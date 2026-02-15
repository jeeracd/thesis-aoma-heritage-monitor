import java.awt.*;
import javax.swing.*;

public class Confirmation extends JDialog {

    private boolean confirmActionTaken = false;

    public Confirmation(Frame owner, String headerText, String messageText, String leftButtonText, String rightButtonText, Color leftBtnColor, Color rightBtnColor) {
        super(owner, "AOMA-Heritage Monitor", true);
        setSize(500, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // for header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel(headerText);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        //message format
        JTextArea messageArea = new JTextArea(messageText);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 13));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setOpaque(false);
        messageArea.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));
        add(messageArea, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));

        JButton leftBtn = new JButton(leftButtonText);
        leftBtn.setFont(new Font("Arial", Font.BOLD, 12));
        leftBtn.setForeground(leftBtnColor);
        leftBtn.setFocusable(false);
        leftBtn.setPreferredSize(new Dimension(120, 30));
        leftBtn.addActionListener(e -> {
            confirmActionTaken = true;
            dispose();
        });

        JButton rightBtn = new JButton(rightButtonText);
        rightBtn.setFont(new Font("Arial", Font.BOLD, 12));
        rightBtn.setForeground(rightBtnColor);
        rightBtn.setFocusable(false);
        rightBtn.setPreferredSize(new Dimension(130, 30));
        rightBtn.addActionListener(e -> {
            confirmActionTaken = false;
            dispose();
        });

        buttonPanel.add(leftBtn);
        buttonPanel.add(rightBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public boolean isConfirmActionTaken() {
        return confirmActionTaken;
    }
}