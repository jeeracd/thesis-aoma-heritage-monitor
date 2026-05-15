import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class EngineerConfigureSensors extends JFrame {

    private JTable table;
    private SensorDataManager sensorManager;
    private UUID projectId;
    private Timer devicePollTimer;
    private JLabel footerLabel;
    private CardLayout rightCenterLayout;
    private JPanel rightCenterPanel;
    private AbstractTableModel tableModel;
    private JLabel projectNameValueLabel;
    private JLabel buildingNameValueLabel;
    private JLabel yearConstructedValueLabel;
    private JLabel materialUsedValueLabel;
    private JLabel conservationStatusValueLabel;
    private JLabel functionValueLabel;
    private JLabel addressValueLabel;
    private JTextArea descriptionValueArea;
    private Runnable removeProjectListener;

    public EngineerConfigureSensors() {
        this(null);
    }

    public EngineerConfigureSensors(UUID projectId) {
        sensorManager = SensorDataManager.getInstance();
        this.projectId = projectId;
        
        setTitle("AOMA-Heritage Monitor - Configure Sensor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.ENGINEER);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", headPanel);
        tabsUI.addTab("Help", new JPanel());

        tabsUI.setSelectedIndex(1);

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

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(new java.awt.Dimension(1300, 850));

        layeredPane.add(projectsDropdownBtn, JLayeredPane.PALETTE_LAYER);

        tabsUI.setBounds(0, 0, 1395, 770);
        layeredPane.add(tabsUI, JLayeredPane.DEFAULT_LAYER);

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
        viewMenuDropdownBtn.setFont(new Font("Arial", Font.BOLD, 14));
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

            if (selectedIndex == 0) {
                projectsMenu.show(
                        tabsUI,
                        bounds.x,
                        bounds.y + bounds.height
                );
            }

            if (selectedIndex == 2) {
                helpMenu.show(
                        tabsUI,
                        bounds.x,
                        bounds.y + bounds.height
                );
            }

            SwingUtilities.invokeLater(() -> tabsUI.setSelectedIndex(1));
        });

        JLabel LGULabel = new JLabel("STRUCTURAL ENGINEER ACCOUNT");
        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGULabel.setBounds(1080, 5, 280, 38);

        headPanel.add(LGULabel);

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
        headPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        headPanel.add(centerPanel);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerPanel.add(contentWrapper, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(4);
        splitPane.setEnabled(false);
        contentWrapper.add(splitPane, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel headerTitle = new JLabel("Building Profile Information");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        headerTitle.setOpaque(true);
        headerTitle.setBackground(new Color(230,230,230));

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
            new EngineerStructuralDetails();
            this.dispose();
        });

        JPanel buildingHeaderPanel = new JPanel(new BorderLayout());
        buildingHeaderPanel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.GRAY));
        buildingHeaderPanel.setBackground(new Color(230,230,230));
        leftPanel.add(buildingHeaderPanel, BorderLayout.NORTH);

        headerTitle.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));

        buildingHeaderPanel.add(headerTitle, BorderLayout.WEST);
        buildingHeaderPanel.add(editStructureBtn, BorderLayout.EAST);

        JPanel buildingInfoPanel = new JPanel();
        buildingInfoPanel.setLayout(new GridBagLayout());
        buildingInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int rowIndex = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl1 = new JLabel("Project Name:");
        lbl1.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl1, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        projectNameValueLabel = new JLabel("Not Set");
        projectNameValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(projectNameValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl2 = new JLabel("Building Name:");
        lbl2.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl2, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        buildingNameValueLabel = new JLabel("Not Set");
        buildingNameValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(buildingNameValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl3 = new JLabel("Year Constructed:");
        lbl3.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl3, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        yearConstructedValueLabel = new JLabel("Not Set");
        yearConstructedValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(yearConstructedValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl4 = new JLabel("Material Used Type:");
        lbl4.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl4, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        materialUsedValueLabel = new JLabel("Not Set");
        materialUsedValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(materialUsedValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl5 = new JLabel("Conservation Status:");
        lbl5.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl5, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        conservationStatusValueLabel = new JLabel("Not Set");
        conservationStatusValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(conservationStatusValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl6 = new JLabel("Function:");
        lbl6.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl6, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        functionValueLabel = new JLabel("Not Set");
        functionValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(functionValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl7 = new JLabel("Address:");
        lbl7.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl7, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        addressValueLabel = new JLabel("Not Set");
        addressValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingInfoPanel.add(addressValueLabel, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lbl8 = new JLabel("Description:");
        lbl8.setFont(new Font("Arial", Font.BOLD, 12));
        buildingInfoPanel.add(lbl8, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        descriptionValueArea = new JTextArea("Not Set");
        descriptionValueArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionValueArea.setEditable(false);
        descriptionValueArea.setLineWrap(true);
        descriptionValueArea.setWrapStyleWord(true);
        descriptionValueArea.setBackground(buildingInfoPanel.getBackground());
        descriptionValueArea.setBorder(null);
        buildingInfoPanel.add(descriptionValueArea, gbc);

        rowIndex++;
        gbc.gridy = rowIndex;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        buildingInfoPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane leftScroll = new JScrollPane(buildingInfoPanel);
        leftScroll.setBorder(null);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        reloadProjectDetails();
        if (projectId != null) {
            removeProjectListener = ProjectRepository.addChangeListener(
                    () -> SwingUtilities.invokeLater(this::reloadProjectDetails)
            );
        }

        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel rightTitle = new JLabel("Configure Sensor");
        rightTitle.setFont(new Font("Arial", Font.BOLD, 14));
        rightTitle.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        rightTitle.setOpaque(true);
        rightTitle.setBackground(new Color(230,230,230));
        rightPanel.add(rightTitle, BorderLayout.NORTH);

        table = new JTable();

        tableModel = new AbstractTableModel() {
            private String[] columns = sensorManager.getSensorColumns();

            @Override
            public int getRowCount() {
                return sensorManager.getTotalSensorCount();
            }

            @Override
            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return columns[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex >= sensorManager.getTotalSensorCount()) {
                    return null;
                }
                SensorDataManager.Sensor sensor = sensorManager.getAllSensors().get(rowIndex);
                switch (columnIndex) {
                    case 0: return sensor.getSensorId();
                    case 1: return sensor.getLocation();
                    case 2: return sensor.getStatus();
                    case 3: return sensor.getDeviceType();
                    case 4: return sensor.getTimestamp();
                    case 5: return sensor.getTimeSync();
                    default: return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                super.setValueAt(aValue, rowIndex, columnIndex);
            }
        };

        table.setModel(tableModel);
        sensorManager.addChangeListener(() -> {
            SwingUtilities.invokeLater(() -> {
                tableModel.fireTableDataChanged();
                sensorManager.validateCounts();
            });
        });

        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240,240,240));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK));

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);

        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 1));

        DefaultTableCellRenderer headerRenderer =
                (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();

        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setHorizontalAlignment(SwingConstants.CENTER);

                if (value != null && value.toString().equalsIgnoreCase("Connected")) {
                    label.setText("Connected");
                    label.setForeground(new Color(0,140,0));
                } else {
                    label.setText("Disconnected");
                    label.setForeground(Color.RED);
                }

                return label;
            }
        });

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);

                if (value.toString().contains("Error")) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(new Color(0, 140, 0));
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);

        JPanel emptyStatePanel = new JPanel(new GridBagLayout());
        JLabel emptyStateLabel = new JLabel(
                "<html>No active ESP32 devices detected.<br>Connect an ESP32 via USB to begin.</html>",
                SwingConstants.CENTER
        );
        emptyStateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emptyStatePanel.add(emptyStateLabel);

        rightCenterLayout = new CardLayout();
        rightCenterPanel = new JPanel(rightCenterLayout);
        rightCenterPanel.add(tableScroll, "TABLE");
        rightCenterPanel.add(emptyStatePanel, "EMPTY");
        rightPanel.add(rightCenterPanel, BorderLayout.CENTER);

        JPanel syncPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        syncPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton syncButton = new JButton("Sync now");
        syncButton.setPreferredSize(new Dimension(120,35));
        syncButton.setFocusPainted(false);

        syncButton.addActionListener(e -> {
            refreshEsp32Devices();
            sensorManager.validateCounts();
            JOptionPane.showMessageDialog(
                    this,
                    "Sensor data synchronized successfully!\n" +
                    "Total Sensors: " + sensorManager.getTotalSensorCount() + "\n" +
                    "Connected: " + sensorManager.getConnectedSensorCount() + "\n" +
                    "Disconnected: " + sensorManager.getDisconnectedSensorCount(),
                    "Sync Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        syncPanel.add(syncButton);
        rightPanel.add(syncPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setPreferredSize(new java.awt.Dimension(1400, 45));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(120, 120, 120)) );

        footerLabel = new JLabel("Status: ESP32 Hub Not Connected");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        footerLabel.setForeground(Color.RED);
        footerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        footerPanel.add(footerLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        refreshEsp32Devices();
        devicePollTimer = new Timer(2000, e -> refreshEsp32Devices());
        devicePollTimer.start();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (devicePollTimer != null) {
                    devicePollTimer.stop();
                }
                if (removeProjectListener != null) {
                    removeProjectListener.run();
                }
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (devicePollTimer != null) {
                    devicePollTimer.stop();
                }
                if (removeProjectListener != null) {
                    removeProjectListener.run();
                }
            }
        });

        setVisible(true);
    }

    private void refreshEsp32Devices() {
        java.util.List<SensorDataManager.Sensor> devices = sensorManager.scanActiveEsp32Devices();
        sensorManager.setSensors(devices);

        boolean hasDevices = !devices.isEmpty();
        if (rightCenterLayout != null && rightCenterPanel != null) {
            rightCenterLayout.show(rightCenterPanel, hasDevices ? "TABLE" : "EMPTY");
        }

        if (footerLabel != null) {
            if (hasDevices) {
                footerLabel.setText("Status: ESP32 Hub Connected");
                footerLabel.setForeground(new Color(0, 140, 0));
            } else {
                footerLabel.setText("Status: ESP32 Hub Not Connected");
                footerLabel.setForeground(Color.RED);
            }
        }
    }

    private void reloadProjectDetails() {
        Project project = null;
        if (projectId != null) {
            Optional<Project> maybeProject = ProjectRepository.findById(projectId);
            if (maybeProject.isPresent()) {
                project = maybeProject.get();
            }
        }

        if (projectNameValueLabel != null) {
            projectNameValueLabel.setText(valueOrNotSet(project == null ? "" : project.getProjectName()));
        }
        if (buildingNameValueLabel != null) {
            buildingNameValueLabel.setText(valueOrNotSet(project == null ? "" : project.getBuildingName()));
        }
        if (yearConstructedValueLabel != null) {
            yearConstructedValueLabel.setText(valueOrNotSet(project == null ? "" : project.getDateConstructed()));
        }
        if (materialUsedValueLabel != null) {
            materialUsedValueLabel.setText(valueOrNotSet(project == null ? "" : project.getMaterialsUsed()));
        }
        if (conservationStatusValueLabel != null) {
            conservationStatusValueLabel.setText(valueOrNotSet(project == null ? "" : project.getConservationStatus()));
        }
        if (functionValueLabel != null) {
            functionValueLabel.setText(valueOrNotSet(project == null ? "" : project.getFunction()));
        }
        if (addressValueLabel != null) {
            addressValueLabel.setText(valueOrNotSet(project == null ? "" : project.getAddress()));
        }
        if (descriptionValueArea != null) {
            descriptionValueArea.setText(valueOrNotSet(project == null ? "" : project.getDescription()));
        }
    }

    private static String valueOrNotSet(String value) {
        if (value == null) {
            return "Not Set";
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? "Not Set" : trimmed;
    }

    public static void main(String[] args) {
        new EngineerConfigureSensors();
    }
}
