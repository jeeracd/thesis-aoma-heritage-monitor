import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class EngineerOMAAnalysisResultWindow extends JFrame {

    public static EngineerOMAAnalysisResultWindow instance;
    public static JPanel projectsContainer;
    public static JPanel tableHeaderPanel;
    public static JPanel centerContentWrapper;
    public static int projectCount = 1;

    public static JLabel totalBuildingsValue;
    public static JLabel criticalValue;
    public static int totalBuildingsCount = 0;
    public static int criticalBuildingsCount = 0;

    public EngineerOMAAnalysisResultWindow() {
        instance = this;
        setTitle("AOMA-Heritage Monitor - OMA Analysis Result");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel engineerPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.ENGINEER);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", engineerPanel);
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
            new EngineerEditStructuralDetails();
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
            new EngineerImportSensorData();
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
            new EngineerExportSensorData();
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
            new EngineerBldgStatusOverview();
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
            new EngineerSetupConnectionWindow();
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
            new EngineerConfigureSensorWindow();
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
            new EngineerESP32StatusWindow();
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
            new EngineerVibrationDataWindow();
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
            new EngineerOMAAnalysisResultWindow();
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
            new EngineerViewReportWindow();
            this.dispose();
        });

        JMenuItem systemLogs = new JMenuItem("System Logs");
        systemLogs.setEnabled(false); // disabled for now


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
            new EngineerSensorSetupGuide();
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
            new EngineerUserDocumentation();
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
            new EngineerAboutAOMA();
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
            new EngineerContactSupport();
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

        JLabel LGULabel = new JLabel("STRUCTURAL ENGINEER ACCOUNT");
        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGULabel.setBounds(1080, 5, 280, 38);

        engineerPanel.add(LGULabel);

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

        // ENGINEER POPUP MENU
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            dispose(); 
            new EngineerDashboardUserSettings(); // opens settings window
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
        engineerPanel.add(centerPanelDescription);

        // CENTER PANEL 
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        engineerPanel.add(centerPanel);

        // HEADER 
        JLabel headerLabel = new JLabel("OMA Analysis Result");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        centerPanel.add(headerLabel, BorderLayout.NORTH);

        centerContentWrapper = new JPanel();
        centerContentWrapper.setLayout(new BoxLayout(centerContentWrapper, BoxLayout.Y_AXIS));
        centerContentWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(centerContentWrapper, BorderLayout.CENTER);

        Dimension overviewSize = new Dimension(1600, 100); 

        JPanel centerPanelStatusOverview = new JPanel(new BorderLayout());
        centerPanelStatusOverview.setPreferredSize(overviewSize);
        centerPanelStatusOverview.setMinimumSize(overviewSize);
        centerPanelStatusOverview.setMaximumSize(overviewSize);
        centerPanelStatusOverview.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        centerPanelStatusOverview.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusOverviewHeaderLabel = new JLabel(
                "OMA Analysis Result",
                SwingConstants.CENTER
        );
        statusOverviewHeaderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusOverviewHeaderLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanelStatusOverview.add(statusOverviewHeaderLabel, BorderLayout.NORTH);

        JLabel statusOverviewSubheaderLabel = new JLabel(
                "No description yet..",
                SwingConstants.CENTER
        );
        statusOverviewSubheaderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusOverviewSubheaderLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centerPanelStatusOverview.add(statusOverviewSubheaderLabel, BorderLayout.CENTER);

        centerContentWrapper.add(centerPanelStatusOverview);
        centerContentWrapper.add(Box.createVerticalStrut(10)); 

        // HORIZONTAL FIRST PANEL 
        JPanel horizontalFirstPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        horizontalFirstPanel.setPreferredSize(new Dimension(1600, 50));
        horizontalFirstPanel.setMaximumSize(new Dimension(1600, 50));
        horizontalFirstPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizontalFirstPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // p1
        JPanel totalBuildingsPanel = new JPanel(new BorderLayout());
        totalBuildingsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel totalBuildingsLabel = new JLabel("Total Buildings", SwingConstants.CENTER);
        totalBuildingsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalBuildingsValue = new JLabel(String.valueOf(totalBuildingsCount), SwingConstants.CENTER);
        totalBuildingsValue.setFont(new Font("Arial", Font.BOLD, 20));

        totalBuildingsPanel.add(totalBuildingsLabel, BorderLayout.NORTH);
        totalBuildingsPanel.add(totalBuildingsValue, BorderLayout.CENTER);

        // p2
        JPanel criticalPanel = new JPanel(new BorderLayout());
        criticalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel criticalLabel = new JLabel("No Data", SwingConstants.CENTER);
        criticalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        criticalLabel.setForeground(Color.RED);

        criticalValue = new JLabel(String.valueOf(criticalBuildingsCount), SwingConstants.CENTER);
        criticalValue.setFont(new Font("Arial", Font.BOLD, 20));
        criticalValue.setForeground(Color.RED);

        criticalPanel.add(criticalLabel, BorderLayout.NORTH);
        criticalPanel.add(criticalValue, BorderLayout.CENTER);

        // p3
        JPanel safePanel = new JPanel(new BorderLayout());
        safePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel safeLabel = new JLabel("With Data", SwingConstants.CENTER);
        safeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        safeLabel.setForeground(new Color(0, 128, 0));

        JLabel safeValue = new JLabel("0", SwingConstants.CENTER);
        safeValue.setFont(new Font("Arial", Font.BOLD, 20));
        safeValue.setForeground(new Color(0, 128, 0));

        safePanel.add(safeLabel, BorderLayout.NORTH);
        safePanel.add(safeValue, BorderLayout.CENTER);

        // p4
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JTextField searchField = new JTextField("🔍 Search");
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        searchPanel.add(searchField, BorderLayout.CENTER);

        horizontalFirstPanel.add(totalBuildingsPanel);
        horizontalFirstPanel.add(criticalPanel);
        horizontalFirstPanel.add(safePanel);
        horizontalFirstPanel.add(searchPanel);

        centerContentWrapper.add(horizontalFirstPanel);
        centerContentWrapper.add(Box.createVerticalStrut(10)); 

        // HORIZONTAL SECOND PANEL 
        JPanel horizontalSecondPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        horizontalSecondPanel.setPreferredSize(new Dimension(1600, 50));
        horizontalSecondPanel.setMaximumSize(new Dimension(1600, 50));
        horizontalSecondPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizontalSecondPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        Font headerFont = new Font("Arial", Font.BOLD, 14);

        // p1
        JPanel bldgPanel = new JPanel(new BorderLayout());
        bldgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel bldgLabel = new JLabel("Heritage Building Name", SwingConstants.CENTER);
        bldgLabel.setFont(headerFont);
        bldgPanel.add(bldgLabel, BorderLayout.CENTER);

        // p2
        JPanel locationPanel = new JPanel(new BorderLayout());
        locationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel locationLabel = new JLabel("Location", SwingConstants.CENTER);
        locationLabel.setFont(headerFont);
        locationPanel.add(locationLabel, BorderLayout.CENTER);

        // p3
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel functionLabel = new JLabel("Function", SwingConstants.CENTER);
        functionLabel.setFont(headerFont);
        functionPanel.add(functionLabel, BorderLayout.CENTER);

        // p4 - note to me: connected or disconnected status
        JPanel StatusPanel = new JPanel(new BorderLayout());
        StatusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel StatusLabel = new JLabel("Status", SwingConstants.CENTER);
        StatusLabel.setFont(headerFont);
        StatusPanel.add(StatusLabel, BorderLayout.CENTER);

        // p5
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel actionsLabel = new JLabel("Actions", SwingConstants.CENTER);
        actionsLabel.setFont(headerFont);
        actionsPanel.add(actionsLabel, BorderLayout.CENTER);

        horizontalSecondPanel.add(bldgPanel);
        horizontalSecondPanel.add(locationPanel);
        horizontalSecondPanel.add(functionPanel);
        horizontalSecondPanel.add(StatusPanel);
        horizontalSecondPanel.add(actionsPanel);

        tableHeaderPanel = horizontalSecondPanel;
        centerContentWrapper.add(tableHeaderPanel);
        centerContentWrapper.add(Box.createVerticalStrut(3));

        projectsContainer = new JPanel();
        projectsContainer.setLayout(new BoxLayout(projectsContainer, BoxLayout.Y_AXIS));
        projectsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerContentWrapper.add(projectsContainer);

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
        add(engineerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

        public static void addNewProjectRow(
            String buildingName,
            String location,
            String function,
            //i should change this: hindi health status to, dpat connection status
            String healthStatus) {

        if (projectsContainer == null) {
            System.err.println("Projects container not initialized!");
            return;
        }

        totalBuildingsCount++;
        totalBuildingsValue.setText(String.valueOf(totalBuildingsCount));

        if (healthStatus.equalsIgnoreCase("CRITICAL")) {
            criticalBuildingsCount++;
            criticalValue.setText(String.valueOf(criticalBuildingsCount));
        }

        JPanel rowPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        rowPanel.setPreferredSize(new Dimension(1600, 50));
        rowPanel.setMaximumSize(new Dimension(1600, 50));
        rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel rowBldgPanel = new JPanel(new BorderLayout());
        rowBldgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        rowBldgPanel.add(new JLabel(buildingName, SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel rowLocationPanel = new JPanel(new BorderLayout());
        rowLocationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        rowLocationPanel.add(new JLabel(location, SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel rowFunctionPanel = new JPanel(new BorderLayout());
        rowFunctionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        rowFunctionPanel.add(new JLabel(function, SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel rowHealthPanel = new JPanel(new BorderLayout());
        rowHealthPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel healthLbl = new JLabel(healthStatus, SwingConstants.CENTER);
        healthLbl.setForeground(Color.GRAY);
        rowHealthPanel.add(healthLbl, BorderLayout.CENTER);

        JPanel rowActionsPanel = new JPanel(new BorderLayout());
        rowActionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JButton viewOMAAnalysisBtn = new JButton("View OMA Analysis Result");
        viewOMAAnalysisBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewOMAAnalysisBtn.setFocusPainted(false);

        viewOMAAnalysisBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new EngineerOMAAnalysisResult();
                instance.setVisible(false);
            });
        });

        rowActionsPanel.add(viewOMAAnalysisBtn, BorderLayout.CENTER);

        rowPanel.add(rowBldgPanel);
        rowPanel.add(rowLocationPanel);
        rowPanel.add(rowFunctionPanel);
        rowPanel.add(rowHealthPanel);
        rowPanel.add(rowActionsPanel);

        projectsContainer.add(Box.createVerticalStrut(3));
        projectsContainer.add(rowPanel);

        projectsContainer.revalidate();
        projectsContainer.repaint();
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EngineerOMAAnalysisResultWindow::new);
    }
}
