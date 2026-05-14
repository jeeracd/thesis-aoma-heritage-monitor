import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class HeadViewReport extends JFrame {

    public HeadViewReport() {
        setTitle("AOMA-Heritage Monitor - View Report");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.HEAD);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", headPanel);
        tabsUI.addTab("Help", new JPanel());

        tabsUI.setSelectedIndex(1); //set default tab

        tabsUI.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
    @Override
        protected void paintTabBackground(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x, int y, int w, int h,
                boolean isSelected
        ) {
            if (isSelected) {
                g.setColor(new Color(0, 102, 204)); 
                g.fillRect(x, y, w, h);
            }
        }

        @Override
        protected void paintText(
                Graphics g,
                int tabPlacement,
                Font font,
                FontMetrics metrics,
                int tabIndex,
                String title,
                Rectangle textRect,
                boolean isSelected
        ) {
            g.setFont(font);
            g.setColor(isSelected ? Color.WHITE : Color.BLACK);
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

        @Override
        protected Insets getTabInsets(int tabPlacement, int tabIndex) {
            return new Insets(6, 20, 6, 20);
        }

        @Override
        protected Insets getTabAreaInsets(int tabPlacement) {
            return new Insets(5, 10, 5, 0);
        }
    });

        //projects menu
        JPopupMenu projectsMenu = new JPopupMenu();

        JMenuItem newProject = new JMenuItem("New Project");
        newProject.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "New Project initialization process will start.",
                    "New Project",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadEditStructuralDetails();
            this.dispose();
        });

        JMenuItem openProject = new JMenuItem("Open Project");
        JMenuItem importCsv = new JMenuItem("Import Sensor Data (.csv)");
        importCsv.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Import Sensor Data page.",
                    "Import Sensor Data",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadImportSensorData();
            this.dispose();
        });

        JMenuItem exportPDF = new JMenuItem("Export Report (PDF)");
        exportPDF.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Export Report page.",
                    "Export Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadExportSensorData();
            this.dispose();
        });

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        projectsMenu.add(newProject);
        projectsMenu.add(openProject);
        projectsMenu.addSeparator();
        projectsMenu.add(importCsv);
        projectsMenu.add(exportPDF);
        projectsMenu.addSeparator();
        projectsMenu.add(exit);

        JButton projectsDropdownBtn = new JButton("▼");
        projectsDropdownBtn.setFont(new Font("Arial", Font.BOLD, 14));
        projectsDropdownBtn.setFocusPainted(false);
        projectsDropdownBtn.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        projectsDropdownBtn.setBackground(Color.LIGHT_GRAY);
        projectsDropdownBtn.setForeground(Color.BLACK);
        projectsDropdownBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        projectsDropdownBtn.setBounds(92,11,28,22);

        projectsDropdownBtn.addActionListener(e ->
        projectsMenu.show(projectsDropdownBtn,0,projectsDropdownBtn.getHeight())
        );

        //project dropdown button position
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(new java.awt.Dimension(1300, 850));

        layeredPane.add(projectsDropdownBtn, JLayeredPane.PALETTE_LAYER); 

        tabsUI.setBounds(0, 0, 1395, 770);
        layeredPane.add(tabsUI, JLayeredPane.DEFAULT_LAYER);

        //VIEW MENU 
        JPopupMenu viewMenu = new JPopupMenu();

        JMenuItem dashboardView = new JMenuItem("Dashboard View");
        dashboardView.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Dashboard View.",
                    "Dashboard View",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadBldgStatusOverview();
            this.dispose();
        });

        JMenuItem setupConnection = new JMenuItem("Setup & Connection");
        setupConnection.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Setup & Connection page.",
                    "Setup & Connection",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadSetupConnectionWindow();
            this.dispose();
        });


        JMenuItem configureSensor = new JMenuItem("Configure Sensor");
        configureSensor.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Configure Sensor page.",
                    "Configure Sensor",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadConfigureSensorWindow();
            this.dispose();
        });

        JMenuItem esp32Status = new JMenuItem("ESP32 Status");
        esp32Status.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to ESP32 Status page.",
                    "ESP32 Status",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadESP32StatusWindow();
            this.dispose();
        });

        JMenuItem vibrationData = new JMenuItem("Vibration Data");
        vibrationData.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Vibration Data page.",
                    "Vibration Data",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadVibrationDataWindow();
            this.dispose();
        }); 

        JMenuItem omaAnalysisResult = new JMenuItem("OMA Analysis Result");
        omaAnalysisResult.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to OMA Analysis Result page.",
                    "OMA Analysis Result",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadOMAAnalysisResultWindow();
            this.dispose();
        });

        JMenuItem reportHistory = new JMenuItem("View Report");
        reportHistory.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to View Report page.",
                    "View Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadViewReport();
            this.dispose();
        });

        JMenuItem systemLogs = new JMenuItem("System Logs");
        systemLogs.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to System Logs page.",
                    "System Logs",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadSystemLogsWindow();
            this.dispose();
        });


        viewMenu.add(dashboardView);
        viewMenu.addSeparator();
        viewMenu.add(setupConnection);
        viewMenu.add(configureSensor);
        viewMenu.add(esp32Status);
        viewMenu.addSeparator();
        viewMenu.add(vibrationData);
        viewMenu.add(omaAnalysisResult);
        viewMenu.addSeparator();
        viewMenu.add(reportHistory);
        viewMenu.addSeparator();
        viewMenu.add(systemLogs);

        JButton viewMenuDropdownBtn = new JButton("▼");
        viewMenuDropdownBtn.setFont(new Font("Arial", Font.BOLD, 14)); // bigger arrow
        viewMenuDropdownBtn.setMargin(new Insets(0, 0, 0, 0));
        viewMenuDropdownBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        viewMenuDropdownBtn.setFocusPainted(false);
        viewMenuDropdownBtn.setContentAreaFilled(false);   
        viewMenuDropdownBtn.setBorderPainted(false);       
        viewMenuDropdownBtn.setOpaque(false);            

        viewMenuDropdownBtn.setForeground(Color.BLACK);
        viewMenuDropdownBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        viewMenuDropdownBtn.addActionListener(e ->
            viewMenu.show(viewMenuDropdownBtn, 0, viewMenuDropdownBtn.getHeight())
        );

        SwingUtilities.invokeLater(() -> {
        Rectangle viewTabBounds = tabsUI.getBoundsAt(1);

        int arrowSize = 22;

        viewMenuDropdownBtn.setBounds(
            viewTabBounds.x + viewTabBounds.width - arrowSize - 4,
            viewTabBounds.y + (viewTabBounds.height - arrowSize) / 2,
            arrowSize,
            arrowSize
        );

        layeredPane.add(viewMenuDropdownBtn, JLayeredPane.PALETTE_LAYER);
    });

        //help menu
        JPopupMenu helpMenu = new JPopupMenu();

        JMenuItem sensorSetupGuide = new JMenuItem("Sensor Setup Guide");
        sensorSetupGuide.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Sensor Setup Guide.",
                    "Sensor Setup Guide",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadSensorSetupGuide();
            this.dispose();
        });

        JMenuItem userDocumentation = new JMenuItem("User Documentation");
        userDocumentation.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to User Documentation.",
                    "User Documentation",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadUserDocumentation();
            this.dispose();
        });

        JMenuItem aboutAOMA = new JMenuItem("About AOMA-Heritage Monitor");
        aboutAOMA.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to About AOMA-Heritage Monitor.",
                    "About AOMA-Heritage Monitor",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadAboutAOMA();
            this.dispose();
        });

        JMenuItem contactSupport = new JMenuItem("Contact Support");
        contactSupport.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Contact Support.",
                    "Contact Support",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadContactSupport();
            this.dispose();
        });

        helpMenu.add(sensorSetupGuide);
        helpMenu.add(userDocumentation);
        helpMenu.addSeparator();
        helpMenu.add(aboutAOMA);
        helpMenu.add(contactSupport);

        JButton helpMenuDropdownBtn = new JButton("▼");
        helpMenuDropdownBtn.setFont(new Font("Arial", Font.BOLD, 14)); 
        helpMenuDropdownBtn.setMargin(new Insets(0, 0, 0, 0));
        helpMenuDropdownBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        helpMenuDropdownBtn.setFocusPainted(false);
        helpMenuDropdownBtn.setContentAreaFilled(false);   
        helpMenuDropdownBtn.setBorderPainted(false);       
        helpMenuDropdownBtn.setOpaque(false);              
        helpMenuDropdownBtn.setForeground(Color.BLACK);
        helpMenuDropdownBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        helpMenuDropdownBtn.addActionListener(e ->
            helpMenu.show(helpMenuDropdownBtn, 0, helpMenuDropdownBtn.getHeight())
        ); 

        SwingUtilities.invokeLater(() -> {
        Rectangle helpTabBounds = tabsUI.getBoundsAt(2);    
        int arrowSize = 22;
        helpMenuDropdownBtn.setBounds(
            helpTabBounds.x + helpTabBounds.width - arrowSize - 4,
            helpTabBounds.y + (helpTabBounds.height - arrowSize) / 2,
            arrowSize,
            arrowSize
        );
        layeredPane.add(helpMenuDropdownBtn, JLayeredPane.PALETTE_LAYER);
    }); 

        //para hindi ma-select yung view/help kapag clinick yung dropdown or text 
        tabsUI.addChangeListener(e -> {
        int selectedIndex = tabsUI.getSelectedIndex();

        Rectangle bounds = tabsUI.getBoundsAt(selectedIndex);

        if (selectedIndex == 0) { // Projects clicked
            projectsMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }

        if (selectedIndex == 2) { // Help clicked
            helpMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }

        // Always go back to View tab
        SwingUtilities.invokeLater(() -> tabsUI.setSelectedIndex(1));
    });
        JLabel LGUHeadLabel = new JLabel("LGU HEAD ACCOUNT");
        LGUHeadLabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGUHeadLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGUHeadLabel.setBounds(1080, 5, 280, 38);

        headPanel.add(LGUHeadLabel);

        JPanel centerPanelDescription = new JPanel(new BorderLayout());
        centerPanelDescription.setBounds(10, 20, 1380, 40);
        Border firstBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanelDescription.setBorder(firstBorder);

        JLabel centerTitleLabel = new JLabel(
                "Automated - Operational Modal Analysis to Monitor the Safety and Serviceability of Heritage Buildings",JLabel.CENTER
        );
        centerTitleLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
        centerTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ImageIcon userIcon = new ImageIcon("usericon.png");
        Image userImgScaled = userIcon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
        JLabel userIconLabel = new JLabel(new ImageIcon(userImgScaled));
        userIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);

        headPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        headPanel.add(centerPanel);

        JLabel headerLabel = new JLabel("View Report", JLabel.LEFT); 
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder( BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)), 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        centerPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel monitoringPanel = new JPanel(null);
        monitoringPanel.setBorder(
        BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.BLACK, 2), // thickness here
        "Monitoring Period"
        )
    );

        JPanel monitoringWrapper = new JPanel(new BorderLayout());
        monitoringWrapper.add(monitoringPanel, BorderLayout.NORTH);
        contentPanel.add(monitoringWrapper);
        monitoringPanel.setPreferredSize(new Dimension(0, 100)); // adjustS height

        // FROM LABEL
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setBounds(40, 40, 50, 25);
        monitoringPanel.add(fromLabel);

        // FROM FIELD
        JTextField fromField = new JTextField("01/24/2026");
        fromField.setBounds(90, 40, 120, 25);
        monitoringPanel.add(fromField);

        // ARROW
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 20));
        arrowLabel.setBounds(220, 40, 30, 25);
        monitoringPanel.add(arrowLabel);

        // TO LABEL
        JLabel toLabel = new JLabel("To:");
        toLabel.setBounds(250, 40, 30, 25);
        monitoringPanel.add(toLabel);

        // TO FIELD
        JTextField toField = new JTextField("02/14/2026");
        toField.setBounds(280, 40, 120, 25);
        monitoringPanel.add(toField);

        // BUTTON
        JButton viewReportBtn = new JButton("View Report");
        viewReportBtn.setBounds(420, 40, 120, 25);
        monitoringPanel.add(viewReportBtn);

        //second left panel
        JPanel vibrationInfoPanel = new JPanel(null);
        vibrationInfoPanel.setBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2)
        );

        vibrationInfoPanel.setPreferredSize(new Dimension(0, 800));

        // TITLE
        JLabel vibrationTitle = new JLabel("Vibration Data", SwingConstants.CENTER);
        vibrationTitle.setFont(new Font("Arial", Font.BOLD, 22));
        vibrationTitle.setBounds(5, 5, 600, 20);
        vibrationInfoPanel.add(vibrationTitle);

        //pre database to ah nakaharcode pa eh
        // DATASET ID
        JLabel datasetID = new JLabel("DATASET ID: #20260124-OMA-005");
        datasetID.setFont(new Font("Arial", Font.PLAIN, 10));
        datasetID.setHorizontalAlignment(SwingConstants.RIGHT);
        datasetID.setBounds(370, 5, 280, 12);
        vibrationInfoPanel.add(datasetID);

        JLabel datasetLabel2 = new JLabel("#20260214-OMA-005");
        datasetLabel2.setFont(new Font("Arial", Font.PLAIN, 10));
        datasetLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        datasetLabel2.setBounds(370, 17, 280, 12);
        vibrationInfoPanel.add(datasetLabel2);

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setOpaque(false);
        legendPanel.setBounds(5, 30, 600, 45); 

        JLabel legendTitle = new JLabel("SPECTROGRAM LEGEND");
        legendTitle.setFont(new Font("Arial", Font.BOLD, 10));
        legendTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        legendPanel.add(legendTitle);
        legendPanel.add(Box.createVerticalStrut(2));

        JPanel legendColors = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        legendColors.setOpaque(false);
        legendColors.add(createLegendLabel("(hz) Normal", new Color(0,170,0)));
        legendColors.add(createLegendLabel("(hz) Warning", new Color(255,165,0)));
        legendColors.add(createLegendLabel("(hz) Critical", new Color(200,0,0)));
        legendPanel.add(legendColors);

        vibrationInfoPanel.add(legendPanel);

        // graph - python integration here
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));
        graphPanel.setOpaque(false);
        graphPanel.setBounds(5, 80, 600, 700);
        vibrationInfoPanel.add(graphPanel);

        //Natural Frequencies
        JLabel naturalFreqLabel = new JLabel("Natural Frequencies (Hz)");
        naturalFreqLabel.setFont(new Font("Arial", Font.BOLD, 12));
        naturalFreqLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(naturalFreqLabel);

        graphPanel.add(Box.createVerticalStrut(5));

        JPanel naturalFrequencyPanel = new JPanel();
        naturalFrequencyPanel.setBackground(Color.WHITE);
        naturalFrequencyPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        naturalFrequencyPanel.setPreferredSize(new Dimension(560, 150));
        naturalFrequencyPanel.setMaximumSize(new Dimension(560, 150));
        naturalFrequencyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(naturalFrequencyPanel);

        graphPanel.add(Box.createVerticalStrut(10));

        //Damping Ratio
        JLabel dampingRatioLabel = new JLabel("Damping Ratio (%)");
        dampingRatioLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dampingRatioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(dampingRatioLabel);

        graphPanel.add(Box.createVerticalStrut(5));

        JPanel dampingRatioPanel = new JPanel();
        dampingRatioPanel.setBackground(Color.WHITE);
        dampingRatioPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dampingRatioPanel.setPreferredSize(new Dimension(560, 150));
        dampingRatioPanel.setMaximumSize(new Dimension(560, 150));
        dampingRatioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(dampingRatioPanel);

        graphPanel.add(Box.createVerticalStrut(10));

        //Mode Shape
        JLabel modeShapeLabel = new JLabel("Mode Shape");
        modeShapeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        modeShapeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(modeShapeLabel);

        graphPanel.add(Box.createVerticalStrut(5));

        JPanel modeShapePanel = new JPanel();
        modeShapePanel.setBackground(Color.WHITE);
        modeShapePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        modeShapePanel.setPreferredSize(new Dimension(560, 150));
        modeShapePanel.setMaximumSize(new Dimension(560, 150));
        modeShapePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(modeShapePanel);

        graphPanel.add(Box.createVerticalStrut(30));

        //STATUS SECTION 
        JPanel statusContainer = new JPanel();
        statusContainer.setLayout(new BoxLayout(statusContainer, BoxLayout.Y_AXIS));
        statusContainer.setOpaque(false);
        statusContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Risk Level Row
        JPanel riskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        riskPanel.setOpaque(false);
        riskPanel.setMaximumSize(new Dimension(560, 30));

        JLabel riskDot = new JLabel("●");
        riskDot.setForeground(new Color(0,170,0));
        riskDot.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel riskText = new JLabel("Risk Level:");
        riskText.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel riskStatus = new JLabel(" Normal ");
        riskStatus.setOpaque(true);
        riskStatus.setBackground(new Color(0,170,0));
        riskStatus.setForeground(Color.WHITE);
        riskStatus.setFont(new Font("Arial", Font.BOLD, 12));

        riskPanel.add(riskDot);
        riskPanel.add(riskText);
        riskPanel.add(riskStatus);

        statusContainer.add(riskPanel);
        statusContainer.add(Box.createVerticalStrut(10));

        // STATUS
        JLabel statusLabel = new JLabel("STATUS: SAFE / SERVICEABLE");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusContainer.add(statusLabel);

        statusContainer.add(Box.createVerticalStrut(5));

        // ACTION
        JLabel actionLabel = new JLabel("ACTION: NO IMMEDIATE INTERVENTION REQUIRED");
        actionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        actionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusContainer.add(actionLabel);

        statusContainer.add(Box.createVerticalStrut(10));

        // DESCRIPTION
        JTextArea descriptionArea = new JTextArea(
            "(The Automated OMA System has completed a routine structural health scan. " +
            "The building's vibrational response is stable.)"
        );
        descriptionArea.setFont(new Font("Arial", Font.ITALIC, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(null);
        descriptionArea.setMaximumSize(new Dimension(520, 100));
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusContainer.add(descriptionArea);

        graphPanel.add(statusContainer);
        
        JScrollPane vibrationScroll = new JScrollPane(vibrationInfoPanel);
        // ALWAYS show vertical scrollbar
        vibrationScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        vibrationScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        vibrationScroll.getVerticalScrollBar().setUnitIncrement(10);
        vibrationScroll.setBorder(null);

        monitoringWrapper.add(vibrationScroll, BorderLayout.CENTER);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(
        BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.BLACK, 2),
        "Report History & Past Reports"
        )
    );
        contentPanel.add(historyPanel);

        // SEARCH BAR
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));
        searchField.setBorder(BorderFactory.createTitledBorder("Search"));

        historyPanel.add(searchField, BorderLayout.NORTH);

        // TABLE DATA
        String[] columns = {"Date", "Dataset ID", "Result"};

        Object[][] data = {
        {"01/24/2026", "#20260124-OMA-005", "SAFE / SERVICEABLE"},
        {"01/31/2026", "#20260121-OMA-005", "NEEDS OBSERVATION"},
        {"02/07/2026", "#20260207-OMA-005", "SAFE / SERVICEABLE"},
        {"02/14/2026", "#20260214-OMA-005", "CRITICAL"}
        };

        JTable table = new JTable(data, columns) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        table.setRowHeight(35);

        table.getColumn("Dataset ID").setCellRenderer(new DatasetCellRenderer());

        // Center ALL cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
        String columnName = table.getColumnName(i);

        if (columnName.equals("Result")) {
            table.getColumnModel().getColumn(i).setCellRenderer(new ResultCellRenderer());
        } 
        else if (!columnName.equals("Dataset ID")) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem viewDetails = new JMenuItem("View Details");
        JMenuItem exportReport = new JMenuItem("Export Report");

        menu.add(viewDetails);
        menu.add(exportReport);

        // Actions
        viewDetails.addActionListener(e -> {
        JOptionPane.showMessageDialog(
            table,
            "Opening View Details...",
            "View Details",
            JOptionPane.INFORMATION_MESSAGE
        );

        new HeadViewDetails(); 
        HeadViewReport.this.dispose();
    });

        exportReport.addActionListener(e -> {
        JOptionPane.showMessageDialog(
            table,
            "Opening Export Report...",
            "Export Report",
            JOptionPane.INFORMATION_MESSAGE
        );

        new HeadExportSensorData(); 
        HeadViewReport.this.dispose();
    });

        // Detect click inside Dataset column
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == table.getColumnModel().getColumnIndex("Dataset ID")) {
                    Rectangle rect = table.getCellRect(row, col, true);

                    // detect if click is on RIGHT SIDE (button area)
                    int buttonWidth = 25;
                    if (e.getX() > rect.x + rect.width - buttonWidth) {
                        menu.show(table, e.getX(), e.getY());
                    }
                }
            }
        });

        // Center header text
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        table.setPreferredScrollableViewportSize(new Dimension(600, 150));
        tableWrapper.add(table.getTableHeader(), BorderLayout.NORTH);
        tableWrapper.add(table, BorderLayout.CENTER);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        historyPanel.add(tableWrapper, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setPreferredSize(new java.awt.Dimension(1400, 45));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(120, 120, 120)) );

        JLabel footerLabel = new JLabel("Status: ESP32 Hub Not Connected");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        footerLabel.setForeground(Color.RED);
        footerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        footerPanel.add(footerLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
        class DatasetCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
            private JLabel label;
            private JButton button;

        public DatasetCellRenderer() {
        setLayout(new BorderLayout());
        setOpaque(true);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER); 

        button = new JButton("▼");
        button.setPreferredSize(new Dimension(30, 20)); 
        button.setFocusPainted(false);

        add(label, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);

        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            label.setText(value.toString());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }

            return this;
        }
    }

    class ResultCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel cell = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String result = value.toString();

            // center text
            cell.setHorizontalAlignment(SwingConstants.CENTER);

            // reset background (important!)
            if (isSelected) {
                cell.setBackground(table.getSelectionBackground());
            } else {
                cell.setBackground(Color.WHITE);
            }

            if (result.equals("SAFE / SERVICEABLE")) {
                cell.setForeground(new Color(0, 170, 0));
            } else if (result.equals("NEEDS OBSERVATION")) {
                cell.setForeground(new Color(255, 165, 0));
            } else if (result.equals("CRITICAL")) {
                cell.setForeground(new Color(200, 0, 0));
            } else {
                cell.setForeground(Color.BLACK);
            }

            return cell;
        }
    }

    class DatasetCellEditor extends DefaultCellEditor {
        private JPanel panel;
        private JLabel label;
        private JButton button;
        private JPopupMenu menu;

        public DatasetCellEditor(JCheckBox checkBox) {
            super(checkBox);

            panel = new JPanel(new BorderLayout());
            label = new JLabel();
            button = new JButton("▼");

            button.setMargin(new Insets(0, 5, 0, 5));
            button.setFocusPainted(false);

            panel.add(label, BorderLayout.CENTER);
            panel.add(button, BorderLayout.EAST);

            // Dropdown menu
            menu = new JPopupMenu();

            JMenuItem viewDetails = new JMenuItem("View Details");
            JMenuItem exportReport = new JMenuItem("Export Report");

            menu.add(viewDetails);
            menu.add(exportReport);

            // Actions
            viewDetails.addActionListener(e -> {
                JOptionPane.showMessageDialog(panel, "Viewing dataset details...");
            });

            exportReport.addActionListener(e -> {
                JOptionPane.showMessageDialog(panel, "Exporting report...");
            });

            button.addActionListener(e -> {
                menu.show(button, 0, button.getHeight());
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {

            label.setText(value.toString());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return label.getText();
        }
    }

    private JLabel createLegendLabel(String text, Color color) {

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));

        return label;
    }

    public static void main(String[] args) {
        new HeadViewReport();
    }
}
