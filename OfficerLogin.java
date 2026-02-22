import javax.swing.JFrame;

public class OfficerLogin extends JFrame {
    public OfficerLogin() {
        setTitle("AOMA-Heritage Monitor - LGU Officer Login");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }
public static void main(String[] args) {
    new OfficerLogin();
    
}
}
