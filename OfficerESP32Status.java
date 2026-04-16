import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class OfficerESP32Status extends JFrame {

    public OfficerESP32Status() {
        setTitle("AOMA-Heritage Monitor - ESP32 Status");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel officerPanel = new JPanel(null);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", officerPanel);
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
    
        JLabel LGULabel = new JLabel("LGU OFFICER ACCOUNT");
        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGULabel.setBounds(1080, 5, 280, 38);

        layeredPane.add(LGULabel, JLayeredPane.PALETTE_LAYER);

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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        officerPanel.add(centerPanel);

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

        JPanel buildingPanel = new JPanel();
        buildingPanel.setLayout(new BorderLayout());
        buildingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel buildingTitle = new JLabel("Building Profile Information");
        buildingTitle.setFont(new Font("Arial", Font.BOLD, 14));
        buildingTitle.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
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
            new EditStructuralDetails();
            this.dispose();
        });

        editStructureBtn.setEnabled(false);
        
        JPanel buildingHeaderPanel = new JPanel(new BorderLayout());
        buildingHeaderPanel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY));
        buildingHeaderPanel.setBackground(new Color(230,230,230));
        buildingPanel.add(buildingHeaderPanel, BorderLayout.NORTH);

        buildingTitle.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));

        buildingHeaderPanel.add(buildingTitle, BorderLayout.WEST);
        buildingHeaderPanel.add(editStructureBtn, BorderLayout.EAST);

        JPanel buildingInfoPanel = new JPanel();
        buildingInfoPanel.setLayout(new GridBagLayout());
        buildingInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addInfoField(buildingInfoPanel, "Project Name:", "Monitoring of San Diego de Alcala");
        addInfoField(buildingInfoPanel, "Building Name:", "San Diego De Alcala");
        addInfoField(buildingInfoPanel, "Year Constructed:", "1999");
        addInfoField(buildingInfoPanel, "Material Used Type:", "Wood, Brick");
        addInfoField(buildingInfoPanel, "Conservation Status:", "National Heritage");
        addInfoField(buildingInfoPanel, "Function:", "Church");
        addInfoField(buildingInfoPanel, "Address:", "1444 Marcelo H. Del Pilar St, Pulilan");

        addInfoArea(buildingInfoPanel, "Description:",
                "San Diego de Alcala Parish Church, located in Valenzuela, about 15.5 km north of Manila in the "
                + "Philippines. It is under the jurisdiction of the Diocese of Malolos.");

        addBottomSpacer(buildingInfoPanel);
        
        JScrollPane leftScroll = new JScrollPane(buildingInfoPanel);
        leftScroll.setBorder(null);
        buildingPanel.add(leftScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(buildingPanel);

        //RIGHT PANEL 
        JPanel sensorPanel = new JPanel(new BorderLayout());
        sensorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel sensorTitle = new JLabel("ESP32 Status");
        sensorTitle.setFont(new Font("Arial", Font.BOLD, 14));
        sensorTitle.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        sensorTitle.setOpaque(true);
        sensorTitle.setBackground(new Color(230,230,230));
        sensorPanel.add(sensorTitle, BorderLayout.NORTH);

        // this makes the box visible pang right panel
        JPanel syncPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        syncPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        sensorPanel.add(syncPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(sensorPanel);

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

    public static void main(String[] args) {
        new OfficerESP32Status();
    }
}

