import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class HeadViewDetails extends JFrame {

    public HeadViewDetails() {
        setTitle("AOMA-Heritage Monitor - View Details (HEAD)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 1500);
        setLocationRelativeTo(null);

        //TABBED PANE 
        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 15)); 
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);

        tabsUI.addTab("Projects", headPanel);
        tabsUI.addTab("View", new JPanel());
        tabsUI.addTab("Help", new JPanel());

        tabsUI.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {

            @Override
            protected Insets getTabInsets(int tabPlacement, int tabIndex) {
                return new Insets(14, 32, 14, 32); // taller + wider tabs
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 50; 
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 180; 
            }

            @Override
            protected void paintTabBackground(
                    Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(new Color(0, 102, 204));
                    g.fillRect(x, y, w, h);
                }
            }

            @Override
            protected void paintText(
                    Graphics g, int tabPlacement, Font font,
                    FontMetrics metrics, int tabIndex,
                    String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                g.setColor(isSelected ? Color.WHITE : Color.BLACK);
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }
        });
        
        //projects menu
        JPopupMenu projectsMenu = new JPopupMenu();

        JMenuItem newProject = new JMenuItem("New Project");
        JMenuItem openProject = new JMenuItem("Open Project");
        JMenuItem importCsv = new JMenuItem("Import Sensor Data (.csv)");
        JMenuItem exportPdf = new JMenuItem("Export Report (PDF)");
        JMenuItem exit = new JMenuItem("Exit");

        projectsMenu.add(newProject);
        projectsMenu.add(openProject);
        projectsMenu.addSeparator();
        projectsMenu.add(importCsv);
        projectsMenu.add(exportPdf);
        projectsMenu.addSeparator();
        projectsMenu.add(exit);

        JButton projectsDropdownBtn = new JButton("▼");
        projectsDropdownBtn.setFont(new Font("Arial", Font.BOLD, 14));
        projectsDropdownBtn.setFocusPainted(false);
        projectsDropdownBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        projectsDropdownBtn.setContentAreaFilled(false);
        projectsDropdownBtn.setBorderPainted(false);
        projectsDropdownBtn.setOpaque(false);

        projectsDropdownBtn.setForeground(Color.BLACK);
        projectsDropdownBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(new Dimension(1400, 1500));

        tabsUI.setBounds(0, 0, 1395, 1900); 
        layeredPane.add(tabsUI, JLayeredPane.DEFAULT_LAYER);


        projectsDropdownBtn.addActionListener(e ->
            projectsMenu.show(projectsDropdownBtn, 0, projectsDropdownBtn.getHeight())
        );

        SwingUtilities.invokeLater(() -> {
            Rectangle projectTabBounds = tabsUI.getBoundsAt(0);
            int arrowSize = 22;

            projectsDropdownBtn.setBounds(
                projectTabBounds.x + projectTabBounds.width - arrowSize - 4,
                projectTabBounds.y + (projectTabBounds.height - arrowSize) / 2,
                arrowSize,
                arrowSize
            );

            layeredPane.add(projectsDropdownBtn, JLayeredPane.PALETTE_LAYER);
        });

        //VIEW MENU 
        JPopupMenu viewMenu = new JPopupMenu();

        JMenuItem dashboardView = new JMenuItem("Dashboard View");

        JMenuItem setupConnection = new JMenuItem("Setup Connection");
        JMenuItem configureSensor = new JMenuItem("Configure Sensor");
        JMenuItem esp32Status = new JMenuItem("ESP32 Status");

        JMenuItem vibrationData = new JMenuItem("Vibration Data");
        JMenuItem omaAnalysisResult = new JMenuItem("OMA Analysis Result");

        JMenuItem reportHistory = new JMenuItem("View Report");

        JMenuItem systemLogs = new JMenuItem("System Logs");

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
        JMenuItem userDocumentation = new JMenuItem("User Documentation");
        JMenuItem aboutAOMA = new JMenuItem("About AOMA-Heritage Monitor");
        JMenuItem contactSupport = new JMenuItem("Contact Support");

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

        JLabel LGUHeadLabel = new JLabel("LGU HEAD ACCOUNT", SwingConstants.RIGHT);
        LGUHeadLabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGUHeadLabel.setBounds(1080, 5, 280, 38);
        layeredPane.add(LGUHeadLabel, JLayeredPane.PALETTE_LAYER);

        //HEADER BAR 
        JPanel centerPanelDescription = new JPanel(new BorderLayout());
        centerPanelDescription.setBounds(10, 20, 1380, 40);
        centerPanelDescription.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel centerTitleLabel = new JLabel(
                "Automated - Operational Modal Analysis to Monitor the Safety and Serviceability of Heritage Buildings",
                JLabel.CENTER
        );
        centerTitleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 20));
        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        headPanel.add(centerPanelDescription);

        //MAIN CENTER PANEL
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 1200);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        headPanel.add(centerPanel);

        //HEADER AREA
        JPanel headerArea = new JPanel(new BorderLayout());
        headerArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); 

        JLabel headerLabel = new JLabel("View Details");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(10, 10, 10, 0)  
        ));

        headerArea.add(headerLabel, BorderLayout.NORTH);
        
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        buttonRow.setOpaque(false);

        JButton manageAccessBtn = new JButton("Manage Access");
        manageAccessBtn.setFont(new Font("Arial", Font.BOLD, 13));
        manageAccessBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manageAccessBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new HeadDashboardManageAccess();
            });
            dispose(); 
        });

        buttonRow.add(manageAccessBtn);

        headerArea.add(buttonRow, BorderLayout.SOUTH);
        centerPanel.add(headerArea, BorderLayout.NORTH);


        //FORM PANEL
        JPanel formPanel = new JPanel(null);

        // ================= SCROLLABLE FORM CONTAINER =================
        JScrollPane centerScrollPane = new JScrollPane(
                formPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // keep layout intact
        centerScrollPane.setBorder(null);

        // ensure scrollbar is always usable
        JScrollBar verticalBar = centerScrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(20);     // mouse wheel speed
        verticalBar.setBlockIncrement(120);   // page up / page down speed
        verticalBar.setFocusable(true);
        verticalBar.setEnabled(true);

        // ensure mouse wheel scrolling works
        centerScrollPane.setWheelScrollingEnabled(true);

        // IMPORTANT: give the viewport a background so scrollbar renders correctly
        centerScrollPane.getViewport().setBackground(Color.WHITE);

        // add to center panel (no layout damage)
        centerPanel.add(centerScrollPane, BorderLayout.CENTER);

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Border fieldBorder = BorderFactory.createLineBorder(Color.BLACK);

        int leftX = 20;
        int rightX = 700;
        int y = 20;
        int gap = 70;

        JLabel projectNameLbl = new JLabel("Project Name");
        projectNameLbl.setFont(labelFont);
        projectNameLbl.setBounds(leftX, y, 200, 10);
        formPanel.add(projectNameLbl);

        JTextField projectName = new JTextField();
        projectName.setFont(fieldFont);
        projectName.setBorder(fieldBorder);
        projectName.setBounds(leftX, y + 30, 1330, 30);
        projectName.setEditable(false);
        projectName.setFocusable(false);
        projectName.setBackground(Color.WHITE);
        formPanel.add(projectName);

        y += 80;

        JLabel structuralLbl = new JLabel("Structural Details");
        structuralLbl.setFont(new Font("Arial", Font.BOLD, 16));
        structuralLbl.setBounds(leftX, y, 300, 30);
        formPanel.add(structuralLbl);

        y += 40;

        JLabel buildingLbl = new JLabel("Building Name:");
        buildingLbl.setFont(labelFont);
        buildingLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(buildingLbl);

        JTextField buildingField = new JTextField();
        buildingField.setFont(fieldFont);
        buildingField.setBorder(fieldBorder);
        buildingField.setBounds(leftX + 160, y, 500, 30);
        buildingField.setEditable(false);
        buildingField.setFocusable(false);
        buildingField.setBackground(Color.WHITE);
        formPanel.add(buildingField);

        JLabel materialsLbl = new JLabel("Materials Used:");
        materialsLbl.setFont(labelFont);
        materialsLbl.setBounds(rightX, y, 150, 25);
        formPanel.add(materialsLbl);

        JTextField materialsField = new JTextField();
        materialsField.setFont(fieldFont);
        materialsField.setBorder(fieldBorder);
        materialsField.setBounds(rightX + 150, y, 500, 30);
        materialsField.setEditable(false);
        materialsField.setFocusable(false);
        materialsField.setBackground(Color.WHITE);
        formPanel.add(materialsField);

        y += gap;

        JLabel dateLbl = new JLabel("Date Constructed:");
        dateLbl.setFont(labelFont);
        dateLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(dateLbl);

        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBounds(leftX + 160, y, 300, 30);
        datePanel.setBorder(fieldBorder);
        datePanel.setBackground(Color.WHITE);

        JTextField dateField = new JTextField();
        dateField.setFont(fieldFont);
        dateField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        dateField.setEditable(false);
        dateField.setFocusable(false);
        dateField.setBackground(Color.WHITE);
        datePanel.add(dateField, BorderLayout.CENTER);

        formPanel.add(datePanel);

        JLabel functionLbl = new JLabel("Function:");
        functionLbl.setFont(labelFont);
        functionLbl.setBounds(rightX, y, 150, 25);
        formPanel.add(functionLbl);

        JTextField functionField = new JTextField();
        functionField.setFont(fieldFont);
        functionField.setBorder(fieldBorder);
        functionField.setBounds(rightX + 150, y, 500, 30);
        functionField.setEditable(false);
        functionField.setFocusable(false);
        functionField.setBackground(Color.WHITE);
        formPanel.add(functionField);

        y += gap;

        JLabel conservationLbl = new JLabel("Conservation Status:");
        conservationLbl.setFont(labelFont);
        conservationLbl.setBounds(leftX, y, 170, 25);
        formPanel.add(conservationLbl);

        JTextField conservationField = new JTextField();
        conservationField.setFont(fieldFont);
        conservationField.setBorder(fieldBorder);
        conservationField.setBounds(leftX + 160, y, 300, 30);
        conservationField.setEditable(false);
        conservationField.setFocusable(false);
        conservationField.setBackground(Color.WHITE);
        formPanel.add(conservationField);

        y += gap;

        JLabel addressLbl = new JLabel("Address:");
        addressLbl.setFont(labelFont);
        addressLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(addressLbl);

        JTextField addressField = new JTextField();
        addressField.setFont(fieldFont);
        addressField.setBorder(fieldBorder);
        addressField.setBounds(leftX + 160, y, 1170, 30);
        addressField.setEditable(false);
        addressField.setFocusable(false);
        addressField.setBackground(Color.WHITE);
        formPanel.add(addressField);

        y += gap;

        JLabel descLbl = new JLabel("Description:");
        descLbl.setFont(labelFont);
        descLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(descLbl);

        JTextArea descArea = new JTextArea();
        descArea.setFont(fieldFont);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setBackground(Color.WHITE);

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBounds(leftX + 160, y, 1170, 80);
        formPanel.add(descScroll);


        //executive summary (NON TECHNICAL) and Full Technical Audit (STANDARD) tabbedpane
        
        // ================= EXECUTIVE SUMMARY & TECHNICAL AUDIT =================
        y += 90; // space after Description section

        JTabbedPane reportTabs = new JTabbedPane(JTabbedPane.TOP);
        reportTabs.setFont(new Font("Arial", Font.BOLD, 13));
        reportTabs.setBounds(leftX, y, 1330, 580);
        formPanel.add(reportTabs);

        formPanel.setPreferredSize(new Dimension(1380, y + 400));

        // ---------- EXECUTIVE SUMMARY (NON-TECHNICAL) ----------
        JTextArea executiveSummaryArea = new JTextArea();
        executiveSummaryArea.setEditable(false);
        executiveSummaryArea.setLineWrap(true);
        executiveSummaryArea.setWrapStyleWord(true);
        executiveSummaryArea.setFont(new Font("Arial", Font.PLAIN, 13));
        executiveSummaryArea.setBackground(Color.WHITE);
        executiveSummaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        executiveSummaryArea.setText(
            "AOMA-HERITAGE MONITOR | SAFETY ASSESSMENT REPORT\n\n" +
            "Preliminary Automated Result (Unverified)\n\n" +
            "Verification Status:  PENDING ENGINEER\n" +
            "Checked By:  [---]\n\n" +
            "1. OVERALL VERDICT\n\n" +
            "STATUS: SAFE / SERVICEABLE\n" +
            "VERIFICATION: NOT YET REVIEWED BY ENGINEER\n" +
            "ACTION: NO IMMEDIATE INTERVENTION REQUIRED\n\n" +
            "The Automated OMA System has completed a routine structural health scan. " +
            "The building's vibrational response is stable.\n\n" +
            "4. RECOMMENDATIONS\n\n" +
            "Continue automated monitoring. Next physical inspection is recommended " +
            "only if Risk Level rises to \"WARNING\"."
        );

        JScrollPane executiveScroll = new JScrollPane(executiveSummaryArea);
        executiveScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        reportTabs.addTab("Executive Summary (Non-Technical)", executiveScroll);

        // ---------- FULL TECHNICAL AUDIT (STANDARD) ----------
        JTextArea technicalAuditArea = new JTextArea();
        technicalAuditArea.setEditable(false);
        technicalAuditArea.setLineWrap(true);
        technicalAuditArea.setWrapStyleWord(true);
        technicalAuditArea.setFont(new Font("Arial", Font.PLAIN, 13));
        technicalAuditArea.setBackground(Color.WHITE);
        technicalAuditArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        technicalAuditArea.setText(
            "FULL TECHNICAL AUDIT (STANDARD)\n\n" +
            "DATASET ID: #20260224-OMA-005\n" +
            "DATE: Feb 24, 2026 | 14:00 PM\n\n" +
            "2. COMPLIANCE CHECK\n\n" +
            "NSCP Section 405 (Drift Limits): COMPLIANT\n" +
            "P.D. 1096 (Structural Safety): PASSED\n\n" +
            "3. KEY FINDINGS\n\n" +
            "- No abnormal frequency shifts detected (>5% deviation).\n" +
            "- Ambient vibration levels are within normal limits.\n" +
            "- All wireless sensors are synchronized and active.\n\n" +
            "This audit is generated automatically and is subject to " +
            "formal engineer verification."
        );

        JScrollPane technicalScroll = new JScrollPane(technicalAuditArea);
        technicalScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        reportTabs.addTab("Full Technical Audit (Standard)", technicalScroll);



        











        //FOOTER
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setPreferredSize(new Dimension(1400, 45));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.GRAY));

        JLabel statusLbl = new JLabel("Status: ESP32 Hub Not Connected");
        statusLbl.setFont(new Font("Arial", Font.BOLD, 14));
        statusLbl.setForeground(Color.RED);
        statusLbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        footerPanel.add(statusLbl, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(layeredPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HeadViewDetails::new);
    }
}