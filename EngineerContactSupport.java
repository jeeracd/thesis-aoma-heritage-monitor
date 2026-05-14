import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class EngineerContactSupport extends JFrame {

    public EngineerContactSupport() {

        setTitle("AOMA-Heritage Monitor - Contact Support");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        // TABBED PANE

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel contactSupportPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.ENGINEER);
        contactSupportPanel.setBackground(Color.WHITE);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", new JPanel());
        tabsUI.addTab("Help", contactSupportPanel);

        tabsUI.setSelectedIndex(2);

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

                g.drawString(
                        title,
                        textRect.x,
                        textRect.y + metrics.getAscent()
                );
            }

            @Override
            protected Insets getTabInsets(
                    int tabPlacement,
                    int tabIndex
            ) {
                return new Insets(6, 20, 6, 20);
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
        // ACCOUNT LABEL

        JLabel LGULabel = new JLabel("STRUCTURAL ENGINEER ACCOUNT");

        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));

        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);

        LGULabel.setBounds(1080, 5, 280, 38);

        contactSupportPanel.add(LGULabel);

        // TOP TITLE PANEL

        JPanel centerPanelDescription =
                new JPanel(new BorderLayout());

        centerPanelDescription.setBounds(
                10,
                20,
                1380,
                40
        );

        Border firstBorder =
                BorderFactory.createLineBorder(
                        Color.BLACK,
                        2
                );

        centerPanelDescription.setBorder(firstBorder);

        JLabel centerTitleLabel = new JLabel(
                "Automated - Operational Modal Analysis to Monitor the Safety and Serviceability of Heritage Buildings",
                JLabel.CENTER
        );

        centerTitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC | Font.BOLD,
                        20
                )
        );

        centerTitleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        5,
                        10
                )
        );

        ImageIcon userIcon = new ImageIcon("usericon.png");
        Image userImgScaled = userIcon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
        JLabel userIconLabel = new JLabel(new ImageIcon(userImgScaled));
        userIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Engineer POPUP MENU
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
        contactSupportPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.setBounds(
                10,
                70,
                1380,
                648
        );

        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK,2);
        centerPanel.setBorder(secondBorder);
        contactSupportPanel.add(centerPanel);

        // HEADER LABEL
        JLabel greetingLabel = new JLabel("Contact Support",JLabel.LEFT);

        greetingLabel.setFont(
                new Font("Arial",Font.BOLD,18));

        greetingLabel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createMatteBorder(
                                0,0,2,0,new Color(120, 120, 120)),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );

        centerPanel.add(
                greetingLabel,
                BorderLayout.NORTH
        );

        // SUPPORT CARDS CONTAINER

        JPanel supportContainer = new JPanel();

        supportContainer.setBackground(Color.WHITE);

        supportContainer.setLayout(
                new FlowLayout(
                        FlowLayout.CENTER,
                        45,
                        35
                )
        );

        // SUPPORT CARDS

        supportContainer.add(createSupportCard(
                "JOHN RAFAEL A. ALEJANDRINO",
                "202310450",
                "09561260465",
                "Balangkas, Valenzuela City",
                "alejandrino.johnrafael@gmail.com",
                "FEU TECH"
        ));

        supportContainer.add(createSupportCard(
                "JUDINELE LORENZ P. PINZA",
                "202310630",
                "09613511301",
                "Lias, Marilao, Bulacan",
                "pinzajud@gmail.com",
                "FEU TECH"
        ));

        supportContainer.add(createSupportCard(
                "JEROME M. DEL ROSARIO",
                "202311234",
                "09763028338",
                "Corazon de Jesus, San Juan City",
                "delrosariomrj@gmail.com",
                "FEU TECH"
        ));

        supportContainer.add(createSupportCard(
                "RALPH CHRISTIAN A. DEL MUNDO ",
                "202211430",
                "09569534569",
                "Brgy. Bagbag Novaliches, Quezon City",
                "delmundo.rc@gmail.com",
                "FEU TECH"
        ));

        centerPanel.add(
                supportContainer,
                BorderLayout.CENTER
        );

        // FOOTER PANEL

        JPanel footerPanel =
                new JPanel(new BorderLayout());

        footerPanel.setPreferredSize(
                new Dimension(1400, 45)
        );

        footerPanel.setBorder(
                BorderFactory.createMatteBorder(
                        3,
                        0,
                        0,
                        0,
                        new Color(120, 120, 120)
                )
        );

        JLabel footerLabel =
                new JLabel(
                        "Status: ESP32 Hub Not Connected"
                );

        footerLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        footerLabel.setForeground(Color.RED);

        footerLabel.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        footerLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        10,
                        0,
                        0
                )
        );

        footerPanel.add(
                footerLabel,
                BorderLayout.CENTER
        );

        // FRAME LAYOUT
        setLayout(new BorderLayout());
        add(layeredPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    //  SUPPORT CARD 
    private JPanel createSupportCard(
            String name,
            String sn,
            String phone,
            String address,
            String email,
            String school
    ) {

        JPanel card = new JPanel();

        card.setPreferredSize(
                new Dimension(240, 500)
        );

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createLineBorder(
                        Color.BLACK,
                        2
                )
        );
        card.setLayout(null);

        // ICON
        JLabel iconLabel = new JLabel();

        iconLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        iconLabel.setBounds(65, 20, 100, 100);

        try {

            ImageIcon icon =
                    new ImageIcon("usericon.png");
            Image scaled =
                    icon.getImage().getScaledInstance(
                            85,
                            85,
                            Image.SCALE_SMOOTH
                    );
            iconLabel.setIcon(
                    new ImageIcon(scaled)
            );
        } catch (Exception e) {
            iconLabel.setText("USER");

        }

        // TEXT AREA
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFocusable(false);
        infoArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        infoArea.setBackground(Color.WHITE);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText(
                "NAME: " + name + "\n\n" +
                "SN: " + sn + "\n\n" +
                "Phone: " + phone + "\n\n" +
                "Residential Address:\n" +
                address + "\n\n" +
                "Email Address:\n" +
                email + "\n\n" +
                "School: " + school
        );

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(null);
        scrollPane.setBounds(
                15,
                120,
                205,
                360
        );

        // COMPONENTS
        card.add(iconLabel);
        card.add(scrollPane);

        return card;
    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new EngineerContactSupport();
        });
    }
}
