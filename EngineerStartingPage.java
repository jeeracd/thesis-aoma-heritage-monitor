import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class EngineerStartingPage extends JFrame {
    private JLabel greetingLabel;
    private Runnable removeProfileListener = () -> {};

    public EngineerStartingPage() {
        setTitle(EngineerUiNames.ENGINEER_ACCOUNT_WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel engineerPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.ENGINEER);

        tabsUI.addTab(EngineerUiNames.TAB_PROJECTS, engineerPanel);
        tabsUI.addTab(EngineerUiNames.TAB_VIEW, new JPanel());
        tabsUI.addTab(EngineerUiNames.TAB_HELP, new JPanel());

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

        JMenuItem newProject = new JMenuItem(EngineerUiNames.MENU_NEW_PROJECT);
        newProject.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "New Project initialization process will start.",
                    EngineerUiNames.MENU_NEW_PROJECT,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerEditStructuralDetails();
            this.dispose();
        });

        JMenuItem openProject = new JMenuItem(EngineerUiNames.MENU_OPEN_PROJECT);
        JMenuItem importCsv = new JMenuItem(EngineerUiNames.MENU_IMPORT_SENSOR_DATA);
        importCsv.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Import Sensor Data page.",
                    EngineerUiNames.MENU_IMPORT_SENSOR_DATA,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerImportSensorData();
            this.dispose();
        });

        JMenuItem exportPDF = new JMenuItem(EngineerUiNames.MENU_EXPORT_REPORT_PDF);
        exportPDF.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Export Report page.",
                    EngineerUiNames.MENU_EXPORT_REPORT_PDF,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerExportSensorData();
            this.dispose();
        });

        JMenuItem exit = new JMenuItem(EngineerUiNames.MENU_EXIT);
        exit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    EngineerUiNames.TITLE_EXIT_CONFIRMATION,
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

        JMenuItem dashboardView = new JMenuItem(EngineerUiNames.MENU_DASHBOARD_VIEW);
        dashboardView.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Dashboard View.",
                    EngineerUiNames.MENU_DASHBOARD_VIEW,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerBldgStatusOverview();
            this.dispose();
        });

        JMenuItem setupConnection = new JMenuItem(EngineerUiNames.MENU_SETUP_CONNECTION);
        setupConnection.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Setup & Connection page.",
                    EngineerUiNames.MENU_SETUP_CONNECTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerSetupConnectionWindow();
            this.dispose();
        });


        JMenuItem configureSensor = new JMenuItem(EngineerUiNames.MENU_CONFIGURE_SENSOR);
        configureSensor.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Configure Sensor page.",
                    EngineerUiNames.MENU_CONFIGURE_SENSOR,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerConfigureSensorWindow();
            this.dispose();
        });

        JMenuItem esp32Status = new JMenuItem(EngineerUiNames.MENU_ESP32_STATUS);
        esp32Status.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to ESP32 Status page.",
                    EngineerUiNames.MENU_ESP32_STATUS,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerESP32StatusWindow();
            this.dispose();
        });

        JMenuItem vibrationData = new JMenuItem(EngineerUiNames.MENU_VIBRATION_DATA);
        vibrationData.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Vibration Data page.",
                    EngineerUiNames.MENU_VIBRATION_DATA,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerVibrationDataWindow();
            this.dispose();
        }); 

        JMenuItem omaAnalysisResult = new JMenuItem(EngineerUiNames.MENU_OMA_ANALYSIS_RESULT);
        omaAnalysisResult.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to OMA Analysis Result page.",
                    EngineerUiNames.MENU_OMA_ANALYSIS_RESULT,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerOMAAnalysisResultWindow();
            this.dispose();
        });

        JMenuItem reportHistory = new JMenuItem(EngineerUiNames.MENU_VIEW_REPORT);
        reportHistory.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to View Report page.",
                    EngineerUiNames.MENU_VIEW_REPORT,
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerViewReportWindow();
            this.dispose();
        });

        JMenuItem systemLogs = new JMenuItem(EngineerUiNames.MENU_SYSTEM_LOGS);
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
        if (tabsUI.getTabCount() <= 1) {
            return;
        }
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

        JMenuItem sensorSetupGuide = new JMenuItem(EngineerUiNames.MENU_SENSOR_SETUP_GUIDE);
        sensorSetupGuide.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Sensor Setup Guide.",
                    EngineerUiNames.MENU_SENSOR_SETUP_GUIDE,
                    JOptionPane.INFORMATION_MESSAGE
            );
            RoleMenuBar.navigate(this, EngineerSensorSetupGuide::new);
        });

        JMenuItem userDocumentation = new JMenuItem(EngineerUiNames.MENU_USER_DOCUMENTATION);
        userDocumentation.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to User Documentation.",
                    EngineerUiNames.MENU_USER_DOCUMENTATION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            RoleMenuBar.navigate(this, EngineerUserDocumentation::new);
        });

        JMenuItem aboutAOMA = new JMenuItem(EngineerUiNames.MENU_ABOUT);
        aboutAOMA.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to About AOMA-Heritage Monitor.",
                    EngineerUiNames.MENU_ABOUT,
                    JOptionPane.INFORMATION_MESSAGE
            );
            RoleMenuBar.navigate(this, EngineerAboutAOMA::new);
        });

        JMenuItem contactSupport = new JMenuItem(EngineerUiNames.MENU_CONTACT_SUPPORT);
        contactSupport.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Contact Support.",
                    EngineerUiNames.MENU_CONTACT_SUPPORT,
                    JOptionPane.INFORMATION_MESSAGE
            );
            RoleMenuBar.navigate(this, EngineerContactSupport::new);
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
        if (tabsUI.getTabCount() <= 2) {
            return;
        }
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

        if (selectedIndex == 1) { // View tab clicked
            tabsUI.setSelectedIndex(0); // go back to Projects

            Rectangle bounds = tabsUI.getBoundsAt(1);
            viewMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }

        if (selectedIndex == 2 && tabsUI.getTabCount() > 2) { // Help tab clicked
            tabsUI.setSelectedIndex(0); // go back to Projects

            Rectangle bounds = tabsUI.getBoundsAt(2);
            helpMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }
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

        // engr POPUP MENU
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem userSettings = new JMenuItem(EngineerUiNames.MENU_USER_SETTINGS);

        // Open Engineer Dashboard User Settings
        userSettings.addActionListener(e -> {
            dispose(); 
            new EngineerDashboardUserSettings(); // opens settings window
        });
        JMenuItem logout = new JMenuItem(EngineerUiNames.MENU_LOGOUT);
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    EngineerUiNames.TITLE_LOGOUT_CONFIRMATION,
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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        engineerPanel.add(centerPanel);

        greetingLabel = new JLabel("", JLabel.LEFT);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        greetingLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        greetingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        greetingLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (EngineerPreferences.getAccessLevel() == EngineerPreferences.AccessLevel.VIEWER) {
                    return;
                }
                editNameFromStartingPage();
            }
        });
        updateGreetingLabel();
        removeProfileListener = EngineerProfileStore.addListener(this::updateGreetingLabel);
        centerPanel.add(greetingLabel, BorderLayout.NORTH);

        JPanel textContainer = new JPanel();
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setOpaque(false);

        ImageIcon imageIcon = new ImageIcon("emptybox.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel textHeaderLabel = new JLabel(
                "Welcome to the AOMA-Heritage Monitor, ENGINEER ACCOUNT!",JLabel.CENTER);
        textHeaderLabel.setFont(new Font("Arial", Font.BOLD, 22));
        textHeaderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel textSubheaderLabel = new JLabel("You currently do not have access to any active monitoring projects." + 
                                                " Please contact your Local Government Unit Head to be assigned to a project",JLabel.CENTER);
        textSubheaderLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        textSubheaderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        textContainer.add(Box.createVerticalGlue());
        textContainer.add(imageLabel);
        textContainer.add(Box.createVerticalStrut(12)); 
        textContainer.add(textHeaderLabel);
        textContainer.add(Box.createVerticalStrut(12)); 
        textContainer.add(textSubheaderLabel);
        textContainer.add(Box.createVerticalGlue());

        centerPanel.add(textContainer, BorderLayout.CENTER);

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

    private void updateGreetingLabel() {
        if (greetingLabel == null) {
            return;
        }
        greetingLabel.setText("Welcome, Engr. " + EngineerProfileStore.getFullName() + "!");
    }

    private void editNameFromStartingPage() {
        JTextField first = new JTextField(EngineerProfileStore.getFirstName());
        JTextField last = new JTextField(EngineerProfileStore.getLastName());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel("First Name"));
        p.add(first);
        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Last Name"));
        p.add(last);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Name", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) {
            return;
        }
        boolean updated = EngineerProfileStore.setName(first.getText(), last.getText());
        if (!updated) {
            JOptionPane.showMessageDialog(this, "Invalid name values.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        removeProfileListener.run();
        super.dispose();
    }

    public static void main(String[] args) {
        new EngineerStartingPage();
    }
}
