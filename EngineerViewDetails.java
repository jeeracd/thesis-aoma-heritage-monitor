import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class EngineerViewDetails extends JFrame {
    private final UUID projectId;

    public EngineerViewDetails() {
        this(null);
    }

    public EngineerViewDetails(UUID projectId) {
        this.projectId = projectId;
        setTitle("AOMA-Heritage Monitor - View Details");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1225, 720); // responsible for the size of the window
        setSize(1225, 720);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x_view = (screenSize.width - getWidth()) / 2;
        int y_view = (screenSize.height - getHeight()) / 2 - 320; //the use of this is to adjust the hright of the window
        setLocation(x_view, y_view);

        Project project = null;
        if (projectId != null) {
            AppSession.setActiveProjectId(projectId);
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

        JPanel engineerPanel = new JPanel(null);
        RoleMenuBar.install(this, RoleMenuBar.Role.ENGINEER);

        tabsUI.addTab("Projects", new JPanel());
        tabsUI.addTab("View", engineerPanel);
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
            new EngineerEditStructuralDetails();
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
            new EngineerImportSensorData();
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
            new EngineerExportSensorData();
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Directing to View Report page.",
                    "View Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
            new EngineerViewReport();
            this.dispose();
        });

        JMenuItem systemLogs = new JMenuItem("System Logs");
        systemLogs.setEnabled(false); // disabled for now

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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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
            if (!confirmNavigateIfDirty(controller)) {
                return;
            }
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

        JLabel LGULabel = new JLabel("STRUCTURAL ENGINEER ACCOUNT");
        LGULabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGULabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGULabel.setBounds(925, 5, 280, 38);

        engineerPanel.add(LGULabel);

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

        engineerPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1200, 520);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        engineerPanel.add(centerPanel);

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
            CalendarDatePicker.show(this, dateField);
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
                                EngineerViewDetails.this,
                                "Project details saved successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } catch (Exception ex) {
                        controller.applySaveFailureRevert();
                        applyControllerToUI.run();
                        setEditableState.run();
                        JOptionPane.showMessageDialog(
                                EngineerViewDetails.this,
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
            new EngineerDashboardManageAccess();
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

        Map<String, Path> sessionPaths = new LinkedHashMap<>();
        Map<String, String> datasetToSessionKey = new LinkedHashMap<>();
        DefaultComboBoxModel<String> sessionComboModel = new DefaultComboBoxModel<>();
        sessionComboModel.addElement("---none---");
        List<Path> availableSessions = OmaResultsModel.listAvailableOutDirs();
        for (Path p : availableSessions) {
            String sessionKey = "#" + p.getFileName().toString();
            String display = sessionKey;
            if (this.projectId != null) {
                String datasetId = ProjectDatasetIdStore.getOrCreateDatasetId(this.projectId, sessionKey);
                if (datasetId != null && !datasetId.isBlank()) {
                    display = datasetId;
                }
            }
            if (!sessionPaths.containsKey(display)) {
                sessionPaths.put(display, p);
                datasetToSessionKey.put(display, sessionKey);
                sessionComboModel.addElement(display);
            }
        }

        JComboBox<String> sessionDropdown = new JComboBox<>(sessionComboModel);
        sessionDropdown.setFont(fieldFont);
        sessionDropdown.setBounds(leftX + 210, y + gap, 200, 30);
        sessionDropdown.setEnabled(true);
        formPanel.add(sessionDropdown);
        if (this.projectId != null) {
            String active = ProjectDatasetIdStore.getActiveDatasetId(this.projectId);
            if (active != null && !active.isBlank() && sessionPaths.containsKey(active)) {
                sessionDropdown.setSelectedItem(active);
            }
        }

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
        engineerPanel.add(reportTypePanel);

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

        JLabel actionExecLbl = new JLabel("ACTION: NO IMMEDIATE INTERVENTION REQUIRED");
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
        JLabel nscpExecLbl = new JLabel("NSCP Section 405 (Drift Limits): COMPLIANT");
        nscpExecLbl.setFont(normalFont);
        nscpExecLbl.setBounds(40, 450, 400, 25);
        reportPanel.add(nscpExecLbl);

        //PASSED PASSED
        JLabel pdExecLbl = new JLabel("P.D. 1096 (Structural Safety): PASSED");
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

        JLabel dotExec1 = new JLabel("• No abnormal frequency shifts detected (>5% deviation).");
        dotExec1.setFont(normalFont);
        dotExec1.setBounds(60, 600, 600, 25);
        reportPanel.add(dotExec1);

        JLabel dotExec2 = new JLabel("• Ambient vibration levels are within normal limits.");
        dotExec2.setFont(normalFont);
        dotExec2.setBounds(60, 630, 600, 25);
        reportPanel.add(dotExec2);

        JLabel dotExec3 = new JLabel("• All wireless sensors are synchronized and active.");
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

        // BUTTON SETTINGS
        int btnWidth = 140;
        int btnHeight = 35;
        int btnGap = 20; // space between buttons

        // total width of both buttons + gap
        int totalWidth = (btnWidth * 2) + btnGap;

        // starting X so the whole group is centered under recoExecText
        int startX = recoExecText.getX() + (recoExecText.getWidth() - totalWidth) / 2;
        int btnY = recoExecText.getY() + recoExecText.getHeight() + 10;

        // REJECT BUTTON
        JButton rejectBtn = new JButton("REJECT");
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 14));
        rejectBtn.setFocusPainted(false);
        rejectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rejectBtn.setBackground(new Color(200, 0, 0));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setOpaque(true);
        rejectBtn.setContentAreaFilled(true);   
        rejectBtn.setBorderPainted(false);      

        rejectBtn.setBounds(startX, btnY, btnWidth, btnHeight);
        reportPanel.add(rejectBtn); 

        rejectBtn.addActionListener(e -> {
        JOptionPane.showMessageDialog(
                this,
                "The report has been marked as REJECTED.\nYou will now proceed to the rejection details page.",
                "Rejected",
                JOptionPane.WARNING_MESSAGE
        );
        new EngineerViewDetailsReject();
        this.dispose();
        });

        // APPROVE BUTTON
        JButton approveBtn = new JButton("APPROVE");
        approveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        approveBtn.setBackground(new Color(0, 153, 0));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setOpaque(true);
        approveBtn.setContentAreaFilled(true);
        approveBtn.setBorderPainted(false);

        approveBtn.setBounds(startX + btnWidth + btnGap, btnY, btnWidth, btnHeight);

        reportPanel.add(approveBtn);

        approveBtn.addActionListener(e -> {
        JOptionPane.showMessageDialog(
                this,
                "The report has been APPROVED.\nYou will now proceed to the approval confirmation page.",
                "Approved",
                JOptionPane.INFORMATION_MESSAGE
        );
        new EngineerViewDetailsApprove();
        this.dispose();
    });

        JPanel reportContainer = new JPanel(new CardLayout());
        reportContainer.setBounds(
                10,
                reportTopY + 60,
                1200,
                750
        );

        engineerPanel.add(reportContainer);

        JScrollPane reportScrollPane = new JScrollPane(reportPanel);
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

        DefaultTableModel modalTableModel = new DefaultTableModel(modalColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable modalTable = new JTable(modalTableModel);
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

        DateTimeFormatter sessionTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());
        Runnable applySessionSelection = () -> {
            String selected = (String) sessionDropdown.getSelectedItem();
            Path outDir = selected == null ? null : sessionPaths.get(selected);
            boolean hasSession = outDir != null;

            reportTypeLbl.setVisible(hasSession);
            reportTypePanel.setVisible(hasSession);
            reportContainer.setVisible(hasSession);
            executiveSummaryBtn.setEnabled(hasSession);
            fullAuditBtn.setEnabled(hasSession);

            if (!hasSession) {
                setSize(1225, 720);
                datasetExecLbl.setText("DATASET ID:");
                dateAssessmentExecLbl.setText("DATE:");
                statusExecLbl2.setText("STATUS:");
                actionExecLbl.setText("ACTION:");
                descExecText.setText("Select a monitoring session to view the report.");
                dotExec1.setText("•");
                dotExec2.setText("•");
                dotExec3.setText("•");
                recoExecText.setText("");

                datasetTechLbl.setText("DATASET ID:");
                dateAssessmentTechLbl.setText("DATE:");
                modalTableModel.setRowCount(0);
                reliabilityText.setText("Select a monitoring session to view technical details.");
                lessThanSymbolTech1.setText("> Mode 1 MPC/MPD:");
                lessThanSymbolTech2.setText("> Mode 2 MPC/MPD:");
                lessThanSymbolTech3.setText("> Mode 3 MPC/MPD:");
                dotTech1.setText("• Computational Run Time:");
                dotTech2.setText("• Memory Usage:");
                dotTech3.setText("• Time Sync Offset:");
                spectralDataText.setText("Artifacts: ");
                return;
            }

            setSize(1225, 1500);
            OmaResultsModel results = OmaResultsModel.loadFromDirOrEmpty(outDir);

            String dateStr;
            try {
                long ms = Long.parseLong(outDir.getFileName().toString());
                dateStr = sessionTimeFmt.format(Instant.ofEpochMilli(ms));
            } catch (Exception ex) {
                dateStr = sessionTimeFmt.format(Instant.ofEpochMilli(outDir.toFile().lastModified()));
            }

            dateAssessmentExecLbl.setText("DATE: " + dateStr);
            UUID effectiveProjectId = this.projectId == null ? AppSession.getActiveProjectId() : this.projectId;
            String datasetId = "";
            if (effectiveProjectId != null) {
                String sessionKey = datasetToSessionKey.getOrDefault(selected, selected);
                if (ProjectDatasetIdStore.isValidDatasetId(effectiveProjectId, selected)) {
                    boolean ok = ProjectDatasetIdStore.setActiveDatasetId(effectiveProjectId, selected);
                    datasetId = ok ? selected : "";
                }
                if (datasetId.isBlank()) {
                    datasetId = ProjectDatasetIdStore.setActiveDatasetForSession(effectiveProjectId, sessionKey);
                }
            }
            if (datasetId == null || datasetId.isBlank()) {
                datasetId = "—";
                if (effectiveProjectId == null) {
                    JOptionPane.showMessageDialog(this, "No active project is selected. Dataset ID cannot be assigned.", "Dataset Sync Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to assign a project dataset ID for this monitoring session.", "Dataset Sync Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            datasetExecLbl.setText("DATASET ID: " + datasetId);
            dateAssessmentTechLbl.setText("DATE: " + dateStr);
            datasetTechLbl.setText("DATASET ID: " + datasetId);

            OmaResultsModel.Severity overall = OmaResultsModel.Severity.OK;
            for (OmaResultsModel.ModeRow r : results.modes()) {
                if (r.severity() == OmaResultsModel.Severity.CRITICAL) {
                    overall = OmaResultsModel.Severity.CRITICAL;
                    break;
                }
                if (r.severity() == OmaResultsModel.Severity.WARNING) {
                    overall = OmaResultsModel.Severity.WARNING;
                }
            }

            String statusText;
            String actionText;
            String descText;
            String recoText;
            if (overall == OmaResultsModel.Severity.OK) {
                statusText = "STATUS: SERVICEABLE";
                actionText = "ACTION: NO IMMEDIATE INTERVENTION REQUIRED";
                descText = "The automated OMA analysis indicates stable vibration characteristics within expected bounds.";
                recoText = "Continue monitoring. Schedule a visual inspection only if the risk level increases.";
            } else if (overall == OmaResultsModel.Severity.WARNING) {
                statusText = "STATUS: REVIEW REQUIRED";
                actionText = "ACTION: PERFORM NON-INVASIVE SITE CHECK";
                descText = "One or more indicators are out-of-tolerance. A focused, non-invasive inspection is recommended to protect heritage fabric.";
                recoText = "Coordinate with a structural engineer and heritage conservator before any intervention. Increase monitoring frequency.";
            } else {
                statusText = "STATUS: UNSAFE / URGENT REVIEW";
                actionText = "ACTION: ESCALATE TO ENGINEERING REVIEW";
                descText = "Critical out-of-tolerance indicators were detected. Treat as urgent until verified by a qualified professional.";
                recoText = "Restrict access to affected areas if needed and initiate an urgent engineering + conservation review.";
            }

            statusExecLbl2.setText(statusText);
            actionExecLbl.setText(actionText);
            descExecText.setText(descText);

            int issues = results.issuesCount();
            if (issues == 0) {
                dotExec1.setText("• No out-of-tolerance modal indicators detected.");
            } else {
                dotExec1.setText("• " + issues + " out-of-tolerance modal indicators detected (review recommended).");
            }
            dotExec2.setText("• Results are automatically generated; verify on-site before intervention.");
            dotExec3.setText("• Heritage note: prioritize reversible and non-invasive checks first.");
            recoExecText.setText(recoText);

            modalTableModel.setRowCount(0);
            if (results.modes().isEmpty()) {
                modalTableModel.addRow(new Object[] {"-", "-", "-", "No modal_properties.csv found"});
            } else {
                int count = 0;
                for (OmaResultsModel.ModeRow r : results.modes()) {
                    if (count >= 10) {
                        break;
                    }
                    String fn = String.format("%.3f Hz", r.frequencyHz());
                    String xi = Double.isFinite(r.dampingRatio()) ? String.format("%.2f %%", r.dampingRatio() * 100.0) : "N/A";
                    String dev = "N/A (no baseline configured)";
                    modalTableModel.addRow(new Object[] {String.valueOf(r.modeIndex()), fn, xi, dev});
                    count++;
                }
            }

            reliabilityText.setText("MPC/MPD indicators summarize mode-shape consistency (higher MPC and lower |MPD| are better).");
            if (results.modes().size() >= 1) {
                OmaResultsModel.ModeRow r = results.modes().get(0);
                lessThanSymbolTech1.setText(String.format("> Mode %d MPC: %.3f | MPD: %.3f", r.modeIndex(), r.mpc(), r.mpd()));
            } else {
                lessThanSymbolTech1.setText("> Mode 1 MPC/MPD: N/A");
            }
            if (results.modes().size() >= 2) {
                OmaResultsModel.ModeRow r = results.modes().get(1);
                lessThanSymbolTech2.setText(String.format("> Mode %d MPC: %.3f | MPD: %.3f", r.modeIndex(), r.mpc(), r.mpd()));
            } else {
                lessThanSymbolTech2.setText("> Mode 2 MPC/MPD: N/A");
            }
            if (results.modes().size() >= 3) {
                OmaResultsModel.ModeRow r = results.modes().get(2);
                lessThanSymbolTech3.setText(String.format("> Mode %d MPC: %.3f | MPD: %.3f", r.modeIndex(), r.mpc(), r.mpd()));
            } else {
                lessThanSymbolTech3.setText("> Mode 3 MPC/MPD: N/A");
            }

            dotTech1.setText("• Computational Run Time: N/A");
            dotTech2.setText("• Memory Usage: N/A");
            dotTech3.setText("• Time Sync Offset: N/A");
            spectralDataText.setText("Artifacts: " + outDir.toString());
        };

        for (ActionListener al : sessionDropdown.getActionListeners()) {
            sessionDropdown.removeActionListener(al);
        }
        sessionDropdown.addActionListener(e -> applySessionSelection.run());
        applySessionSelection.run();

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setPreferredSize(new Dimension(1400, 45));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.GRAY));

        JLabel statusLbl = new JLabel("Status: ESP32 Hub Not Connected");
        statusLbl.setFont(new Font("Arial", Font.BOLD, 14));
        statusLbl.setForeground(Color.RED);
        statusLbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        footerPanel.add(statusLbl, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(engineerPanel, BorderLayout.CENTER);
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

    public static void main(String[] args) {
        new EngineerViewDetails();
    }
}
