import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SetupConnection extends JFrame {

    public SetupConnection() {
        setTitle("AOMA-Heritage Monitor - Setup & Connection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane setupTabs = new JTabbedPane(JTabbedPane.TOP);

        setupTabs.addTab("Setup & Connection", new JPanel());
        setupTabs.addTab("Analysis", new JPanel());
        setupTabs.addTab("Report", new JPanel());
        setupTabs.setFont(new Font("Arial", Font.BOLD,17));
        setupTabs.setBackground(Color.LIGHT_GRAY);
        setupTabs.setForeground(Color.BLACK);
        

        setupTabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(
                    java.awt.Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h,
                    boolean isSelected) {

                if (isSelected) {
                    g.setColor(new Color(0, 102, 204)); 
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(x, y, w, h);
            }

            @Override
            protected void paintText(
                    java.awt.Graphics g, int tabPlacement,
                    java.awt.Font font, java.awt.FontMetrics metrics,
                    int tabIndex, String title,
                    java.awt.Rectangle textRect,
                    boolean isSelected) {

                g.setFont(font);
                g.setColor(isSelected ? Color.WHITE : Color.BLACK);
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }
        });

        add(setupTabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SetupConnection();
    }
}
