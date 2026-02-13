import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import com.fazecast.jSerialComm.SerialPort; //this is used for connecting the ESP32 


/*
in order to use the jserialcomm for the mean time type this in the terminal mga pre::

javac -cp .:jserialcomm-2.11.4.jar LandingPage.java
java  -cp .:jserialcomm-2.11.4.jar LandingPage

*/


public class LandingPage extends JFrame implements ActionListener {

    JButton landingButton;
    JLabel ESP32StatusLabel;

    LandingPage() {
        this.setTitle("AOMA-Heritage Monitor - Landing Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 850); 
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

        // Bottom left panel for ESP32 status
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ESP32StatusLabel = new JLabel("Status: ESP32 Hub Not Connected");
        ESP32StatusLabel.setFont(new Font("Arial", Font.BOLD, 15));
        ESP32StatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.add(ESP32StatusLabel);
        bottomPanel.add(Box.createHorizontalGlue());

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == landingButton) {
            //System.out.println("Test click in landing page button");
            boolean esp32Connected = checkESP32Connection();
            if(esp32Connected){
                ESP32StatusLabel.setText("Status: ESP32 Hub Connected");
                ESP32StatusLabel.setForeground(Color.GREEN);
            }
            else{
                ESP32StatusLabel.setText("Status: ESP32 Hub Not Connected");
                ESP32StatusLabel.setForeground(Color.RED);
            }
        }
    }
    private boolean checkESP32Connection() {
        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {
            System.out.println("Detected port: " + port.getDescriptivePortName());

            if (port.getDescriptivePortName().toLowerCase().contains("usb")
                    || port.getDescriptivePortName().toLowerCase().contains("cp210")
                    || port.getDescriptivePortName().toLowerCase().contains("ch340")) {

                return true; // ESP32 detected
            }
        }
        return false; // ESP32 not found
    }

    public static void main(String[] args) {
        new LandingPage();
    }
}
