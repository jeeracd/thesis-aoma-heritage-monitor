import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
//import com.fazecast.jSerialComm.SerialPort; //this is used for connecting the ESP32 


public class LandingPage extends JFrame implements ActionListener {

    JButton landingButton;

    LandingPage() {
        this.setTitle("AOMA-Heritage Monitor - Landing Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1220, 1080); 
        this.setLocationRelativeTo(null); 
        this.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        ImageIcon esp32Image = new ImageIcon("esp-wroom-32.jpg");
        Image scaledImage = esp32Image.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH); 
        ImageIcon resizedImage = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(resizedImage);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 

        landingButton = new JButton("CONNECT ESP32 HUB (via Wired Connection)");
        landingButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
        landingButton.setPreferredSize(new Dimension(400, 50));
        landingButton.setMaximumSize(new Dimension(400, 50));
        landingButton.setMinimumSize(new Dimension(400, 50));
        landingButton.setFont(new Font("Arial", Font.BOLD, 15));
        landingButton.setFocusable(false);
        landingButton.setForeground(Color.WHITE);
        landingButton.setBackground(Color.BLUE);
        landingButton.setOpaque(true);
        landingButton.setContentAreaFilled(true);
        landingButton.setBorderPainted(false);
        landingButton.addActionListener(this);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(imageLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30))); 
        centerPanel.add(landingButton);
        centerPanel.add(Box.createVerticalGlue());

        this.add(centerPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == landingButton) {
            System.out.println("Test click in landing page button");
        }
    }

    public static void main(String[] args) {
        new LandingPage();
    }
}
