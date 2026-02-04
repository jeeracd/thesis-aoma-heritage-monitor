
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class LandingPage extends JFrame implements ActionListener{

    JButton landingButton;

    LandingPage(){
        landingButton = new JButton();
        landingButton.setBounds(200, 100, 400, 100);
        landingButton.addActionListener(this);
        landingButton.setText("CONNECT ESP32 HUB (via Wired Connection)");
        landingButton.setFocusable(false);
        landingButton.setFont(new Font("Arial", Font.BOLD,15));
        landingButton.setOpaque(true);
        landingButton.setForeground(Color.WHITE);
        landingButton.setBorderPainted(false);
        landingButton.setBackground(Color.BLUE);
        

        this.setTitle("AOMA-Heritage Monitor - Landing Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(1920, 1080);
        this.setVisible(true);

        this.add(landingButton); //show button to the frame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == landingButton) { //action to be performed when button is clicked
            System.out.println("Test click in landing page button");
        }
    }

    public static void main(String[] args) {
        new LandingPage();
    }
}
