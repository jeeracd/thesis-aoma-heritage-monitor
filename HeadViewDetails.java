import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.UUID;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HeadViewDetails extends JFrame {
    private final UUID projectId;

    public HeadViewDetails() {
        this(null);
    }

    public HeadViewDetails(UUID projectId) {
        this.projectId = projectId;
        setTitle("AOMA-Heritage Monitor - View Details");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1225, 600);
        setSize(1225, 600);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x_view = (screenSize.width - getWidth()) / 2;
        int y_view = (screenSize.height - getHeight()) / 2 - 400; //the use of this is to adjust the hright of the window
        setLocation(x_view, y_view);

        Project project = null;
        if (projectId != null) {
            project = ProjectRepository.findById(projectId).orElse(null);
            if (project == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Project not found. The data may have been removed or failed to load.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        ProjectDetailsController controller = project == null ? null : new ProjectDetailsController(project);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", headPanel);
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
        newProject.addActionListener(e -> {
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmExitIfDirty(controller)) {
                return;
            }
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
        layeredPane.setPreferredSize(new java.awt.Dimension(1300, 1500)); //responsible for the size of the layered pane
        layeredPane.add(projectsDropdownBtn, JLayeredPane.PALETTE_LAYER); 
        tabsUI.setBounds(0, 0, 1395, 1500); //responsible for the size of the tabbed pane adjust lang to if magdagdag ng mga bagong jpanel sa tabbed pane
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

        JLabel LGUHeadLabel = new JLabel("LGU HEAD ACCOUNT");
        LGUHeadLabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGUHeadLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGUHeadLabel.setBounds(925, 5, 280, 38);

        layeredPane.add(LGUHeadLabel, JLayeredPane.PALETTE_LAYER);

        JPanel centerPanelDescription = new JPanel(new BorderLayout());
        centerPanelDescription.setBounds(10, 20, 1200, 40);
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
        centerPanel.setBounds(10, 70, 1200, 520);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        headPanel.add(centerPanel);

        JLabel headerLabel = new JLabel("View Details");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        centerPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(null);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Border fieldBorder = BorderFactory.createLineBorder(Color.BLACK);

        int leftX = 20;
        int rightX = 700;
        int y = 10;
        int gap = 35; // vertical gap between fields changeable sha

        JLabel projectNameLbl = new JLabel("Project Name");
        projectNameLbl.setFont(labelFont);
        projectNameLbl.setBounds(leftX, y, 200, 25);
        formPanel.add(projectNameLbl);

        //buttons for edit and manage access
        JButton editProjectBtn = new JButton("Edit Project Details");
        editProjectBtn.setFont(new Font("Arial", Font.BOLD, 15));
        editProjectBtn.setFocusPainted(false);
        editProjectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editProjectBtn.setBounds(800, y, 180, 25);
        formPanel.add(editProjectBtn);

        JButton manageAccessBtn = new JButton("Manage Access");
        manageAccessBtn.setFont(new Font("Arial", Font.BOLD, 15));
        manageAccessBtn.setFocusPainted(false);
        manageAccessBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        manageAccessBtn.setBounds(990, y, 160, 25);
        formPanel.add(manageAccessBtn);

        JTextField projectName = new JTextField("");
        projectName.setFont(fieldFont);
        projectName.setBorder(fieldBorder);
        projectName.setBounds(leftX, y + 30, 1130, 35);
        projectName.setEnabled(false);
        formPanel.add(projectName);
        if (project != null) {
            projectName.setText(project.getProjectName());
        }

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

        JTextField buildingField = new JTextField("");
        buildingField.setFont(fieldFont);
        buildingField.setBorder(fieldBorder);
        buildingField.setBounds(leftX + 160, y, 500, 30);
        buildingField.setEnabled(false);
        formPanel.add(buildingField);
        if (project != null) {
            buildingField.setText(project.getBuildingName());
        }

        JLabel materialsLbl = new JLabel("Materials Used:");
        materialsLbl.setFont(labelFont);
        materialsLbl.setBounds(rightX, y, 150, 25);
        formPanel.add(materialsLbl);

        JTextField materialsField = new JTextField("");
        materialsField.setFont(fieldFont);
        materialsField.setBorder(fieldBorder);
        materialsField.setBounds(rightX + 150, y, 300, 30);
        materialsField.setEnabled(false);
        formPanel.add(materialsField);
        if (project != null) {
            materialsField.setText(project.getMaterialsUsed());
        }

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
        dateField.setEnabled(false);
        datePanel.add(dateField, BorderLayout.CENTER);
        if (project != null) {
            dateField.setText(project.getDateConstructed());
        }

        ImageIcon calendarIcon = new ImageIcon(
                new ImageIcon("calendaricon.png")
                        .getImage()
                        .getScaledInstance(18, 18, Image.SCALE_SMOOTH)
        );

        JButton calendarBtn = new JButton(calendarIcon);
        calendarBtn.setEnabled(false);

        calendarBtn.setFocusPainted(false);
        calendarBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        calendarBtn.setContentAreaFilled(false);
        calendarBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calendarBtn.setPreferredSize(new Dimension(36, 30));
        calendarBtn.setToolTipText("Select date");

        datePanel.add(calendarBtn, BorderLayout.EAST);
        formPanel.add(datePanel);

        JLabel functionLbl = new JLabel("Function:");
        functionLbl.setFont(labelFont);
        functionLbl.setBounds(rightX, y, 150, 25);
        formPanel.add(functionLbl);

        JTextField functionField = new JTextField("");
        functionField.setFont(fieldFont);
        functionField.setBorder(fieldBorder);
        functionField.setBounds(rightX + 150, y, 300, 30);
        functionField.setEnabled(false);
        formPanel.add(functionField);
        if (project != null) {
            functionField.setText(project.getFunction());
        }

        y += gap;

        JLabel conservationLbl = new JLabel("Conservation Status:");
        conservationLbl.setFont(labelFont);
        conservationLbl.setBounds(leftX, y, 170, 25);
        formPanel.add(conservationLbl);

        JTextField conservationField = new JTextField("");
        conservationField.setFont(fieldFont);
        conservationField.setBorder(fieldBorder);
        conservationField.setBounds(leftX + 160, y, 300, 30);
        conservationField.setEnabled(false);
        formPanel.add(conservationField);
        if (project != null) {
            conservationField.setText(project.getConservationStatus());
        }

        y += gap;

        JLabel addressLbl = new JLabel("Address:");
        addressLbl.setFont(labelFont);
        addressLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(addressLbl);

        JTextField addressField = new JTextField("");
        addressField.setFont(fieldFont);
        addressField.setBorder(fieldBorder);
        addressField.setBounds(leftX + 160, y, 970, 30);
        addressField.setEnabled(false);
        formPanel.add(addressField);
        if (project != null) {
            addressField.setText(project.getAddress());
        }

        y += gap;

        JLabel descLbl = new JLabel("Description:");
        descLbl.setFont(labelFont);
        descLbl.setBounds(leftX, y, 150, 25);
        formPanel.add(descLbl);

        JTextArea descArea = new JTextArea("");
        descArea.setFont(fieldFont);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(fieldBorder);
        descArea.setEnabled(false);
        if (project != null) {
            descArea.setText(project.getDescription());
        }
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBounds(leftX + 160, y, 970, 90);
        formPanel.add(descScroll);
        y += 100;

        if (controller == null) {
            editProjectBtn.setEnabled(false);
        }

        Runnable applyControllerToUI = () -> {
            if (controller == null) {
                return;
            }
            Project p = controller.isEditing() ? controller.getDraft() : controller.getBaseline();
            projectName.setText(p.getProjectName());
            buildingField.setText(p.getBuildingName());
            dateField.setText(p.getDateConstructed());
            materialsField.setText(p.getMaterialsUsed());
            functionField.setText(p.getFunction());
            conservationField.setText(p.getConservationStatus());
            addressField.setText(p.getAddress());
            descArea.setText(p.getDescription());
        };

        Runnable setEditableState = () -> {
            boolean enabled = controller != null && controller.isEditing();
            projectName.setEnabled(enabled);
            buildingField.setEnabled(enabled);
            dateField.setEnabled(enabled);
            materialsField.setEnabled(enabled);
            functionField.setEnabled(enabled);
            conservationField.setEnabled(enabled);
            addressField.setEnabled(enabled);
            descArea.setEnabled(enabled);
            calendarBtn.setEnabled(enabled);

            editProjectBtn.setText(enabled ? "Save Changes" : "Edit Project Details");
            manageAccessBtn.setText(enabled ? "Cancel" : "Manage Access");
            manageAccessBtn.setForeground(enabled ? Color.RED : Color.BLACK);
        };

        DocumentListener dirtyListener = new DocumentListener() {
            private void mark() {
                if (controller == null || !controller.isEditing()) {
                    return;
                }
                controller.setDraftDetails(
                        projectName.getText(),
                        buildingField.getText(),
                        dateField.getText(),
                        materialsField.getText(),
                        functionField.getText(),
                        conservationField.getText(),
                        addressField.getText(),
                        descArea.getText()
                );
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                mark();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mark();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mark();
            }
        };

        projectName.getDocument().addDocumentListener(dirtyListener);
        buildingField.getDocument().addDocumentListener(dirtyListener);
        dateField.getDocument().addDocumentListener(dirtyListener);
        materialsField.getDocument().addDocumentListener(dirtyListener);
        functionField.getDocument().addDocumentListener(dirtyListener);
        conservationField.getDocument().addDocumentListener(dirtyListener);
        addressField.getDocument().addDocumentListener(dirtyListener);
        descArea.getDocument().addDocumentListener(dirtyListener);

        calendarBtn.addActionListener(e -> {
            if (controller == null || !controller.isEditing()) {
                return;
            }
            showCalendarDialog(this, dateField);
        });

        editProjectBtn.addActionListener(e -> {
            if (controller == null) {
                return;
            }
            if (!controller.isEditing()) {
                controller.enterEditMode();
                setEditableState.run();
                return;
            }

            controller.setDraftDetails(
                    projectName.getText(),
                    buildingField.getText(),
                    ProjectValidation.normalizeDateOrReturnOriginal(dateField.getText()),
                    materialsField.getText(),
                    functionField.getText(),
                    conservationField.getText(),
                    addressField.getText(),
                    descArea.getText()
            );
            dateField.setText(ProjectValidation.normalizeDateOrReturnOriginal(dateField.getText()));
            dateField.setText(ProjectValidation.normalizeDateOrReturnOriginal(dateField.getText()));

            try {
                controller.validateDraft();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JDialog savingDialog = createSavingDialog(this);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            editProjectBtn.setEnabled(false);
            manageAccessBtn.setEnabled(false);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Project updated = controller.getBaseline().withUpdatedDetails(
                            controller.getDraft().getProjectName(),
                            controller.getDraft().getBuildingName(),
                            controller.getDraft().getDateConstructed(),
                            controller.getDraft().getMaterialsUsed(),
                            controller.getDraft().getFunction(),
                            controller.getDraft().getConservationStatus(),
                            controller.getDraft().getAddress(),
                            controller.getDraft().getDescription()
                    );
                    ProjectRepository.updateProject(updated);
                    return null;
                }

                @Override
                protected void done() {
                    savingDialog.dispose();
                    setCursor(Cursor.getDefaultCursor());
                    editProjectBtn.setEnabled(true);
                    manageAccessBtn.setEnabled(true);
                    try {
                        get();
                        Project persisted = ProjectRepository.findById(controller.getBaseline().getId()).orElse(controller.getDraft());
                        controller.applySaveSuccess(persisted);
                        applyControllerToUI.run();
                        setEditableState.run();
                        JOptionPane.showMessageDialog(
                                HeadViewDetails.this,
                                "Project details saved successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } catch (Exception ex) {
                        controller.applySaveFailureRevert();
                        applyControllerToUI.run();
                        setEditableState.run();
                        JOptionPane.showMessageDialog(
                                HeadViewDetails.this,
                                "Failed to save changes. Reverted to last saved values.",
                                "Save Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };
            worker.execute();
            savingDialog.setVisible(true);
        });

        manageAccessBtn.addActionListener(e -> {
            if (controller != null && controller.isEditing()) {
                if (controller.isDirty()) {
                    int c = JOptionPane.showConfirmDialog(
                            this,
                            "You have unsaved changes. Discard them?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (c != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                controller.cancelEdits();
                applyControllerToUI.run();
                setEditableState.run();
                return;
            }

            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to Manage Access page.",
                    "Manage Access",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new HeadDashboardManageAccess();
            this.dispose();
        });

        applyControllerToUI.run();
        setEditableState.run();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!confirmNavigateIfDirty(controller)) {
                    return;
                }
                dispose();
            }
        });

        JLabel monitoringSessionLbl = new JLabel("Select Monitoring Session:");
        monitoringSessionLbl.setFont(labelFont);
        monitoringSessionLbl.setBounds(leftX, y + gap, 200, 25);
        formPanel.add(monitoringSessionLbl);

        JComboBox<String> sessionDropdown = new JComboBox<>(new String[]{
            "---none---", 
            "#20260224-OMA-005"
        });
        sessionDropdown.setFont(fieldFont);
        sessionDropdown.setBounds(leftX + 210, y + gap, 200, 30);
        sessionDropdown.setEnabled(true);
        formPanel.add(sessionDropdown);

        sessionDropdown.addActionListener(e -> {
            String selected = (String) sessionDropdown.getSelectedItem();

            if (selected.equals("---none---")) {
                setSize(1225, 600);
            } else {
                setSize(1225, 1500);
            }
        });

        JLabel reportTypeLbl = new JLabel("Select Report Type:");
        reportTypeLbl.setFont(labelFont);
        reportTypeLbl.setBounds(leftX + 215, y + gap - 25, 200, 25);
        formPanel.add(reportTypeLbl);

        // EXECUTIVE SUMMARY (NON TECHNICAL) & FULL TECHNICAL AUDIT (STANDARD)
        int reportTopY = centerPanel.getY() + centerPanel.getHeight() + 30;
        JPanel reportTypePanel = new JPanel(null);
        reportTypePanel.setBounds(10, reportTopY, 1200, 50);
        reportTypePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        reportTypePanel.setBackground(new Color(230,230,230));

        JButton executiveSummaryBtn = new JButton("Executive Summary (Non-Technical)");
        executiveSummaryBtn.setFont(new Font("Arial", Font.BOLD, 14));
        executiveSummaryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        executiveSummaryBtn.setFocusPainted(false);
        executiveSummaryBtn.setBackground(new Color(132,166,210)); // active
        executiveSummaryBtn.setOpaque(true);
        executiveSummaryBtn.setBounds(2, 2, 598, 46);

        JButton fullAuditBtn = new JButton("Full Technical Audit (Standard)");
        fullAuditBtn.setFont(new Font("Arial", Font.BOLD, 14));
        fullAuditBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fullAuditBtn.setFocusPainted(false);
        fullAuditBtn.setBackground(Color.WHITE); // inactive
        fullAuditBtn.setOpaque(true);
        fullAuditBtn.setBounds(600, 2, 598, 46);

        reportTypePanel.add(executiveSummaryBtn);
        reportTypePanel.add(fullAuditBtn);
        headPanel.add(reportTypePanel);

        // SAFETY ASSESSMENT REPORT PANEL -- DATABASE TO AH
        JPanel reportPanel = new JPanel(null);
        reportPanel.setBounds(10, reportTopY + 60, 1200, 800);
        reportPanel.setPreferredSize(new Dimension(1200, 1000)); // purpose nito is para maadjust yung scrollbar pababa
        reportPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        reportPanel.setBackground(new Color(245,245,245));

        Font reportTitleFont = new Font("Arial", Font.BOLD, 18);
        Font sectionFont = new Font("Arial", Font.BOLD, 14);
        Font normalFont = new Font("Arial", Font.PLAIN, 14);

        // REPORT TITLE
        JLabel reportExecTitle = new JLabel("AOMA-HERITAGE MONITOR | SAFETY ASSESSMENT REPORT", JLabel.CENTER);
        reportExecTitle.setFont(reportTitleFont);
        reportExecTitle.setBounds(0, 10, 1200, 30);
        reportPanel.add(reportExecTitle);

        // TOP LINE
        JPanel lineExec1 = new JPanel();
        lineExec1.setBackground(Color.BLACK);
        lineExec1.setBounds(20, 50, 1160, 2);
        reportPanel.add(lineExec1);

        // LEFT INFO
        JLabel preliminaryExecLbl = new JLabel("Preliminary Automated Result (Unverified)");
        preliminaryExecLbl.setFont(sectionFont);
        preliminaryExecLbl.setBounds(40, 70, 400, 25);
        reportPanel.add(preliminaryExecLbl);

        JLabel verificationExecLbl = new JLabel("Verification Status: ");
        verificationExecLbl.setFont(normalFont);
        verificationExecLbl.setBounds(40, 100, 160, 25);
        reportPanel.add(verificationExecLbl);

        JLabel approveExecLbl = new JLabel("● APPROVE");
        approveExecLbl.setFont(normalFont);
        approveExecLbl.setForeground(new Color(0,150,0));
        approveExecLbl.setBounds(200, 100, 150, 25);
        reportPanel.add(approveExecLbl);

        JLabel checkedExecLbl = new JLabel("Checked By: STRUCTURAL ENGINEER [02-24-2026 16:37]");
        checkedExecLbl.setFont(normalFont);
        checkedExecLbl.setBounds(40, 130, 500, 25);
        reportPanel.add(checkedExecLbl);

        // RIGHT INFO
        JLabel dateAssessmentExecLbl = new JLabel("DATE: | AM/PM");
        dateAssessmentExecLbl.setFont(normalFont);
        dateAssessmentExecLbl.setBounds(750, 70, 350, 25);
        reportPanel.add(dateAssessmentExecLbl);

        JLabel datasetExecLbl = new JLabel("DATASET ID: #20260224-OMA-005");
        datasetExecLbl.setFont(normalFont);
        datasetExecLbl.setBounds(750, 100, 350, 25);
        reportPanel.add(datasetExecLbl);

        JLabel verdictExecTitle = new JLabel("1. OVERALL VERDICT");
        verdictExecTitle.setFont(sectionFont);
        verdictExecTitle.setBounds(40, 180, 300, 25);
        reportPanel.add(verdictExecTitle);

        JPanel lineExec2 = new JPanel();
        lineExec2.setBackground(Color.BLACK);
        lineExec2.setBounds(20, 210, 1160, 2);
        reportPanel.add(lineExec2);

        //SAFE / SERVICEABLE / UNSAFE
        JLabel statusExecLbl2 = new JLabel("STATUS:");
        statusExecLbl2.setFont(normalFont);
        statusExecLbl2.setBounds(40, 230, 400, 25);
        reportPanel.add(statusExecLbl2);

        JLabel verifyExecLbl2 = new JLabel("VERIFICATION: VERIFIED BY ENGR. STRUCTURAL ENGINEER ACCOUNT");
        verifyExecLbl2.setFont(normalFont);
        verifyExecLbl2.setBounds(40, 260, 600, 25);
        reportPanel.add(verifyExecLbl2);

        JLabel actionExecLbl = new JLabel("ACTION:");
        actionExecLbl.setFont(normalFont);
        actionExecLbl.setBounds(40, 290, 600, 25);
        reportPanel.add(actionExecLbl);

        // DESCRIPTION
        JLabel descExecText = new JLabel("The Automated OMA System has completed a routine structural health scan. The building's vibrational response is stable.");
        descExecText.setFont(normalFont);
        descExecText.setBounds(40, 330, 1000, 50);
        reportPanel.add(descExecText);

        // COMPLIANCE
        JLabel complianceExecTitle = new JLabel("2. COMPLIANCE CHECK");
        complianceExecTitle.setFont(sectionFont);
        complianceExecTitle.setBounds(40, 400, 300, 25);
        reportPanel.add(complianceExecTitle);

        JPanel lineExec3 = new JPanel();
        lineExec3.setBackground(Color.BLACK);
        lineExec3.setBounds(20, 430, 1160, 2);
        reportPanel.add(lineExec3);

        // COMPLIANT / NON-COMPLIANT 
        JLabel nscpExecLbl = new JLabel("NSCP Section 405 (Drift Limits): ");
        nscpExecLbl.setFont(normalFont);
        nscpExecLbl.setBounds(40, 450, 400, 25);
        reportPanel.add(nscpExecLbl);

        //PASSED PASSED
        JLabel pdExecLbl = new JLabel("P.D. 1096 (Structural Safety): ");
        pdExecLbl.setFont(normalFont);
        pdExecLbl.setBounds(40, 480, 400, 25);
        reportPanel.add(pdExecLbl);

        // KEY FINDINGS
        JLabel findingsExecTitle = new JLabel("3. KEY FINDINGS");
        findingsExecTitle.setFont(sectionFont);
        findingsExecTitle.setBounds(40, 540, 300, 25);
        reportPanel.add(findingsExecTitle);

        JPanel lineExec4 = new JPanel();
        lineExec4.setBackground(Color.BLACK);
        lineExec4.setBounds(20, 570, 1160, 2);
        reportPanel.add(lineExec4);

        JLabel dotExec1 = new JLabel("• ");
        dotExec1.setFont(normalFont);
        dotExec1.setBounds(60, 600, 600, 25);
        reportPanel.add(dotExec1);

        JLabel dotExec2 = new JLabel("• ");
        dotExec2.setFont(normalFont);
        dotExec2.setBounds(60, 630, 600, 25);
        reportPanel.add(dotExec2);

        JLabel dotExec3 = new JLabel("• ");
        dotExec3.setFont(normalFont);
        dotExec3.setBounds(60, 660, 600, 25);
        reportPanel.add(dotExec3);

        // RECOMMENDATIONS
        JLabel recExecTitle = new JLabel("4. RECOMMENDATIONS");
        recExecTitle.setFont(sectionFont);
        recExecTitle.setBounds(40, 710, 300, 25);
        reportPanel.add(recExecTitle);

        JPanel lineExec5 = new JPanel();
        lineExec5.setBackground(Color.BLACK);
        lineExec5.setBounds(20, 740, 1160, 2);
        reportPanel.add(lineExec5);

        JLabel recoExecText = new JLabel("Continue automated monitoring. Next physical inspection is recommended only if Risk Level rises to \"WARNING\".");
        recoExecText.setFont(normalFont);
        recoExecText.setBounds(40, 760, 1000, 40);
        reportPanel.add(recoExecText);

        JPanel reportContainer = new JPanel(new CardLayout());
        reportContainer.setBounds(10, 540, 1200, 750);
        headPanel.add(reportContainer);

        JScrollPane reportScrollPane = new JScrollPane(reportPanel);
        reportScrollPane.setBounds(10, 540, 1200, 750); //adjust the height to show the report without scrolling
        reportScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        reportScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        reportScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        reportContainer.add(reportScrollPane, "EXEC_SUMMARY");
        
        //TECHNICAL AUDIT LOG  -- DATABASE TO AH
        JPanel technicalAuditPanel = new JPanel(null);
        technicalAuditPanel.setBounds(10, 540, 1200, 800);
        technicalAuditPanel.setPreferredSize(new Dimension(1200, 1400)); // purpose nito is para maadjust yung scrollbar pababa
        technicalAuditPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        technicalAuditPanel.setBackground(new Color(245,245,245));

        JLabel techTitle = new JLabel("AOMA-HERITAGE MONITOR | TECHNICAL AUDIT LOG", JLabel.CENTER);
        techTitle.setFont(new Font("Arial", Font.BOLD, 18));
        techTitle.setBounds(0, 10, 1200, 30);
        technicalAuditPanel.add(techTitle);

        // TOP LINE
        JPanel lineTech1 = new JPanel();
        lineTech1.setBackground(Color.BLACK);
        lineTech1.setBounds(20, 50, 1160, 2);
        technicalAuditPanel.add(lineTech1);

        // LEFT INFO
        JLabel preliminaryTechLbl = new JLabel("Preliminary Automated Result (Unverified)");
        preliminaryTechLbl.setFont(sectionFont);
        preliminaryTechLbl.setBounds(40, 70, 400, 25);
        technicalAuditPanel.add(preliminaryTechLbl);

        JLabel verificationTechLbl = new JLabel("Verification Status: ");
        verificationTechLbl.setFont(normalFont);
        verificationTechLbl.setBounds(40, 100, 160, 25);
        technicalAuditPanel.add(verificationTechLbl);

        JLabel approveTechLbl = new JLabel("● APPROVE");
        approveTechLbl.setFont(normalFont);
        approveTechLbl.setForeground(new Color(0,150,0));
        approveTechLbl.setBounds(200, 100, 150, 25);
        technicalAuditPanel.add(approveTechLbl);

        JLabel checkedTechLbl = new JLabel("Checked By: STRUCTURAL ENGINEER [02-24-2026 16:37]");
        checkedTechLbl.setFont(normalFont);
        checkedTechLbl.setBounds(40, 130, 500, 25);
        technicalAuditPanel.add(checkedTechLbl);

        // RIGHT INFO
        JLabel dateAssessmentTechLbl = new JLabel("DATE: | AM/PM");
        dateAssessmentTechLbl.setFont(normalFont);
        dateAssessmentTechLbl.setBounds(750, 70, 350, 25);
        technicalAuditPanel.add(dateAssessmentTechLbl);

        JLabel datasetTechLbl = new JLabel("DATASET ID: #20260224-OMA-005");
        datasetTechLbl.setFont(normalFont);
        datasetTechLbl.setBounds(750, 100, 350, 25);
        technicalAuditPanel.add(datasetTechLbl);

        JLabel modalParametersTechTitle = new JLabel("1. MODAL PARAMETERS (SSI-COV)");
        modalParametersTechTitle.setFont(sectionFont);
        modalParametersTechTitle.setBounds(40, 180, 300, 25);
        technicalAuditPanel.add(modalParametersTechTitle);

        JPanel lineTech2 = new JPanel();
        lineTech2.setBackground(Color.BLACK);
        lineTech2.setBounds(20, 210, 1160, 2);
        technicalAuditPanel.add(lineTech2);

        String[] modalColumns = {
            "MODE", "FREQ (fn)", "DAMPING (ζ)", "DEVIATION (Baseline)"
        };

        // TABLE DATA
        Object[][] modalData = {
            {"1", " Hz", " %", "% (Stable)"},
            {"2", " Hz", " %", "% (Stable)"},
            {"3", " Hz", " %", "% (Stable)"}
        };

        //TABLE
        JTable modalTable = new JTable(modalData, modalColumns);
        modalTable.setFont(new Font("Arial", Font.PLAIN, 13));
        modalTable.setRowHeight(30);
        modalTable.setEnabled(false); 
        modalTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) modalTable.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        modalTable.setShowGrid(true);
        modalTable.setGridColor(Color.BLACK);
        modalTable.setIntercellSpacing(new Dimension(1, 1));

        modalTable.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < modalTable.getColumnCount(); i++) {
            modalTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane modalTableScroll = new JScrollPane(modalTable);
        modalTableScroll.setBounds(40, 240, 1120, 120);
        modalTableScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        technicalAuditPanel.add(modalTableScroll);

        JLabel reliabilityExecTitle = new JLabel("2. RELIABILITY ASSESSMENT (MAC VALUE)");
        reliabilityExecTitle.setFont(sectionFont);
        reliabilityExecTitle.setBounds(40, 400, 300, 25);
        technicalAuditPanel.add(reliabilityExecTitle);

        JPanel lineTech3 = new JPanel();
        lineTech3.setBackground(Color.BLACK);
        lineTech3.setBounds(20, 430, 1160, 2);
        technicalAuditPanel.add(lineTech3);

        JLabel reliabilityText = new JLabel("Modal Assurance Criterion (MAC) indicates high confidence in the extracted data quality.");
        reliabilityText.setFont(normalFont);
        reliabilityText.setBounds(40, 440, 600, 10);
        technicalAuditPanel.add(reliabilityText);

        JLabel lessThanSymbolTech1 = new JLabel("> Mode 1 MAC: ");
        lessThanSymbolTech1.setFont(normalFont);
        lessThanSymbolTech1.setBounds(40, 450, 400, 30);
        technicalAuditPanel.add(lessThanSymbolTech1);   

        JLabel lessThanSymbolTech2 = new JLabel("> Mode 2 MAC: ");
        lessThanSymbolTech2.setFont(normalFont);
        lessThanSymbolTech2.setBounds(40, 480, 400, 25);
        technicalAuditPanel.add(lessThanSymbolTech2);  
        
        JLabel lessThanSymbolTech3 = new JLabel("> Mode 3 MAC: ");
        lessThanSymbolTech3.setFont(normalFont);
        lessThanSymbolTech3.setBounds(40, 510, 400, 25);
        technicalAuditPanel.add(lessThanSymbolTech3);   

        JLabel systemEfficiencyTitle = new JLabel("3. SYSTEM EFFICIENCY DIAGNOSTICS");
        systemEfficiencyTitle.setFont(sectionFont);
        systemEfficiencyTitle.setBounds(40, 540, 300, 25);
        technicalAuditPanel.add(systemEfficiencyTitle);

        JPanel lineTech4 = new JPanel();
        lineTech4.setBackground(Color.BLACK);
        lineTech4.setBounds(20, 570, 1160, 2);
        technicalAuditPanel.add(lineTech4);

        JLabel dotTech1 = new JLabel("• Computational Run Time: ");
        dotTech1.setFont(normalFont);
        dotTech1.setBounds(60, 600, 600, 25);
        technicalAuditPanel.add(dotTech1);

        JLabel dotTech2 = new JLabel("• Memory Usage: ");
        dotTech2.setFont(normalFont);
        dotTech2.setBounds(60, 630, 600, 25);
        technicalAuditPanel.add(dotTech2);

        JLabel dotTech3 = new JLabel("• Time Sync Offset: ");
        dotTech3.setFont(normalFont);
        dotTech3.setBounds(60, 660, 600, 25);
        technicalAuditPanel.add(dotTech3);

        JLabel spectralDataTitle = new JLabel("4. SPECTRAL DATA");
        spectralDataTitle.setFont(sectionFont);
        spectralDataTitle.setBounds(40, 710, 300, 25);
        technicalAuditPanel.add(spectralDataTitle);

        JPanel lineTech5 = new JPanel();
        lineTech5.setBackground(Color.BLACK);
        lineTech5.setBounds(20, 740, 1160, 2);
        technicalAuditPanel.add(lineTech5);

        JLabel spectralDataText = new JLabel("put data spectrogram image here ahh");
        spectralDataText.setFont(normalFont);
        //adjust the height to show the report without scrolling
        spectralDataText.setBounds(40, 760, 600, 25);
        technicalAuditPanel.add(spectralDataText);

        JScrollPane technicalScroll = new JScrollPane(technicalAuditPanel);
        technicalScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        technicalScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        reportContainer.add(technicalScroll, "TECH_AUDIT");

        CardLayout cardLayoutAuditTech = (CardLayout) reportContainer.getLayout();

        executiveSummaryBtn.addActionListener(e -> {
        cardLayoutAuditTech.show(reportContainer, "EXEC_SUMMARY");
        executiveSummaryBtn.setBackground(new Color(132,166,210)); 
        fullAuditBtn.setBackground(Color.WHITE); 
        });

        fullAuditBtn.addActionListener(e -> {
        cardLayoutAuditTech.show(reportContainer, "TECH_AUDIT");

        fullAuditBtn.setBackground(new Color(132,166,210)); 
        executiveSummaryBtn.setBackground(Color.WHITE); 
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

    private static boolean confirmNavigateIfDirty(ProjectDetailsController controller) {
        if (controller != null && controller.isEditing() && controller.isDirty()) {
            int c = JOptionPane.showConfirmDialog(
                    null,
                    "You have unsaved changes. Discard them and continue?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION
            );
            return c == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private static boolean confirmExitIfDirty(ProjectDetailsController controller) {
        if (controller != null && controller.isEditing() && controller.isDirty()) {
            int c = JOptionPane.showConfirmDialog(
                    null,
                    "You have unsaved changes. Discard them and exit?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION
            );
            return c == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private static JDialog createSavingDialog(JFrame owner) {
        JDialog dialog = new JDialog(owner, "Saving", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(300, 120);
        dialog.setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Saving changes...");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        panel.add(bar, BorderLayout.CENTER);

        dialog.setContentPane(panel);
        return dialog;
    }

    private static void showCalendarDialog(JFrame owner, JTextField dateField) {
        JDialog calendarDialog = new JDialog(owner, "Select Date", true);
        calendarDialog.setSize(340, 320);
        calendarDialog.setLocationRelativeTo(owner);
        calendarDialog.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
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

            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String d : days) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("Arial", Font.BOLD, 12));
                calendarPanel.add(lbl);
            }

            int year = (int) yearSpinner.getValue();
            int month = monthBox.getSelectedIndex() + 1;

            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);

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
                    dateField.setText(String.format("%04d-%02d-%02d", year, month, selectedDay));
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
    }

    public static void main(String[] args) {
        new HeadViewDetails();
    }
}
