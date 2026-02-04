import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

class UserLogin {
    public static void main(String[] args) {

        //instantiate JFrame for user login
        JFrame loginFrame = new JFrame();
        loginFrame.setTitle("AOMA-Heritage Monitor - Login Account");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(1920, 1080);
        //loginFrame.getContentPane().setBackground(new Color(0x1A3263)); //background color for user login frame

        //this button will directly go to the landing page when clicked
        new LandingPage(); 




        // create and customize JLabels for title
        JLabel loginTitleLabel = new JLabel();
        loginTitleLabel.setText("AOMA-Heritage Monitor"); 
        loginTitleLabel.setFont(new Font("Arial", Font.BOLD,25)); //title font size
        loginTitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); //centers the label

        // create and customize JLabels for title
        JLabel loginSubtitleLabel = new JLabel();
        loginSubtitleLabel.setText("Login Account"); 
        loginSubtitleLabel.setFont(new Font("Arial", Font.BOLD,20)); //subtitle font size
        loginSubtitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); //centers the label

        //shows the outputs on the frame
        loginFrame.setLayout(new BoxLayout(loginFrame.getContentPane(), BoxLayout.Y_AXIS));
        loginFrame.add(loginTitleLabel);
        loginFrame.add(loginSubtitleLabel);

        //to prevent the frame/display from not showing up dito ko nilagay sa dulo...
        loginFrame.setVisible(true); //this will get the frame to show up

    }
}