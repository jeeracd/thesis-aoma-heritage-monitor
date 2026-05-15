import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.Border;

public class EngineerBldgStatusOverview extends JFrame {

    public static EngineerBldgStatusOverview instance;
    public static JPanel projectsContainer;
    public static JScrollPane projectsScrollPane;
    public static JPanel tableHeaderPanel;
    public static JPanel centerContentWrapper;
    public static int projectCount = 1;

    public static JLabel totalBuildingsValue;
    public static JLabel criticalValue;
    public static JLabel safeValue;
    public static int totalBuildingsCount = 0;
    public static int criticalBuildingsCount = 0;
    public static int safeBuildingsCount = 0;

    private SensorDataManager sensorManager;
    private SensorDataManager.ChangeListener sensorListener;
    private Runnable removeRepoListener;
    private Runnable removeDatasetListener;

    public EngineerBldgStatusOverview() {
        instance = this;
        sensorManager = SensorDataManager.getInstance();
        
        setTitle("AOMA-Heritage Monitor - Building Status Overview");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JPanel engineerPanel = new JPanel(null);

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

        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            dispose();
            new EngineerDashboardUserSettings();
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
                new UsersLoginOptions();
            }
        });

        userMenu.add(userSettings);
        userMenu.addSeparator();
        userMenu.add(logout);
        userIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        userIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                userMenu.show(userIconLabel, 0, userIconLabel.getHeight());
            }
        });

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);
        engineerPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        engineerPanel.add(centerPanel);

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

        Dimension overviewSize = new Dimension(1360, 100);

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

        JPanel horizontalFirstPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        horizontalFirstPanel.setPreferredSize(new Dimension(1360, 50));
        horizontalFirstPanel.setMaximumSize(new Dimension(1360, 50));
        horizontalFirstPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizontalFirstPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel totalBuildingsPanel = new JPanel(new BorderLayout());
        totalBuildingsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel totalBuildingsLabel = new JLabel("Total Buildings", SwingConstants.CENTER);
        totalBuildingsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalBuildingsValue = new JLabel(String.valueOf(totalBuildingsCount), SwingConstants.CENTER);
        totalBuildingsValue.setFont(new Font("Arial", Font.BOLD, 20));

        totalBuildingsPanel.add(totalBuildingsLabel, BorderLayout.NORTH);
        totalBuildingsPanel.add(totalBuildingsValue, BorderLayout.CENTER);

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

        JPanel safePanel = new JPanel(new BorderLayout());
        safePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel safeLabel = new JLabel("Safe / Serviceable", SwingConstants.CENTER);
        safeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        safeLabel.setForeground(new Color(0, 128, 0));

        safeValue = new JLabel(String.valueOf(safeBuildingsCount), SwingConstants.CENTER);
        safeValue.setFont(new Font("Arial", Font.BOLD, 20));
        safeValue.setForeground(new Color(0, 128, 0));

        safePanel.add(safeLabel, BorderLayout.NORTH);
        safePanel.add(safeValue, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JTextField searchField = new JTextField("Search");
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        searchPanel.add(searchField, BorderLayout.CENTER);

        horizontalFirstPanel.add(totalBuildingsPanel);
        horizontalFirstPanel.add(criticalPanel);
        horizontalFirstPanel.add(safePanel);
        horizontalFirstPanel.add(searchPanel);

        centerContentWrapper.add(horizontalFirstPanel);
        centerContentWrapper.add(Box.createVerticalStrut(10));

        JPanel horizontalSecondPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        horizontalSecondPanel.setPreferredSize(new Dimension(1360, 50));
        horizontalSecondPanel.setMaximumSize(new Dimension(1360, 50));
        horizontalSecondPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizontalSecondPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        Font headerFont = new Font("Arial", Font.BOLD, 14);

        JPanel bldgPanel = new JPanel(new BorderLayout());
        bldgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel bldgLabel = new JLabel("Heritage Building Name", SwingConstants.CENTER);
        bldgLabel.setFont(headerFont);
        bldgPanel.add(bldgLabel, BorderLayout.CENTER);

        JPanel locationPanel = new JPanel(new BorderLayout());
        locationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel locationLabel = new JLabel("Location", SwingConstants.CENTER);
        locationLabel.setFont(headerFont);
        locationPanel.add(locationLabel, BorderLayout.CENTER);

        JPanel functionPanel = new JPanel(new BorderLayout());
        functionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel functionLabel = new JLabel("Function", SwingConstants.CENTER);
        functionLabel.setFont(headerFont);
        functionPanel.add(functionLabel, BorderLayout.CENTER);

        JPanel healthStatusPanel = new JPanel(new BorderLayout());
        healthStatusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel healthStatusLabel = new JLabel("Health Status", SwingConstants.CENTER);
        healthStatusLabel.setFont(headerFont);
        healthStatusPanel.add(healthStatusLabel, BorderLayout.CENTER);

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
        assert tableHeaderPanel.getComponentCount() == 5;

        projectsContainer = new JPanel();
        projectsContainer.setLayout(new BoxLayout(projectsContainer, BoxLayout.Y_AXIS));
        projectsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        projectsContainer.setOpaque(true);
        projectsContainer.setBackground(Color.WHITE);

        projectsScrollPane = new JScrollPane(projectsContainer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        projectsScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        projectsScrollPane.getViewport().setBorder(null);
        projectsScrollPane.getViewport().setOpaque(true);
        projectsScrollPane.getViewport().setBackground(Color.WHITE);
        projectsScrollPane.setOpaque(true);
        projectsScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension listSize = new Dimension(1360, 320);
        projectsScrollPane.setPreferredSize(listSize);
        projectsScrollPane.setMaximumSize(listSize);
        projectsScrollPane.setMinimumSize(new Dimension(1360, 200));
        JScrollBar vsb = projectsScrollPane.getVerticalScrollBar();
        vsb.setUI(new AppScrollBarUI(10));
        vsb.setUnitIncrement(16);
        centerContentWrapper.add(projectsScrollPane);
        loadPersistedProjectsIntoUI();

        removeRepoListener = ProjectRepository.addChangeListener(() -> SwingUtilities.invokeLater(EngineerBldgStatusOverview::loadPersistedProjectsIntoUI));
        removeDatasetListener = ProjectDatasetIdStore.addActiveDatasetListener((projectId, datasetId) -> SwingUtilities.invokeLater(() -> updateDatasetLabelForProject(projectId)));
        sensorListener = () -> SwingUtilities.invokeLater(EngineerBldgStatusOverview::loadPersistedProjectsIntoUI);
        sensorManager.addChangeListener(sensorListener);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cleanupListeners();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cleanupListeners();
            }
        });

        JPanel createBtnWrapper = new JPanel();
        createBtnWrapper.setLayout(new GridBagLayout());
        createBtnWrapper.setOpaque(false);

        createBtnWrapper.setPreferredSize(new Dimension(220, 90));

        JButton createBtn = new JButton("Create a New Project");
        createBtn.setFont(new Font("Arial", Font.BOLD, 14));
        createBtn.setForeground(new Color(0, 153, 0));
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        createBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "New Project initialization process will start.",
                    "New Project",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerEditStructuralDetails();
            this.dispose();
        });

        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerContentWrapper.add(Box.createVerticalStrut(15));
        centerContentWrapper.add(createBtnWrapper);
        centerContentWrapper.add(Box.createVerticalGlue());

        createBtnWrapper.add(createBtn);

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
            String healthStatus
    ) {
        renderProjectRow(null, buildingName, location, function, healthStatus);
    }

    private static void loadPersistedProjectsIntoUI() {
        if (projectsContainer == null) {
            return;
        }
        projectsContainer.removeAll();

        totalBuildingsCount = 0;
        criticalBuildingsCount = 0;
        safeBuildingsCount = 0;
        if (totalBuildingsValue != null) {
            totalBuildingsValue.setText(String.valueOf(totalBuildingsCount));
        }
        if (criticalValue != null) {
            criticalValue.setText(String.valueOf(criticalBuildingsCount));
        }
        if (safeValue != null) {
            safeValue.setText(String.valueOf(safeBuildingsCount));
        }

        for (Project p : ProjectRepository.getAll()) {
            ProjectDatasetIdStore.ensureActiveDatasetId(p.getId());
            String buildingName = p.getBuildingName().isEmpty()
                    ? "New Heritage Building"
                    : p.getBuildingName();
            String location = p.getAddress().isEmpty()
                    ? "Location Not Set"
                    : p.getAddress();
            String function = p.getFunction().isEmpty()
                    ? "Not Specified"
                    : p.getFunction();
            SensorDataManager mgr = SensorDataManager.getInstance();
            String connectionStatus = mgr.getBuildingConnectionStatus(p.getId());
            String operationalStatus = mgr.getBuildingOperationalStatus(p.getId());
            String healthStatus = operationalStatus + " | " + connectionStatus;
            renderProjectRow(p.getId(), buildingName, location, function, healthStatus);
        }

        safeBuildingsCount = Math.max(0, totalBuildingsCount - criticalBuildingsCount);
        if (safeValue != null) {
            safeValue.setText(String.valueOf(safeBuildingsCount));
        }

        projectsContainer.revalidate();
        projectsContainer.repaint();
    }

    private static void renderProjectRow(
            UUID projectId,
            String buildingName,
            String location,
            String function,
            String healthStatus
    ) {
        if (projectsContainer == null) {
            System.err.println("Projects container not initialized!");
            return;
        }

        totalBuildingsCount++;
        totalBuildingsValue.setText(String.valueOf(totalBuildingsCount));

        if (healthStatus != null && healthStatus.toUpperCase().startsWith("CRITICAL")) {
            criticalBuildingsCount++;
            criticalValue.setText(String.valueOf(criticalBuildingsCount));
        }
        safeBuildingsCount = Math.max(0, totalBuildingsCount - criticalBuildingsCount);
        if (safeValue != null) {
            safeValue.setText(String.valueOf(safeBuildingsCount));
        }

        JPanel rowPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        rowPanel.setPreferredSize(new Dimension(1360, 50));
        rowPanel.setMaximumSize(new Dimension(1360, 50));
        rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel rowBldgPanel = new JPanel(new BorderLayout());
        rowBldgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel buildingLbl = new JLabel(buildingName, SwingConstants.CENTER);
        rowBldgPanel.add(buildingLbl, BorderLayout.CENTER);

        JPanel rowLocationPanel = new JPanel(new BorderLayout());
        rowLocationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel locationLbl = new JLabel(location, SwingConstants.CENTER);
        rowLocationPanel.add(locationLbl, BorderLayout.CENTER);

        JPanel rowFunctionPanel = new JPanel(new BorderLayout());
        rowFunctionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel functionLbl = new JLabel(function, SwingConstants.CENTER);
        rowFunctionPanel.add(functionLbl, BorderLayout.CENTER);

        JPanel rowHealthPanel = new JPanel(new BorderLayout());
        rowHealthPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel healthLbl = new JLabel(healthStatus, SwingConstants.CENTER);
        healthLbl.setForeground(Color.GRAY);
        rowHealthPanel.add(healthLbl, BorderLayout.CENTER);

        if (projectId != null) {
            installFieldEditor(projectId, buildingLbl, 0);
            installFieldEditor(projectId, locationLbl, 1);
            installFieldEditor(projectId, functionLbl, 2);
            installStatusEditor(projectId, healthLbl);
        }

        JPanel rowActionsPanel = new JPanel(new BorderLayout());
        rowActionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewDetailsBtn.setFocusPainted(false);

        JLabel datasetLabel = new JLabel(formatDatasetLabel(projectId), SwingConstants.CENTER);
        datasetLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        datasetLabel.setForeground(Color.DARK_GRAY);
        datasetLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        viewDetailsBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (projectId == null) {
                    new EngineerViewDetails();
                } else {
                    AppSession.setActiveProjectId(projectId);
                    new EngineerViewDetails(projectId);
                }
                instance.setVisible(false);
            });
        });

        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 1, 6, 0));
        actionButtonsPanel.add(viewDetailsBtn);
        rowActionsPanel.add(actionButtonsPanel, BorderLayout.CENTER);
        rowActionsPanel.add(datasetLabel, BorderLayout.SOUTH);

        rowPanel.add(rowBldgPanel);
        rowPanel.add(rowLocationPanel);
        rowPanel.add(rowFunctionPanel);
        rowPanel.add(rowHealthPanel);
        rowPanel.add(rowActionsPanel);

        rowPanel.putClientProperty("projectId", projectId);
        rowPanel.putClientProperty("datasetLabel", datasetLabel);

        projectsContainer.add(Box.createVerticalStrut(3));
        projectsContainer.add(rowPanel);
    }

    private static String formatDatasetLabel(UUID projectId) {
        if (projectId == null) {
            return "Dataset: —";
        }
        String active = ProjectDatasetIdStore.getActiveDatasetId(projectId);
        if (active == null || active.isBlank()) {
            return "Dataset: —";
        }
        return "Dataset: " + active;
    }

    private static void updateDatasetLabelForProject(UUID projectId) {
        if (projectsContainer == null || projectId == null) {
            return;
        }
        for (Component c : projectsContainer.getComponents()) {
            if (!(c instanceof JPanel p)) {
                continue;
            }
            Object id = p.getClientProperty("projectId");
            if (!(id instanceof UUID uid)) {
                continue;
            }
            if (!uid.equals(projectId)) {
                continue;
            }
            Object l = p.getClientProperty("datasetLabel");
            if (l instanceof JLabel label) {
                label.setText(formatDatasetLabel(projectId));
            }
        }
    }

    private static void installFieldEditor(UUID projectId, JLabel label, int fieldIndex) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Optional<Project> maybeProject = ProjectRepository.findById(projectId);
                if (maybeProject.isEmpty()) {
                    return;
                }
                Project p = maybeProject.get();
                String currentValue;
                String title;
                if (fieldIndex == 0) {
                    currentValue = p.getBuildingName();
                    title = "Heritage Building Name";
                } else if (fieldIndex == 1) {
                    currentValue = p.getAddress();
                    title = "Location";
                } else {
                    currentValue = p.getFunction();
                    title = "Function";
                }

                String nextValue = JOptionPane.showInputDialog(instance, "Edit " + title + ":", currentValue);
                if (nextValue == null) {
                    return;
                }
                nextValue = nextValue.trim();
                if (nextValue.isEmpty()) {
                    JOptionPane.showMessageDialog(instance, "Value cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Project updated;
                if (fieldIndex == 0) {
                    updated = p.withUpdatedDetails(
                            p.getProjectName(),
                            nextValue,
                            p.getDateConstructed(),
                            p.getMaterialsUsed(),
                            p.getFunction(),
                            p.getConservationStatus(),
                            p.getAddress(),
                            p.getDescription()
                    );
                } else if (fieldIndex == 1) {
                    updated = p.withUpdatedDetails(
                            p.getProjectName(),
                            p.getBuildingName(),
                            p.getDateConstructed(),
                            p.getMaterialsUsed(),
                            p.getFunction(),
                            p.getConservationStatus(),
                            nextValue,
                            p.getDescription()
                    );
                } else {
                    updated = p.withUpdatedDetails(
                            p.getProjectName(),
                            p.getBuildingName(),
                            p.getDateConstructed(),
                            p.getMaterialsUsed(),
                            nextValue,
                            p.getConservationStatus(),
                            p.getAddress(),
                            p.getDescription()
                    );
                }

                try {
                    ProjectRepository.updateProject(updated);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(instance, "Failed to update project.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static void installStatusEditor(UUID projectId, JLabel label) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SensorDataManager mgr = SensorDataManager.getInstance();
                String currentConn = mgr.getBuildingConnectionStatus(projectId);
                String currentOp = mgr.getBuildingOperationalStatus(projectId);

                String[] options = {"Toggle Connection", "Edit Operational Status", "Cancel"};
                int choice = JOptionPane.showOptionDialog(
                        instance,
                        "Choose an update:",
                        "Update Status",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]
                );

                if (choice == 0) {
                    String nextConn = "Connected".equalsIgnoreCase(currentConn) ? "Disconnected" : "Connected";
                    mgr.setBuildingConnectionStatus(projectId, nextConn);
                } else if (choice == 1) {
                    String nextOp = JOptionPane.showInputDialog(instance, "Operational Status:", currentOp);
                    if (nextOp == null) {
                        return;
                    }
                    nextOp = nextOp.trim();
                    if (nextOp.isEmpty()) {
                        JOptionPane.showMessageDialog(instance, "Value cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mgr.setBuildingOperationalStatus(projectId, nextOp);
                }
            }
        });
    }

    private void cleanupListeners() {
        if (removeRepoListener != null) {
            removeRepoListener.run();
            removeRepoListener = null;
        }
        if (removeDatasetListener != null) {
            removeDatasetListener.run();
            removeDatasetListener = null;
        }
        if (sensorListener != null) {
            sensorManager.removeChangeListener(sensorListener);
            sensorListener = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EngineerBldgStatusOverview::new);
    }
}
