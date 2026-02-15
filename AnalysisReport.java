import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class AnalysisReport extends JPanel {

    public AnalysisReport() {
        setLayout(null);
        setBounds(0, 0, 1400, 850);

         // MAIN container panel (your existing panel)
        JPanel analysisReportPanel = new JPanel(null);
        setPreferredSize(new java.awt.Dimension(1400, 850));
        analysisReportPanel.setBounds(0, 0, 1400, 850);

        JPanel buildingInfoPanel = new JPanel(null);
        buildingInfoPanel.setBounds(10, 10, 350, 300);

        //building information panel
        Border buildingBorder = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder buildingTitle = BorderFactory.createTitledBorder(buildingBorder,"Building Profile Information");
        buildingTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        buildingTitle.setTitleColor(Color.BLACK);
        buildingInfoPanel.setBorder(buildingTitle);
        analysisReportPanel.add(buildingInfoPanel);
        
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
        analysisReportPanel.add(esp32StatusPanel);

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

        //vibration data panel
        JPanel vibrationDataPanel = new JPanel(new BorderLayout());
        vibrationDataPanel.setBounds(370, 20, 700, 720);
        Border descriptionBorder = BorderFactory.createLineBorder(Color.GRAY);
        vibrationDataPanel.setBorder(descriptionBorder);

        JLabel vibrationDataLabel = new JLabel("Vibration Data",JLabel.LEFT);
        vibrationDataLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        vibrationDataLabel.setFont(new Font("Arial", Font.BOLD, 20));
        vibrationDataPanel.add(vibrationDataLabel, BorderLayout.NORTH);
        analysisReportPanel.add(vibrationDataPanel);

        JPanel vibrationCenterWrapper = new JPanel(null); 
        vibrationDataPanel.add(vibrationCenterWrapper, BorderLayout.CENTER);

        JPanel spectogramPanel = new JPanel(new BorderLayout());
        spectogramPanel.setBounds(5, 0, 690, 350); 
        spectogramPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                        "Spectrogram",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 17)));

        vibrationCenterWrapper.add(spectogramPanel);

        JPanel pyOmaPanel = new JPanel(null);
        pyOmaPanel.setBounds(5, 360, 350, 300);
        pyOmaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                        "PyOma2 Data",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 17)));
        vibrationCenterWrapper.add(pyOmaPanel);

        //oma analysis results 1 panel
        JPanel omaAnalysisResults1Panel = new JPanel(null);
        omaAnalysisResults1Panel.setBounds(360, 360, 330, 300);
        omaAnalysisResults1Panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                        "OMA Analysis Results",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 17)));
        vibrationCenterWrapper.add(omaAnalysisResults1Panel);

        JLabel riskLevelLabel = new JLabel("Risk Level:");
        riskLevelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        riskLevelLabel.setBounds(20, 30, 150, 25);
        omaAnalysisResults1Panel.add(riskLevelLabel);

        JLabel conditionLabel = new JLabel("Condition:");
        conditionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        conditionLabel.setBounds(20, 70, 150, 25);
        omaAnalysisResults1Panel.add(conditionLabel);

        JLabel RecommendedActionsLabel = new JLabel("Recommended Actions:");
        RecommendedActionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        RecommendedActionsLabel.setBounds(20, 170, 200, 25);
        omaAnalysisResults1Panel.add(RecommendedActionsLabel);


        //oma analysis results 2 panel
        JPanel omaAnalysisResults2Panel = new JPanel();
        omaAnalysisResults2Panel.setLayout(null);
        omaAnalysisResults2Panel.setBounds(370 + 700 + 10,10,310,400);

        Border omaAnalysisResults2Border = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder omaAnalysisResults2Title = BorderFactory.createTitledBorder(omaAnalysisResults2Border, "OMA Analysis Results");
        omaAnalysisResults2Title.setTitleFont(new Font("Arial", Font.BOLD, 17));
        omaAnalysisResults2Title.setTitleColor(Color.BLACK);
        omaAnalysisResults2Panel.setBorder(omaAnalysisResults2Title);       
        analysisReportPanel.add(omaAnalysisResults2Panel);

        JLabel naturalFrequenciesLabel = new JLabel("Natural Frequencies:");
        naturalFrequenciesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        naturalFrequenciesLabel.setBounds(20, 30, 150, 25);
        omaAnalysisResults2Panel.add(naturalFrequenciesLabel);

        JLabel dampingRatiosLabel = new JLabel("Damping Ratios:");
        dampingRatiosLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dampingRatiosLabel.setBounds(20, 170, 150, 25);
        omaAnalysisResults2Panel.add(dampingRatiosLabel);

        JLabel structuralChangesLabel = new JLabel("Structural Changes:");
        structuralChangesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        structuralChangesLabel.setBounds(20, 300, 150, 25);
        omaAnalysisResults2Panel.add(structuralChangesLabel);

        //system logs panel
        JPanel systemLogsPanel = new JPanel();
        systemLogsPanel.setLayout(null);
        systemLogsPanel.setBounds(370 + 700 + 10, 60 + 350 + 10, 310, 320);

        Border systemLogsBorder = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder systemLogsTitle = BorderFactory.createTitledBorder(systemLogsBorder, "System Logs");
        systemLogsTitle.setTitleFont(new Font("Arial", Font.BOLD, 17));
        systemLogsTitle.setTitleColor(Color.BLACK);
        systemLogsPanel.setBorder(systemLogsTitle);
        analysisReportPanel.add(systemLogsPanel);

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
        analysisReportPanel.add(sensorConnectionStatusLabel);

        add(analysisReportPanel);
        
    }

    public static void main(String[] args) {
        new AnalysisReport();
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
