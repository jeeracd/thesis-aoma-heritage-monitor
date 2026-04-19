import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
public class HeadOMAAnalysisResult extends JFrame {

    public HeadOMAAnalysisResult() {
        setTitle("AOMA-Heritage Monitor - OMA Analysis Result");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);

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
            new HeadViewReportWindow();
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

        layeredPane.add(LGUHeadLabel, JLayeredPane.PALETTE_LAYER);

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

        // Head POPUP MENU
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            dispose(); 
            new HeadDashboardUserSettings(); // opens settings window
        });

        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); 
                new UsersLoginOptions(); // opens login page
            }
        });

        userMenu.add(userSettings);
        userMenu.addSeparator();
        userMenu.add(logout);
        userIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Show popup when clicked
        userIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                userMenu.show(userIconLabel, 0, userIconLabel.getHeight());
            }
        });

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);
        headPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        headPanel.add(centerPanel);

        // MAIN CONTENT WRAPPER
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerPanel.add(contentWrapper, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(4);
        splitPane.setEnabled(false);
        contentWrapper.add(splitPane, BorderLayout.CENTER);

        //LEFT PANEL 

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel buildingTitle = new JLabel("Building Profile Information");
        buildingTitle.setFont(new Font("Arial", Font.BOLD, 14));
        buildingTitle.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        buildingTitle.setOpaque(true);
        buildingTitle.setBackground(new Color(230,230,230));

        // smol button for editing structural details
        JButton editStructureBtn = new JButton("...");
        editStructureBtn.setFocusPainted(true);
        editStructureBtn.setMargin(new Insets(2,8,2,8));
        editStructureBtn.setFont(new Font("Arial", Font.BOLD, 15));

        editStructureBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Edit Structural Details page.",
                    "Edit Structural Details",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadStructuralDetails();
            this.dispose();
        });
        
        JPanel buildingHeaderPanel = new JPanel(new BorderLayout());
        buildingHeaderPanel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY));
        buildingHeaderPanel.setBackground(new Color(230,230,230));
        leftPanel.add(buildingHeaderPanel, BorderLayout.NORTH);

        buildingTitle.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));

        buildingHeaderPanel.add(buildingTitle, BorderLayout.WEST);
        buildingHeaderPanel.add(editStructureBtn, BorderLayout.EAST);

        //DATABASE to ah
        JPanel buildingInfoPanel = new JPanel();
        buildingInfoPanel.setLayout(new GridBagLayout());
        buildingInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addInfoField(buildingInfoPanel, "Project Name:", "test1");
        addInfoField(buildingInfoPanel, "Building Name:", "test1");
        addInfoField(buildingInfoPanel, "Year Constructed:", "test1");
        addInfoField(buildingInfoPanel, "Material Used Type:", "test1");
        addInfoField(buildingInfoPanel, "Conservation Status:", "test1");
        addInfoField(buildingInfoPanel, "Function:", "test1");
        addInfoField(buildingInfoPanel, "Address:", "test1");

        addInfoArea(buildingInfoPanel, "Description:","test1");

        addBottomSpacer(buildingInfoPanel);
        
        JScrollPane leftScroll = new JScrollPane(buildingInfoPanel);
        leftScroll.setBorder(null);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        //RIGHT PANEL 
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel headerTitle = new JLabel("OMA Analysis Result");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180,180,180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        headerTitle.setOpaque(true);
        headerTitle.setBackground(new Color(230,230,230));
        rightPanel.add(headerTitle, BorderLayout.NORTH);
        splitPane.setRightComponent(rightPanel);

        JPanel vibrationPanel = new JPanel();
        vibrationPanel.setLayout(new BoxLayout(vibrationPanel, BoxLayout.Y_AXIS));
        vibrationPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        vibrationPanel.setBackground(Color.WHITE);

        //for scrolling the vibration data content
        JScrollPane scrollPane = new JScrollPane(vibrationPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel vibrationTitle = new JLabel("Vibration Data", SwingConstants.CENTER);
        vibrationTitle.setFont(new Font("Arial", Font.BOLD, 22));
        vibrationTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //datbase to ah
        JLabel datasetID = new JLabel("DATASET ID: #20260224-OMA-005");
        datasetID.setFont(new Font("Arial", Font.PLAIN, 12));
        datasetID.setForeground(Color.DARK_GRAY);
        datasetID.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel leftSpacer = new JLabel();
        leftSpacer.setPreferredSize(new Dimension(200, 1));

        titlePanel.add(leftSpacer, BorderLayout.WEST);
        titlePanel.add(vibrationTitle, BorderLayout.CENTER);
        titlePanel.add(datasetID, BorderLayout.EAST);

        vibrationPanel.add(titlePanel);

        vibrationPanel.add(Box.createVerticalStrut(5));

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setOpaque(false);
        legendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel legendTitle = new JLabel("SPECTROGRAM LEGEND");
        legendTitle.setFont(new Font("Arial", Font.BOLD, 12));
        legendTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        legendPanel.add(legendTitle);
        legendPanel.add(Box.createVerticalStrut(2));

        JPanel legendColors = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        legendColors.setOpaque(false);

        legendColors.add(createLegendLabel("(hz) Normal", new Color(0,170,0)));
        legendColors.add(createLegendLabel("(hz) Warning", new Color(255,165,0)));
        legendColors.add(createLegendLabel("(hz) Critical", new Color(200,0,0)));

        legendPanel.add(legendColors);

        vibrationPanel.add(legendPanel);
        vibrationPanel.add(Box.createVerticalStrut(10));

        // GRAPH PANEL (placeholder only)
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));
        graphPanel.setOpaque(false);
        graphPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vibrationPanel.add(graphPanel);

        JLabel naturalFreqLabel = new JLabel("Natural Frequencies (Hz)");
        naturalFreqLabel.setFont(new Font("Arial", Font.BOLD, 16));
        naturalFreqLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(naturalFreqLabel);
        graphPanel.add(Box.createVerticalStrut(5));

        JPanel naturalFrequencyPanel = new JPanel();
        naturalFrequencyPanel.setLayout(new BorderLayout());
        naturalFrequencyPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        naturalFrequencyPanel.setPreferredSize(new Dimension(850,150));
        naturalFrequencyPanel.setMaximumSize(new Dimension(850,150));
        naturalFrequencyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        naturalFrequencyPanel.setBackground(Color.WHITE);
        graphPanel.add(naturalFrequencyPanel);
        graphPanel.add(Box.createVerticalStrut(15));

        JLabel dampingRatioLabel = new JLabel("Damping Ratios (%)");
        dampingRatioLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dampingRatioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(dampingRatioLabel);
        graphPanel.add(Box.createVerticalStrut(5));

        JPanel dampingRatioPanel = new JPanel();
        dampingRatioPanel.setLayout(new BorderLayout());
        dampingRatioPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dampingRatioPanel.setPreferredSize(new Dimension(850,150));
        dampingRatioPanel.setMaximumSize(new Dimension(850,150));
        dampingRatioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dampingRatioPanel.setBackground(Color.WHITE);
        graphPanel.add(dampingRatioPanel);
        graphPanel.add(Box.createVerticalStrut(15));

        JLabel modeShapeLabel = new JLabel("Mode Shapes");
        modeShapeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        modeShapeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphPanel.add(modeShapeLabel);
        graphPanel.add(Box.createVerticalStrut(5));

        JPanel modeShapePanel = new JPanel();
        modeShapePanel.setLayout(new BorderLayout());
        modeShapePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        modeShapePanel.setPreferredSize(new Dimension(850,150));
        modeShapePanel.setMaximumSize(new Dimension(850,150));
        modeShapePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeShapePanel.setBackground(Color.WHITE);
        graphPanel.add(modeShapePanel);
        //adjust the height of buttom
        //graphPanel.add(Box.createVerticalStrut(400));
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(245,245,245));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        resultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.setMaximumSize(new Dimension(850, 200));

        // --- Risk Level Row ---
        JPanel riskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        riskPanel.setOpaque(false);

        JLabel riskDot = new JLabel("●");
        riskDot.setForeground(new Color(0,170,0));
        riskDot.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel riskText = new JLabel("Risk Level:");
        riskText.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel riskStatus = new JLabel(" Normal ");
        riskStatus.setOpaque(true);
        riskStatus.setBackground(new Color(0,170,0));
        riskStatus.setForeground(Color.WHITE);
        riskStatus.setFont(new Font("Arial", Font.BOLD, 12));

        riskPanel.add(riskDot);
        riskPanel.add(riskText);
        riskPanel.add(riskStatus);

        //Status
        JLabel statusLabel = new JLabel("STATUS: SAFE / SERVICEABLE");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        //Action
        JLabel actionLabel = new JLabel("ACTION: NO IMMEDIATE INTERVENTION REQUIRED");
        actionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        //Description
        JTextArea descriptionArea = new JTextArea(
            "(The Automated OMA System has completed a routine structural health scan. " +
            "The building's vibrational response is stable.)"
        );
        descriptionArea.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(null);

        // Add components
        resultPanel.add(riskPanel);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(statusLabel);
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(actionLabel);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(descriptionArea);

        // Add to graphPanel
        graphPanel.add(Box.createVerticalStrut(15));
        graphPanel.add(resultPanel);

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
        add(layeredPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

        private int rowIndex = 0; 

        private void addInfoField(JPanel panel, String label, String value) {

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 4, 6, 4); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = rowIndex;

            // LABEL
            gbc.gridx = 0;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(lbl, gbc);

            // VALUE
            gbc.gridx = 1;
            gbc.weightx = 1;
            JLabel val = new JLabel(value);
            val.setFont(new Font("Arial", Font.PLAIN, 12));
            panel.add(val, gbc);

            rowIndex++;
        }

        private void addInfoArea(JPanel panel, String label, String value) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4); // slightly more for description
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = rowIndex;

        // LABEL
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl, gbc);

        // TEXT AREA
        gbc.gridx = 1;
        gbc.weightx = 1;

        JTextArea area = new JTextArea(value);
        area.setFont(new Font("Arial", Font.PLAIN, 12));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(panel.getBackground());
        area.setBorder(null);

        panel.add(area, gbc);

        rowIndex++;
    }

        private void addBottomSpacer(JPanel panel) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.gridwidth = 2;           
        gbc.weighty = 1;        
        gbc.fill = GridBagConstraints.VERTICAL;

        panel.add(Box.createVerticalGlue(), gbc);
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
        new HeadOMAAnalysisResult();
    }
}