import javax.swing.JFrame;

public class HeadLogin extends JFrame {
    public HeadLogin() {
        setTitle("AOMA-Heritage Monitor - LGU Head Login");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }
public static void main(String[] args) {
    new HeadLogin();
}
}