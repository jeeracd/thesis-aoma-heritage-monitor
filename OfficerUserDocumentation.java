import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class OfficerUserDocumentation extends JFrame {

    public OfficerUserDocumentation() {
        setTitle("AOMA-Heritage Monitor - User Documentation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel userDocumentationPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.OFFICER);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", new JPanel());
        tabsUI.addTab("Help", userDocumentationPanel);

        tabsUI.setSelectedIndex(2); //set default tab

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
        JMenuItem openProject = new JMenuItem("Open Project");
        JMenuItem importCsv = new JMenuItem("Import Sensor Data (.csv)");
        JMenuItem exportPDF = new JMenuItem("Export Report (PDF)");

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
        exportPDF.setEnabled(false);
        exit.setEnabled(true);

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

        if (selectedIndex == 1) { // View clicked
            viewMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }

        // Always return to Help tab
        SwingUtilities.invokeLater(() -> tabsUI.setSelectedIndex(2));
    });

        JLabel LGULabel = new JLabel("LGU OFFICER ACCOUNT");
        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGULabel.setBounds(1080, 5, 280, 38);

        userDocumentationPanel.add(LGULabel);

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

        // officer POPUP MENU
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            dispose();
            new OfficerDashboardUserSettings();
        });

        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    OfficerUserDocumentation.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
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

        // Show popup when clicked
        userIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                userMenu.show(userIconLabel, 0, userIconLabel.getHeight());
            }
        });

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);

        userDocumentationPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        userDocumentationPanel.add(centerPanel);

        JLabel greetingLabel = new JLabel("User Documentation", JLabel.LEFT); 
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        greetingLabel.setBorder( BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)), 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        centerPanel.add(greetingLabel, BorderLayout.NORTH);

        // Documentation content - newly added
        JTextArea documentationArea = new JTextArea();

        documentationArea.setFont(new Font("Arial", Font.PLAIN, 24));
        documentationArea.setEditable(false);
        documentationArea.setLineWrap(true);
        documentationArea.setWrapStyleWord(true);
        documentationArea.setMargin(new Insets(15, 15, 15, 15));

        documentationArea.setText(
                "1. System Features\n\n" +

                "The AOMA Heritage Monitor provides the following features:\n\n" +

                "* Real-time vibration monitoring\n" +
                "* Structural health monitoring\n" +
                "* Automated modal analysis\n" +
                "* Project management\n" +
                "* Sensor management\n" +
                "* Report generation\n" +
                "* Data visualization\n" +
                "* Historical data storage\n" +
                "* User account management\n" +
                "* Structural assessment dashboard\n\n" +

                "------------------------------------------------------------\n\n" +

                "2. User Roles and Permissions\n\n" +

                "User Role Permissions\n\n" +

                "* LGU Head - Manage accounts, review reports, monitor projects\n" +
                "* Structural Engineer - Perform analysis, generate reports, manage projects\n" +
                "* LGU Officer - Setup ESP32 placements, create projects, monitor data\n\n" +

                "------------------------------------------------------------\n\n" +

                "3. Hardware Requirements\n\n" +
                "Minimum Hardware Requirements\n\n" +
                "Component | Specification \n\n" +

                "* Processor - Dual-core processor (2.0 GHz or higher).\n" +
                "* RAM - 4GB.\n" +
                "* Storage - 50 GB HDD or SSD \n\n" +

                "IoT Devices Used\n\n" +
                "* ESP32-WROOM-32\n" +
                "* MPU6050 MEMS Accelerometers.\n" +
                "* 3.3V Li/Po Batteries \n\n" +

                "------------------------------------------------------------\n\n" +

                "4. Software Requirements\n\n" +
                "The following software is required to run the system:\n\n" +

                "* Windows 10 Operating System.\n" +
                "* Python Programming Language.\n" +
                "* PyOMA2 Library.\n" +
                "* PostgreSQL Database Management System.\n" +
                "* Required Python Dependencies.\n\n" +

                "------------------------------------------------------------\n\n" +

                "5. User Guide\n\n" +

                "Login Procedure\n\n" +

                "1. Open the system.\n" +
                "2. Enter username and password.\n" +
                "3. Click the Login button.\n\n" +

                "Create Project\n\n" +
                "1. Navigate to the Project Management section.\n" +
                "2. Click Create Project.\n" +
                "2. Enter project details:\n\n" +

                "Example:\n\n" +
                
                "Project Name\n" +
                "Location\n" +
                "etc..\n\n" +

                "Setup ESP32 Placements\n\n" +

                "1. Assign ESP32 sensor nodes to designated areas.\n" +
                "2. Verify proper sensor placement.\n" +
                "3. Ensure all devices are connected.\n\n" +

                "View Analysis Results\n\n" +

                "1. Open the Analysis Dashboard.\n" +
                "2. View extracted modal parameters.\n" +
                "3. Review graphs and visualizations.\n\n" +

                "Generate Reports\n\n" +

                "1. Navigate to Reports.\n" +
                "2. Select the desired project.\n" +
                "3. Click Generate Report.\n" + 
                "4. Save the report.\n\n" +
                
                "------------------------------------------------------------\n\n" +

                "6. Understanding the Results\n\n" +

                "--Natural Frequency--\n" +
                "* Natural Frequency refers to the vibration cycles of the structure over time. Significant changes may indicate structural deterioration.\n\n" +

                "--Damping Ratio--\n" +
                "* Damping Ratio measures how quickly vibrations decrease within the structure.\n\n" +

                "--Mode Shape--\n" +
                "* Mode Shape represents the movement pattern of the structure during vibration.\n\n" +

                "--MAC Value--\n" +
                "* The Modal Assurance Criterion (MAC) Value is used to evaluate the reliability and similarity of modal parameters.\n\n" +

                "--RMS Analysis--\n" +
                "* Root Mean Square (RMS) analysis helps identify irregular vibration behavior and noise.\n\n" +

                "------------------------------------------------------------\n\n" +

                "7. Troubleshooting Guide\n\n" +

                "| Problem | Possible Cause | Solution |\n" + 
                "--------------------------------------------------\n" + 
                "ESP32 device not detected -> Device disconnected -> [Reconnect the device]\n" + 
                "No vibration data -> Sensor issue  -> [Check sensor wiring]\n" + 
                "Slow data processing -> Low system memory -> [Close unused applications]\n" + 
                "Connection failure  -> ESP-NOW interruption -> [Restart ESP32 devices]\n\n" + 

                "------------------------------------------------------------\n\n" +

                "8. Safety and Usage Notes\n\n" +

                "1. Ensure proper placement of sensors before data collection.\n" +
                "2. Avoid moving ESP32 devices during monitoring.\n" +
                "3. Maintain stable power supply for all devices.\n" + 
                "4. Ensure proper internet or local connection during operation.\n" + 
                "5. Only authorized personnel should modify system settings.\n\n" +

                "------------------------------------------------------------\n\n" +

                "9. Safety and Usage Notes\n\n" +

                "To maintain system performance:\n\n" +

                "1. Regularly inspect sensors and ESP32 devices.\n" +
                "2. Backup monitoring data frequently.\n" +
                "3. Check battery levels before data acquisition.\n" + 
                "4. Verify calibration of accelerometers periodically.\n\n"
        );

        JScrollPane scrollPane = new JScrollPane(documentationArea);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        centerPanel.add(scrollPane, BorderLayout.CENTER); //end of documentation content code

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

    public static void main(String[] args) {
        new OfficerUserDocumentation();
    }
}
