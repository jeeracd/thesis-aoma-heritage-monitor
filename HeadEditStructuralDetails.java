import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class HeadEditStructuralDetails extends JFrame {

    public HeadEditStructuralDetails() {
        setTitle("AOMA-Heritage Monitor - Edit Structural Details");
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

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);

        headPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        headPanel.add(centerPanel);

        JLabel headerLabel = new JLabel("New Project");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        centerPanel.add(headerLabel, BorderLayout.NORTH);

        // FORM PANEL - ADD DATABASE HERE kasi FORM TYPE SHA!!
        JPanel formPanel = new JPanel(null);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Border fieldBorder = BorderFactory.createLineBorder(Color.BLACK);

        int leftX = 20;
        int rightX = 700;
        int y = 20;
        int gap = 55;

        JLabel projectNameLbl = new JLabel("Project Name");
        projectNameLbl.setFont(labelFont);
        projectNameLbl.setBounds(leftX, y, 200, 25);
        formPanel.add(projectNameLbl);

        //input fiedl
        JTextField projectName = new JTextField("");
        projectName.setFont(fieldFont);
        projectName.setBorder(fieldBorder);
        projectName.setBounds(leftX, y + 30, 1330, 35);
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

        //input fiedl
        JTextField buildingField = new JTextField("");
        buildingField.setFont(fieldFont);
        buildingField.setBorder(fieldBorder);
        buildingField.setBounds(leftX + 160, y, 500, 30);
        formPanel.add(buildingField);

        JLabel materialsLbl = new JLabel("Materials Used:");
        materialsLbl.setFont(labelFont);
        materialsLbl.setBounds(rightX, y, 150, 25);
        formPanel.add(materialsLbl);

        //input fiedl
        JTextField materialsField = new JTextField("");
        materialsField.setFont(fieldFont);
        materialsField.setBorder(fieldBorder);
        materialsField.setBounds(rightX + 150, y, 500, 30);
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
        datePanel.add(dateField, BorderLayout.CENTER);

        ImageIcon calendarIcon = new ImageIcon(
                new ImageIcon("calendaricon.png")
                        .getImage()
                        .getScaledInstance(18, 18, Image.SCALE_SMOOTH)
        );

        JButton calendarBtn = new JButton(calendarIcon);
        calendarBtn.setFocusPainted(false);
        calendarBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        calendarBtn.setContentAreaFilled(false);
        calendarBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calendarBtn.setPreferredSize(new Dimension(36, 30));
        calendarBtn.setToolTipText("Select date");

        calendarBtn.addActionListener(e -> {
        JDialog calendarDialog = new JDialog(this, "Select Date", true);
        calendarDialog.setSize(340, 320);
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        String[] months = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
        };

        JComboBox<String> monthBox = new JComboBox<>(months);
        monthBox.setFont(new Font("Arial", Font.PLAIN, 12));

        java.time.LocalDate now = java.time.LocalDate.now();
        monthBox.setSelectedIndex(now.getMonthValue() - 1);

        JSpinner yearSpinner = new JSpinner(
            new SpinnerNumberModel(now.getYear(), 1500, 2100, 1)
        );
        yearSpinner.setFont(new Font("Arial", Font.PLAIN, 12));

        topPanel.add(monthBox);
        topPanel.add(yearSpinner);

        calendarDialog.add(topPanel, BorderLayout.NORTH);

        JPanel calendarPanel = new JPanel();
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Runnable buildCalendar = () -> {
            calendarPanel.removeAll();
            calendarPanel.setLayout(new GridLayout(0, 7, 5, 5));

            String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
            for (String d : days) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("Arial", Font.BOLD, 12));
                calendarPanel.add(lbl);
            }

            int year = (int) yearSpinner.getValue();
            int month = monthBox.getSelectedIndex() + 1;

            java.time.LocalDate firstDay =
                    java.time.LocalDate.of(year, month, 1);

            int startDay = firstDay.getDayOfWeek().getValue() % 7;
            int daysInMonth = firstDay.lengthOfMonth();

            for (int i = 0; i < startDay; i++) {
                calendarPanel.add(new JLabel(""));
            }

            for (int day = 1; day <= daysInMonth; day++) {
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setFocusPainted(false);

                int selectedDay = day;
                dayBtn.addActionListener(ev -> {
                    dateField.setText(
                        String.format("%04d-%02d-%02d",
                            year, month, selectedDay)
                    );
                    calendarDialog.dispose();
                });

                calendarPanel.add(dayBtn);
            }

            calendarPanel.revalidate();
            calendarPanel.repaint();
        };

        buildCalendar.run();

        monthBox.addActionListener(ev -> buildCalendar.run());
        yearSpinner.addChangeListener(ev -> buildCalendar.run());

        calendarDialog.add(calendarPanel, BorderLayout.CENTER);
        calendarDialog.setVisible(true);
    });

        datePanel.add(calendarBtn, BorderLayout.EAST);
        formPanel.add(datePanel);


        JLabel functionLbl = new JLabel("Function:");
        functionLbl.setFont(labelFont);
        functionLbl.setBounds(rightX, y, 150, 25);
        formPanel.add(functionLbl);

        //input fiedl
        JTextField functionField = new JTextField("");
        functionField.setFont(fieldFont);
        functionField.setBorder(fieldBorder);
        functionField.setBounds(rightX + 150, y, 500, 30);
        formPanel.add(functionField);
        y += gap;

        JLabel conservationLbl = new JLabel("Conservation Status:");
        conservationLbl.setFont(labelFont);
        conservationLbl.setBounds(leftX, y, 170, 25);
        formPanel.add(conservationLbl);

        //input fiedl
        JTextField conservationField = new JTextField("");
        conservationField.setFont(fieldFont);
        conservationField.setBorder(fieldBorder);
        conservationField.setBounds(leftX + 160, y, 300, 30);
        formPanel.add(conservationField);

        y += gap;

        JLabel addressLbl = new JLabel("Address:");
        addressLbl.setFont(labelFont);
        addressLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(addressLbl);

        //input fiedl
        JTextField addressField = new JTextField("");
        addressField.setFont(fieldFont);
        addressField.setBorder(fieldBorder);
        addressField.setBounds(leftX + 160, y, 1170, 30);
        formPanel.add(addressField);

        y += gap;

        JLabel descLbl = new JLabel("Description:");
        descLbl.setFont(labelFont);
        descLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(descLbl);

        //input fiedl
        JTextArea descArea = new JTextArea("");
        descArea.setFont(fieldFont);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(fieldBorder);

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBounds(leftX + 160, y, 1170, 90);
        formPanel.add(descScroll);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setForeground(Color.RED);     
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.setBounds(520, 520, 140, 35);

        cancelBtn.addActionListener(e -> {
        Confirmation exitConfirm = new Confirmation(
                this, 
                "Confirm Exit", 
                "This window is asking you to confirm that you want to leave - data you have entered may not be save", 
                "Exit", 
                "Stay on Window", 
                Color.RED, 
                new Color(0, 153, 0)
            );
            exitConfirm.setVisible(true);

            if (exitConfirm.isConfirmActionTaken()) {
                dispose(); 
            }
        });

        formPanel.add(cancelBtn);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setForeground(new Color(0, 153, 0)); 
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setBounds(680, 520, 220, 35);
        formPanel.add(submitBtn);

        submitBtn.addActionListener(e -> {

        String buildingName = buildingField.getText().isEmpty()
                ? "New Heritage Building " + HeadBldgStatusOverview.projectCount
                : buildingField.getText();

        String location = addressField.getText().isEmpty()
                ? "Location Not Set"
                : addressField.getText();

        String function = functionField.getText().isEmpty()
                ? "Not Specified"
                : functionField.getText();

        String healthStatus = "NO DATA";

        HeadBldgStatusOverview.projectCount++;

        JOptionPane.showMessageDialog(
                this,
                "Project added to Dashboard!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

        SwingUtilities.invokeLater(() -> {
            HeadBldgStatusOverview dashboard = new HeadBldgStatusOverview();
            dashboard.setVisible(true);

            HeadBldgStatusOverview.addNewProjectRow(
                    buildingName,
                    location,
                    function,
                    healthStatus
            );
        });
    });

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
        new HeadEditStructuralDetails();
    }
}