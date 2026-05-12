import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.File;

public class HeadImportSensorData extends JFrame {

    public HeadImportSensorData() {
        setTitle("AOMA-Heritage Monitor - Import Sensor Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);

        tabsUI.addTab("Projects", headPanel);
        tabsUI.addTab("View", new JPanel());
        tabsUI.addTab("Help", new JPanel());

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
            new HeadViewReport();
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

        if (selectedIndex == 1) { // View tab clicked
            tabsUI.setSelectedIndex(0); // go back to Projects

            Rectangle bounds = tabsUI.getBoundsAt(1);
            viewMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }

        if (selectedIndex == 2) { // Help tab clicked
            tabsUI.setSelectedIndex(0); // go back to Projects

            Rectangle bounds = tabsUI.getBoundsAt(2);
            helpMenu.show(
                    tabsUI,
                    bounds.x,
                    bounds.y + bounds.height
            );
        }
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
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        headPanel.add(centerPanel);

        JLabel greetingLabel = new JLabel("Import Sensor Data", JLabel.LEFT);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        greetingLabel.setBorder( BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)), 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        centerPanel.add(greetingLabel, BorderLayout.NORTH);

        // IMPORT PANEL
        JPanel importContentPanel = new JPanel(null);
        importContentPanel.setBackground(Color.WHITE);
        centerPanel.add(importContentPanel, BorderLayout.CENTER);

        JLabel addAFileLabel = new JLabel("Add a file", JLabel.LEFT);
        addAFileLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addAFileLabel.setBounds(150, 10, 200, 30);
        importContentPanel.add(addAFileLabel);

        // Upload Box Panel
        JPanel uploadBox = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                float[] dash = {8f, 8f};
                g2.setColor(new Color(150,150,150));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, dash, 0));
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
            }
        };

        uploadBox.setBounds(150, 40, 1080, 280);
        uploadBox.setBackground(Color.WHITE);
        importContentPanel.add(uploadBox);

        ImageIcon uploadIcon = new ImageIcon("uploadicon.png");
        Image scaledUploadImg = uploadIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        JLabel uploadIconLabel = new JLabel(new ImageIcon(scaledUploadImg));

        uploadIconLabel.setBounds(470, 15, 140, 140);
        uploadBox.add(uploadIconLabel);

        JLabel uploadTitle = new JLabel("Upload your file here", JLabel.CENTER);
        uploadTitle.setFont(new Font("Arial", Font.BOLD, 22));
        uploadTitle.setBounds(340, 130, 400, 30);
        uploadBox.add(uploadTitle);

        JLabel supportedLabel = new JLabel("Files supported: CSV", JLabel.CENTER);
        supportedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        supportedLabel.setBounds(340, 165, 400, 20);
        uploadBox.add(supportedLabel);

        JLabel orLabel = new JLabel("OR", JLabel.CENTER);
        orLabel.setFont(new Font("Arial", Font.BOLD, 14));
        orLabel.setBounds(340, 190, 400, 20);
        uploadBox.add(orLabel);

        JButton browseBtn = new JButton("BROWSE");
        browseBtn.setFont(new Font("Arial", Font.BOLD, 14));
        browseBtn.setBounds(440, 215, 200, 35);
        browseBtn.setFocusPainted(false);
        browseBtn.setBackground(Color.WHITE);
        browseBtn.setBorder(BorderFactory.createLineBorder(new Color(0,102,204),2));
        browseBtn.setForeground(new Color(0,102,204));
        browseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBox.add(browseBtn);

        JLabel maxSizeLabel = new JLabel("Maximum Size: 50MB", JLabel.CENTER);
        maxSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        maxSizeLabel.setBounds(340, 255, 400, 20);
        uploadBox.add(maxSizeLabel);

        JPanel selectedFilePanel = new JPanel(null);
        selectedFilePanel.setBounds(150, 340, 1080, 70);
        selectedFilePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        selectedFilePanel.setBackground(new Color(245,245,245));
        selectedFilePanel.setVisible(false);
        importContentPanel.add(selectedFilePanel);

        JLabel fileNameLabel = new JLabel();
        fileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fileNameLabel.setBounds(60, 10, 600, 20);
        selectedFilePanel.add(fileNameLabel);

        JLabel fileSizeLabel = new JLabel();
        fileSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fileSizeLabel.setBounds(60, 30, 600, 20);
        selectedFilePanel.add(fileSizeLabel);

        JButton deleteBtn = new JButton("🗑");
        deleteBtn.setBounds(1030, 20, 30, 30);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        selectedFilePanel.add(deleteBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setBounds(550, 430, 120, 35);
        cancelBtn.setForeground(Color.RED);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        importContentPanel.add(cancelBtn);

        JButton uploadBtn = new JButton("Upload CSV File");
        uploadBtn.setFont(new Font("Arial", Font.BOLD, 14));
        uploadBtn.setBounds(680, 430, 130, 35);
        uploadBtn.setForeground(new Color(0, 153, 0)); 
        uploadBtn.setFocusPainted(false);
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        importContentPanel.add(uploadBtn);

        JLabel inlineMsg = new JLabel("", JLabel.CENTER);
        inlineMsg.setFont(new Font("Arial", Font.PLAIN, 12));
        inlineMsg.setBounds(150, 470, 1080, 20);
        inlineMsg.setForeground(Color.GRAY);
        importContentPanel.add(inlineMsg);

        final File[] selectedFile = {null};
        browseBtn.addActionListener(e -> {
            inlineMsg.setForeground(Color.GRAY);
            inlineMsg.setText("Opening file picker...");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            browseBtn.setEnabled(false);
            uploadBtn.setEnabled(false);

            File file;
            try {
                file = NativeFilePicker.pickCsvFile(this, "Select CSV File");
            } finally {
                setCursor(Cursor.getDefaultCursor());
                browseBtn.setEnabled(true);
                uploadBtn.setEnabled(true);
            }

            if (file == null) {
                inlineMsg.setForeground(Color.GRAY);
                inlineMsg.setText("No file selected.");
                Toast.show(this, "Selection canceled", new Color(80, 80, 80), 1600);
                return;
            }

            CsvFileValidator.ValidationResult vr = CsvFileValidator.validate(file);
            if (!vr.valid()) {
                inlineMsg.setForeground(Color.RED);
                inlineMsg.setText(vr.message());
                selectedFile[0] = null;
                selectedFilePanel.setVisible(false);
                Toast.show(this, vr.message(), new Color(160, 40, 40), 2200);
                return;
            }

            selectedFile[0] = file;
            long fileSizeInMB = file.length() / (1024 * 1024);
            fileNameLabel.setText(file.getName());
            fileSizeLabel.setText(fileSizeInMB + " MB");
            selectedFilePanel.setVisible(true);
            inlineMsg.setForeground(new Color(0, 128, 0));
            inlineMsg.setText("CSV selected successfully.");
            Toast.show(this, "CSV selected", new Color(0, 128, 0), 1600);
        });

        deleteBtn.addActionListener(e -> {
            selectedFile[0] = null;
            selectedFilePanel.setVisible(false);
            inlineMsg.setForeground(Color.GRAY);
            inlineMsg.setText("");
        });

        cancelBtn.addActionListener(e -> {
            selectedFile[0] = null;
            selectedFilePanel.setVisible(false);
            inlineMsg.setForeground(Color.GRAY);
            inlineMsg.setText("");
        });

        uploadBtn.addActionListener(e -> {
            if (selectedFile[0] == null) {
                inlineMsg.setForeground(Color.RED);
                inlineMsg.setText("Please select a CSV file first.");
                Toast.show(this, "No file selected", new Color(160, 40, 40), 2000);
                return;
            }

            Object[] options = {
        "Proceed to Analysis",
        "Go to Dashboard"
        };

        int choice = JOptionPane.showOptionDialog(
                this,
                "CSV File Uploaded Successfully!\nThe raw vibration data has been stored. You can now proceed to the OMA Analysis panel to extract the structural health parameters.",
                "Upload Complete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // Proceed to Analysis
            AppSession.setLastUploadedCsv(selectedFile[0]);
            new HeadOMAAnalysisResult(); 
            this.dispose();
        }
        else if (choice == 1) {
            // Go to Dashboard
            new HeadBldgStatusOverview();
            this.dispose();
        }
        });


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
        new HeadImportSensorData();
    }
}
