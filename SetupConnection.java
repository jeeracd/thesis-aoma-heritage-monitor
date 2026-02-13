import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class SetupConnection extends JFrame {

    public SetupConnection() {
        setTitle("AOMA-Heritage Monitor - Setup & Connection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane setupTabs = new JTabbedPane(JTabbedPane.TOP);
        setupTabs.setFont(new Font("Arial", Font.BOLD, 17));
        setupTabs.setBackground(Color.LIGHT_GRAY);
        setupTabs.setForeground(Color.BLACK);

        //serves as the panel - pang call
        JPanel setupPanel = new JPanel();
        setupPanel.setLayout(null);

        JPanel buildingInfoPanel = new JPanel();
        buildingInfoPanel.setLayout(null);
        buildingInfoPanel.setBounds(10, 10, 350, 300);

        Border buildingBorder = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder buildingTitle = BorderFactory.createTitledBorder(buildingBorder,"Building Profile Information");
        buildingTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        buildingTitle.setTitleColor(Color.BLACK);
        buildingInfoPanel.setBorder(buildingTitle);
        setupPanel.add(buildingInfoPanel);

        //deets for Building Information and Edit Structural Details
        JLabel buildingNameLabel = new JLabel("Building Name:");
        buildingNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        buildingNameLabel.setBounds(20, 30, 150, 25);
        buildingInfoPanel.add(buildingNameLabel);

        JLabel dateConstructedLabel = new JLabel("Date Constructed:");
        dateConstructedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateConstructedLabel.setBounds(20, 50, 150, 25);
        buildingInfoPanel.add(dateConstructedLabel);
        
        JLabel materialsUsedLabel = new JLabel("Materials Used:");
        materialsUsedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        materialsUsedLabel.setBounds(20, 70, 150, 25);
        buildingInfoPanel.add(materialsUsedLabel);

        JLabel conservationStatusLabel = new JLabel("Conservation Status:");
        conservationStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        conservationStatusLabel.setBounds(20, 90, 150, 25);
        buildingInfoPanel.add(conservationStatusLabel);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressLabel.setBounds(20, 110, 150, 25);
        buildingInfoPanel.add(addressLabel);

        JLabel functionLabel = new JLabel("Function:");
        functionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        functionLabel.setBounds(20, 130, 150, 25);
        buildingInfoPanel.add(functionLabel);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descriptionLabel.setBounds(20, 150, 150, 25);
        buildingInfoPanel.add(descriptionLabel);

        




        JPanel esp32StatusPanel = new JPanel();
        esp32StatusPanel.setLayout(null);
        esp32StatusPanel.setBounds(10, 320, 350, 420);

        Border espBorder = BorderFactory.createLineBorder(Color.GRAY);
        TitledBorder espTitle = BorderFactory.createTitledBorder(espBorder,"ESP32 Status");
        espTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        espTitle.setTitleColor(Color.BLACK);
        esp32StatusPanel.setBorder(espTitle);
        setupPanel.add(esp32StatusPanel);

        setupTabs.addTab("Setup & Connection", setupPanel);
        setupTabs.addTab("Analysis", new JPanel());
        setupTabs.addTab("Report", new JPanel());

        setupTabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(
                    java.awt.Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h,
                    boolean isSelected) {

                g.setColor(isSelected ? new Color(0, 102, 204) : Color.LIGHT_GRAY);
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
                g.drawString(title,
                        textRect.x,
                        textRect.y + metrics.getAscent());
            }
        });

        add(setupTabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SetupConnection();
    }

    

}


