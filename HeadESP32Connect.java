import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import com.fazecast.jSerialComm.SerialPort; //this is used for connecting the ESP32 


/*
in order to use the jserialcomm tapos mag run ang landing page for the mean time type this in the vsc terminal mga pre::

For Mac/Linux
javac -cp .:jserialcomm-2.11.4.jar HeadESP32Connect.java
java  -cp .:jserialcomm-2.11.4.jar HeadESP32Connect

For Windows
javac -cp .;jserialcomm-2.11.4.jar HeadESP32Connect.java
java  -cp .;jserialcomm-2.11.4.jar HeadESP32Connect

*/

public class HeadESP32Connect extends JFrame implements ActionListener {

    JButton ESP32ConnectButton;
    JLabel ESP32StatusLabel;

    HeadESP32Connect() {
        this.setTitle("AOMA-Heritage Monitor - ESP32 Connect");
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

        ESP32ConnectButton = new JButton("CONNECT ESP32 HUB (via Wired Connection)");
        ESP32ConnectButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
        ESP32ConnectButton.setPreferredSize(new Dimension(400, 50));
        ESP32ConnectButton.setMaximumSize(new Dimension(400, 50));
        ESP32ConnectButton.setMinimumSize(new Dimension(400, 50));
        ESP32ConnectButton.setFont(new Font("Arial", Font.BOLD, 15));
        ESP32ConnectButton.setFocusable(false);
        ESP32ConnectButton.setForeground(Color.WHITE);
        ESP32ConnectButton.setBackground(Color.BLUE);
        ESP32ConnectButton.setOpaque(true);
        ESP32ConnectButton.setContentAreaFilled(true);
        ESP32ConnectButton.setBorderPainted(false);
        ESP32ConnectButton.addActionListener(this);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(imageLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30))); 
        centerPanel.add(ESP32ConnectButton);
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
        if (e.getSource() == ESP32ConnectButton) {
            testAndConnectESP32();
        }
    }

    private void testAndConnectESP32() {

        ESP32StatusLabel.setText("Status: Scanning ports...");
        ESP32StatusLabel.setForeground(Color.BLACK);

        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {

            String sysName = port.getSystemPortName();
            String desc = port.getDescriptivePortName();

            System.out.println("Trying port: " + sysName + " (" + desc + ")");

            // Skip Bluetooth & virtual ports
            if (desc.toLowerCase().contains("bluetooth")
                    || desc.toLowerCase().contains("dial")
                    || desc.toLowerCase().contains("debug")) {
                continue;
            }

            port.setBaudRate(115200);
            port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

            if (port.openPort()) {

                System.out.println("ESP32 connected on: " + sysName);
                port.closePort();

                ESP32StatusLabel.setText("Status: Connected (" + sysName + ")");
                ESP32StatusLabel.setForeground(Color.GREEN);

                Timer timer = new Timer(5000, evt -> {
                    dispose();
                    new HeadConfigureSensors();
                });
                timer.setRepeats(false);
                timer.start();

                return;
            }
        }

        ESP32StatusLabel.setText("Status: ESP32 Hub Not Connected");
        ESP32StatusLabel.setForeground(Color.RED);
    }

    public static void main(String[] args) {
        new HeadESP32Connect();
    }
}
