import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class OfficerBldgStatusOverview extends JFrame {

    public static OfficerBldgStatusOverview instance;
    public static JPanel projectsContainer;
    public static JPanel tableHeaderPanel;
    public static JPanel centerContentWrapper;
    public static int projectCount = 1;

    public static JLabel totalBuildingsValue;
    public static JLabel criticalValue;
    public static int totalBuildingsCount = 0;
    public static int criticalBuildingsCount = 0;

    public OfficerBldgStatusOverview() {
        instance = this;
        setTitle("AOMA-Heritage Monitor - Building Status Overview");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JPanel officerPanel = new JPanel(null);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.LIGHT_GRAY);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JMenu projectsMenu = new JMenu("Projects");
        projectsMenu.setFont(new Font("Arial", Font.BOLD, 15));
        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(new Font("Arial", Font.BOLD, 15));
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Arial", Font.BOLD, 15));

        menuBar.add(projectsMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        JMenuItem newProject = new JMenuItem("New Project");
        JMenuItem openProject = new JMenuItem("Open Project");
        JMenuItem importCsv = new JMenuItem("Import Sensor Data (.csv)");
        JMenuItem exportPdf = new JMenuItem("Export Report (PDF)");
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

        //for reference greyout siya
        newProject.setEnabled(false);
        openProject.setEnabled(true);
        importCsv.setEnabled(false);
        exportPdf.setEnabled(false);
        exit.setEnabled(true);

        projectsMenu.add(newProject);
        projectsMenu.add(openProject);
        projectsMenu.addSeparator();
        projectsMenu.add(importCsv);
        projectsMenu.add(exportPdf);
        projectsMenu.addSeparator();
        projectsMenu.add(exit);

        //VIEW MENU 

        JMenuItem dashboardView = new JMenuItem("Dashboard View");
        dashboardView.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Dashboard View.",
                    "Dashboard View",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new OfficerBldgStatusOverview();
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
            new OfficerSetupConnectionWindow();
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
            new OfficerConfigureSensorWindow();
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
            new OfficerESP32StatusWindow();
            this.dispose();
        });

        JMenuItem vibrationData = new JMenuItem("Vibration Data");
        JMenuItem omaAnalysisResult = new JMenuItem("OMA Analysis Result");
        JMenuItem reportHistory = new JMenuItem("View Report");
        JMenuItem systemLogs = new JMenuItem("System Logs");

        dashboardView.setEnabled(true);
        setupConnection.setEnabled(true);
        configureSensor.setEnabled(true);
        esp32Status.setEnabled(true);
        vibrationData.setEnabled(false);
        omaAnalysisResult.setEnabled(false);
        reportHistory.setEnabled(false);
        systemLogs.setEnabled(false);
        
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

        //help menu 

        JMenuItem sensorSetupGuide = new JMenuItem("Sensor Setup Guide");
        sensorSetupGuide.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Sensor Setup Guide.",
                    "Sensor Setup Guide",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new OfficerSensorSetupGuide();
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
            new OfficerUserDocumentation();
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
            new OfficerAboutAOMA();
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
            new OfficerContactSupport();
            this.dispose();
        });
    

        helpMenu.add(sensorSetupGuide);
        helpMenu.add(userDocumentation);
        helpMenu.addSeparator();
        helpMenu.add(aboutAOMA);
        helpMenu.add(contactSupport);

        JLabel LGULabel = new JLabel("LGU OFFICER ACCOUNT");
        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGULabel.setBounds(1080, 5, 280, 38);

        officerPanel.add(LGULabel);

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

        // Officer POPUP MENU
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            dispose(); 
            new OfficerDashboardUserSettings(); // opens settings window
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
        officerPanel.add(centerPanelDescription);

        // CENTER PANEL 
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        officerPanel.add(centerPanel);

        // HEADER 
        JLabel headerLabel = new JLabel("Dashboard");
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
                "Heritage Building Status Overview",
                SwingConstants.CENTER
        );
        statusOverviewHeaderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusOverviewHeaderLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanelStatusOverview.add(statusOverviewHeaderLabel, BorderLayout.NORTH);

        JLabel statusOverviewSubheaderLabel = new JLabel(
                "Welcome, LGU Head. Below is the real-time safety and serviceability status of all heritage structures under your jurisdiction.",
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

        JLabel criticalLabel = new JLabel("Critical Attention Needed", SwingConstants.CENTER);
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

        JLabel safeLabel = new JLabel("Safe / Serviceable", SwingConstants.CENTER);
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

        // p4
        JPanel healthStatusPanel = new JPanel(new BorderLayout());
        healthStatusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel healthStatusLabel = new JLabel("Health Status", SwingConstants.CENTER);
        healthStatusLabel.setFont(headerFont);
        healthStatusPanel.add(healthStatusLabel, BorderLayout.CENTER);

        // p5
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel actionsLabel = new JLabel("Actions", SwingConstants.CENTER);
        actionsLabel.setFont(headerFont);
        actionsPanel.add(actionsLabel, BorderLayout.CENTER);

        horizontalSecondPanel.add(bldgPanel);
        horizontalSecondPanel.add(locationPanel);
        horizontalSecondPanel.add(functionPanel);
        horizontalSecondPanel.add(healthStatusPanel);
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
        add(officerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

        public static void addNewProjectRow(
            String buildingName,
            String location,
            String function,
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

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewDetailsBtn.setFocusPainted(false);

        viewDetailsBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new OfficerViewDetails();
                instance.setVisible(false);
            });
        });

        rowActionsPanel.add(viewDetailsBtn, BorderLayout.CENTER);

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
        SwingUtilities.invokeLater(OfficerBldgStatusOverview::new);
    }
}


