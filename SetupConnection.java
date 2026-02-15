import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

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
    
        //building information panel
        Border buildingBorder = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder buildingTitle = BorderFactory.createTitledBorder(buildingBorder,"Building Profile Information");
        buildingTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        buildingTitle.setTitleColor(Color.BLACK);
        buildingInfoPanel.setBorder(buildingTitle);
        setupPanel.add(buildingInfoPanel);

        // Placing a small "Edit" button in the top-right corner of the panel
        JButton editBuildingBtn = new JButton("Edit");
        editBuildingBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        editBuildingBtn.setBounds(270, 20, 60, 25); // Positioned top-right inside the panel
        editBuildingBtn.setFocusable(false);
        editBuildingBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Open the new EditStructuralDetails window
                new EditStructuralDetails();
            }
        });
        buildingInfoPanel.add(editBuildingBtn);


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

        //esp32 status panel
        JPanel esp32StatusPanel = new JPanel();
        esp32StatusPanel.setLayout(null);
        esp32StatusPanel.setBounds(10, 320, 350, 420);

        Border espBorder = BorderFactory.createLineBorder(Color.GRAY);
        TitledBorder espTitle = BorderFactory.createTitledBorder(espBorder,"ESP32 Status");
        espTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        espTitle.setTitleColor(Color.BLACK);
        esp32StatusPanel.setBorder(espTitle);
        setupPanel.add(esp32StatusPanel);

        // Placing a small "Configure Sensors" button in the top-right corner of the panel
        JButton configureSensorsBtn = new JButton("Configure Sensors");
        configureSensorsBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        configureSensorsBtn.setFocusable(false);
        configureSensorsBtn.setBounds(340 - 10 - 90, 3, 100, 22);

        configureSensorsBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Open the new ConfigureSensors window
                new ConfigureSensors();
            }
        });
        esp32StatusPanel.add(configureSensorsBtn);


        String[] sensorCols = {"Sensor ID", "Location", "Status"};
        Object[][] sensorData = {{"Sensor 1-Test", "Lobby", "Connected"},
                                 {"Sensor 2-Test", "Hallway", "Connected"},
                                 {"Sensor 3-Test", "Roof", "Disconnected"},
                                 {"Sensor 4-Test", "Basement", "Connected"},
                                 {"Sensor 5-Test", "Garden", "Disconnected"}};

        JTable sensorTable = new JTable(sensorData, sensorCols);
        sensorTable.setFont(new Font("Arial", Font.BOLD, 13));
        sensorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        sensorTable.setRowHeight(24);
        sensorTable.setFillsViewportHeight(true);
        sensorTable.setFocusable(true);

        //helps centerin the data in the table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < sensorTable.getColumnCount(); i++) {
            sensorTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        sensorTable.getColumnModel().getColumn(2).setCellRenderer(new StatusColorRenderer());

        ((DefaultTableCellRenderer) sensorTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane sensorScrollPane = new JScrollPane(sensorTable);
        sensorScrollPane.setBounds(10, 30, 330, 380);
        sensorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        esp32StatusPanel.setComponentZOrder(sensorScrollPane, 0);
        esp32StatusPanel.revalidate();
        esp32StatusPanel.repaint();
        esp32StatusPanel.add(sensorScrollPane);

        


        //description panel
        JPanel centerPanelDescription = new JPanel(new BorderLayout());
        centerPanelDescription.setBounds(370, 20, 1020, 40);
        Border descriptionBorder = BorderFactory.createLineBorder(Color.GRAY);
        centerPanelDescription.setBorder(descriptionBorder);

        JLabel centerTitleLabel = new JLabel("Connect the ESP32 hub to monitor the condition of a heritage building",JLabel.CENTER);
        centerTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerTitleLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
        centerPanelDescription.add(centerTitleLabel, BorderLayout.NORTH);
        setupPanel.add(centerPanelDescription);

        //configure sensor panel
        JPanel configureSensorPanel = new JPanel(new BorderLayout());
        configureSensorPanel.setBounds(370, 70, 700, 670);
        Border configureBorder = BorderFactory.createLineBorder(Color.GRAY);
        configureSensorPanel.setBorder(configureBorder);

        JLabel configureTitleLabel = new JLabel("Configure Sensors", JLabel.LEFT);
        configureTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        configureTitleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        configureSensorPanel.add(configureTitleLabel, BorderLayout.NORTH);
        setupPanel.add(configureSensorPanel);

        String configureSensorCols[] = {"Sensor ID", "Location", "Status", "Device Type"};
        Object configureSensorData[][] = {{"Sensor 1-Test", "Lobby", "Connected", "HUB"},
                                         {"Sensor 2-Test", "Hallway", "Connected", "ACCELEROMETER"},
                                         {"Sensor 3-Test", "Roof", "Disconnected", "ACCELEROMETER"},
                                         {"Sensor 4-Test", "Basement", "Connected", "ACCELEROMETER"},
                                         {"Sensor 5-Test", "Garden", "Disconnected", "ACCELEROMETER"}};

        JTable configureSensorTable = new JTable(configureSensorData, configureSensorCols);
        configureSensorTable.setFont(new Font("Arial", Font.BOLD, 13));
        configureSensorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        configureSensorTable.setRowHeight(24);
        configureSensorTable.setFillsViewportHeight(true);
        configureSensorTable.setFocusable(true);

        //helps centerin the data in the table
        DefaultTableCellRenderer configureCenterRenderer = new DefaultTableCellRenderer();
        configureCenterRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < configureSensorTable.getColumnCount(); i++) {
            configureSensorTable.getColumnModel().getColumn(i).setCellRenderer(configureCenterRenderer);
        }

        configureSensorTable.getColumnModel().getColumn(2).setCellRenderer(new StatusColorRenderer());

        //helps centerin the header of the table
        ((DefaultTableCellRenderer) configureSensorTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane configureSensorScrollPane = new JScrollPane(configureSensorTable);
        configureSensorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        configureSensorPanel.add(configureSensorScrollPane, BorderLayout.CENTER);

        //sensor status panel inside configure sensor panel
        JPanel sensorStatusWrapper = new JPanel(new BorderLayout());
        sensorStatusWrapper.setOpaque(false);

        JPanel sensorStatusPanel = new JPanel(new BorderLayout());
        sensorStatusPanel.setPreferredSize(new java.awt.Dimension(690, 200));

        TitledBorder sensorStatusBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
        "Sensor Status");
        sensorStatusBorder.setTitleFont(new Font("Arial", Font.BOLD, 15));
        sensorStatusPanel.setBorder(sensorStatusBorder);

        sensorStatusWrapper.add(sensorStatusPanel, BorderLayout.WEST);
        configureSensorPanel.add(sensorStatusWrapper, BorderLayout.SOUTH);

        //sensor setup guide panel
        JPanel sensorSetupGuidePanel = new JPanel();
        sensorSetupGuidePanel.setLayout(null);
        sensorSetupGuidePanel.setBounds(370 + 700 + 10,60,310,350);

        Border sensorSetupGuideBorder = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder sensorSetupGuideTitle = BorderFactory.createTitledBorder(sensorSetupGuideBorder, "Sensor Setup Guide");
        sensorSetupGuideTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        sensorSetupGuideTitle.setTitleColor(Color.BLACK);
        sensorSetupGuidePanel.setBorder(sensorSetupGuideTitle);
        setupPanel.add(sensorSetupGuidePanel);

        JPanel systemLogsPanel = new JPanel();
        systemLogsPanel.setLayout(null);
        systemLogsPanel.setBounds(370 + 700 + 10, 60 + 350 + 10, 310, 320);

        Border systemLogsBorder = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder systemLogsTitle = BorderFactory.createTitledBorder(systemLogsBorder, "System Logs");
        systemLogsTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        systemLogsTitle.setTitleColor(Color.BLACK);
        systemLogsPanel.setBorder(systemLogsTitle);
        setupPanel.add(systemLogsPanel);

        String systemLogsCols[] = {"Time", "Sensor ID", "Status"};
        Object systemLogsData[][] = {{"10:00:00", "Sensor 1-Test", "Connected"},
                                     {"10:05:00", "Sensor 2-Test", "Connected"},
                                     {"10:10:00", "Sensor 3-Test", "Disconnected"},
                                     {"10:15:00", "Sensor 4-Test", "Connected"},
                                     {"10:20:00", "Sensor 5-Test", "Disconnected"}};
        JTable systemLogsTable = new JTable(systemLogsData, systemLogsCols);
        systemLogsTable.setFont(new Font("Arial", Font.BOLD, 13));
        systemLogsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        systemLogsTable.setRowHeight(24);
        systemLogsTable.setFillsViewportHeight(true);
        systemLogsTable.setFocusable(true);
        
        //helps centerin the data in the table
        DefaultTableCellRenderer systemLogsCenterRenderer = new DefaultTableCellRenderer();
        systemLogsCenterRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < systemLogsTable.getColumnCount(); i++) {
            systemLogsTable.getColumnModel().getColumn(i).setCellRenderer(systemLogsCenterRenderer);
        }

        systemLogsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusColorRenderer());

        //helps centerin the header of the table
        ((DefaultTableCellRenderer) systemLogsTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        JScrollPane systemLogsScrollPane = new JScrollPane(systemLogsTable);
        systemLogsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        systemLogsScrollPane.setBounds(10, 30, 290, 270);
        systemLogsPanel.add(systemLogsScrollPane);

        JLabel sensorConnectionStatusLabel = new JLabel("Status: ESP32 Hub Connected", JLabel.LEFT);
        sensorConnectionStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sensorConnectionStatusLabel.setForeground(new Color(0, 153, 0));

        // BELOW esp32StatusPanel (outside it)
        sensorConnectionStatusLabel.setBounds(10, 320 + 420 + 10, 350,25);
        setupPanel.add(sensorConnectionStatusLabel);

        setupTabs.addTab("Setup & Connection", setupPanel);
        setupTabs.addTab("Analysis", new AnalysisReport());
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
                g.drawString(title,textRect.x, textRect.y + metrics.getAscent());
            }
        });

        add(setupTabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SetupConnection();
    }
class StatusColorRenderer extends DefaultTableCellRenderer {
    @Override
    public void setValue(Object value) {
        super.setValue(value);

        if (value != null) {
            String text = value.toString().toLowerCase();

            if (text.equals("connected")) {
                setForeground(new Color(0, 153, 0)); // green
            } else if (text.equals("disconnected")) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.BLACK);
            }
        }
        setHorizontalAlignment(JLabel.CENTER);
    }
}

}

