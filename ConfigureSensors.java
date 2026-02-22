import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;

public class ConfigureSensors extends JFrame {

    // Define common fonts and colors used in the design
    private final Font mainFontBold = new Font("Arial", Font.BOLD, 14);
    private final Font titleFont = new Font("Arial", Font.BOLD, 16);
    private final Color bgColor = Color.LIGHT_GRAY;
    private final Color fieldColor = new Color(220, 220, 220); // Slightly darker gray for data fields
    private final Color statusGreen = new Color(0, 153, 0);
    private final Color statusRed = Color.RED;
    private final Color statusOrange = Color.ORANGE;

    public ConfigureSensors() {
        setTitle("AOMA-Heritage Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Setting size similar to the original base code
        setSize(1400, 850);
        setLocationRelativeTo(null);

        // --- Main Tabbed Pane Setup (adapted from base code) ---
        JTabbedPane mainTabs = new JTabbedPane(JTabbedPane.TOP);
        mainTabs.setFont(new Font("Arial", Font.BOLD, 15));
        mainTabs.setBackground(bgColor);
        mainTabs.setUI(new FoldingTabbedPaneUI()); // Use custom UI for selected tab highlighting

        // The main container panel for the "Setup & Connection" tab
        JPanel setupContainerPanel = new JPanel();
        setupContainerPanel.setLayout(null);
        setupContainerPanel.setBackground(Color.WHITE);

        // ===========================================================================
        // === LEFT SIDEBAR ===
        // ===========================================================================
        int sidebarWidth = 320;
        int sidebarX = 10;

        // --- 1. Building Profile Information Panel ---
        JPanel buildingProfilePanel = new JPanel();
        buildingProfilePanel.setLayout(null);
        buildingProfilePanel.setBounds(sidebarX, 10, sidebarWidth, 230);
        buildingProfilePanel.setBorder(createTitledBorder("Building Profile Information"));
        buildingProfilePanel.setBackground(Color.WHITE);

        // Helper to create the gray read-only data fields shown in the image
        addProfileField(buildingProfilePanel, "Building Name: San Diego De Alcala", 30);
        addProfileField(buildingProfilePanel, "Year Constructed: 1999", 70);
        addProfileField(buildingProfilePanel, "Material Used Type: Wood, Brick", 110);
        addProfileField(buildingProfilePanel, "Conservation Status: National Heritage", 150);

        // The ellipsis "..." button
        JButton editProfileBtn = new JButton("•••");
        styleSmallButton(editProfileBtn);
        editProfileBtn.setBounds(sidebarWidth - 50, 0, 40, 25); // positioned in the border header area
        buildingProfilePanel.add(editProfileBtn);

        setupContainerPanel.add(buildingProfilePanel);

        // --- 2. ESP32 Status Panel ---
        JPanel espStatusPanel = new JPanel();
        espStatusPanel.setLayout(null);
        espStatusPanel.setBounds(sidebarX, 250, sidebarWidth, 450);
        espStatusPanel.setBorder(createTitledBorder("ESP32 Status"));
        espStatusPanel.setBackground(Color.WHITE);

        // Small refresh button near title
        JButton refreshBtn = new JButton("⟳");
        styleSmallButton(refreshBtn);
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        refreshBtn.setBounds(sidebarWidth - 50, 0, 40, 25);
        espStatusPanel.add(refreshBtn);

        // Table Data for ESP32 Status
        String[] espCols = {"Sensor ID", "Location", "Status"};
        Object[][] espData = {
                {"Sensor 1", "Corner", "Connected"},
                {"Sensor 2", "Corner", "Connected"},
                {"Sensor 3", "Corner", "Connected"},
                {"Sensor 4", "Middle", "Connected"},
                {"Sensor 5", "Middle", "Connected"},
                {"Sensor 6", "Corner", "Connected"},
                {"ESP32 HUB", "Center", "Connected"}
        };

        JTable espTable = createStyledTable(espData, espCols);
        // Apply the custom color renderer from the base code to the Status column
        espTable.getColumnModel().getColumn(2).setCellRenderer(new StatusColorRenderer());

        JScrollPane espScroll = new JScrollPane(espTable);
        espScroll.setBounds(10, 30, sidebarWidth - 20, 380);
        espStatusPanel.add(espScroll);

        // Ellipsis button at the bottom of ESP panel
        JButton moreEspBtn = new JButton("•••");
        styleSmallButton(moreEspBtn);
        moreEspBtn.setBounds(sidebarWidth / 2 - 20, 415, 40, 25);
        espStatusPanel.add(moreEspBtn);

        setupContainerPanel.add(espStatusPanel);

        // ===========================================================================
        // === RIGHT SIDE MAIN CONTENT ===
        // ===========================================================================
        int mainContentX = sidebarX + sidebarWidth + 20;
        int mainContentWidth = 1360 - mainContentX;

        // --- Top Banner Label ---
        JLabel topBanner = new JLabel("Connect the ESP32 hub to monitor the condition of a heritage building", SwingConstants.CENTER);
        topBanner.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 18));
        topBanner.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        topBanner.setBounds(mainContentX, 10, mainContentWidth, 50);
        setupContainerPanel.add(topBanner);

        // --- Configure Sensors Panel (The complex table) ---
        JPanel configurePanel = new JPanel();
        configurePanel.setLayout(null);
        configurePanel.setBounds(mainContentX, 70, mainContentWidth, 630);
        configurePanel.setBorder(createTitledBorder("Configure Sensors"));
        configurePanel.setBackground(Color.WHITE);

        String[] configCols = {"Sensor ID", "Location", "Status", "Device Type", "Timestamp", "Time Sync"};
        Object[][] configData = {
            {"ESP32 HUB", "Center", "Connected", "HUB", "00:01:00", "Synced (<1ms)"},
            {"Sensor 1", "Corner", "Connected", "Accelerometer", "00:01:00", "Synced (<1ms)"},
            {"Sensor 2", "Corner", "Connected", "Accelerometer", "00:01:00", "Synced (<1ms)"},
            {"Sensor 3", "Corner", "Connected", "Accelerometer", "00:01:00", "Aligning.."},
            {"Sensor 4", "Middle", "Connected", "Accelerometer", "00:01:00", "Aligning.."},
            {"Sensor 5", "Middle", "Connected", "Accelerometer", "00:01:00", "Error (>5ms)"},
            {"Sensor 6", "Middle", "Connected", "Accelerometer", "00:01:00", "Synced (<1ms)"},
            {"Sensor 7", "Middle", "Connected", "Accelerometer", "00:01:00", "Synced (<1ms)"},
            {"Sensor 8", "Middle", "Connected", "Accelerometer", "00:01:00", "Error (>5ms)"}
        };

        JTable configTable = createStyledTable(configData, configCols);

        // --- Setting up Cell Editors (ComboBoxes) ---
        // Location ComboBox Editor
        JComboBox<String> locationCb = new JComboBox<>(new String[]{"Center", "Corner", "Middle"});
        // Device Type ComboBox Editor
        JComboBox<String> deviceCb = new JComboBox<>(new String[]{"HUB", "Accelerometer", "Gyroscrope"});

        // Apply editors to specific columns. Note: We need a custom editor that
        // doesn't allow editing the "HUB" row, but for simplicity in this visual recreation,
        // we apply it generally.
        configTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(locationCb));
        configTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(deviceCb));

        // --- Setting up Cell Renderers ---
        // Reuse Status renderer for column 2
        configTable.getColumnModel().getColumn(2).setCellRenderer(new StatusColorRenderer());
        // Custom Time Sync renderer for column 5 (handles green, orange, red text)
        configTable.getColumnModel().getColumn(5).setCellRenderer(new TimeSyncRenderer());
        // Custom ComboBox renderer to show dropdown arrow visually in the table
        configTable.getColumnModel().getColumn(1).setCellRenderer(new ComboBoxLookRenderer());
        configTable.getColumnModel().getColumn(3).setCellRenderer(new ComboBoxLookRenderer());


        JScrollPane configScroll = new JScrollPane(configTable);
        configScroll.setBounds(10, 30, mainContentWidth - 20, 530);
        configurePanel.add(configScroll);

        // "Sync now" Button
        JButton syncNowBtn = new JButton("Sync now");
        syncNowBtn.setFont(mainFontBold);
        syncNowBtn.setBackground(bgColor);
        syncNowBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        syncNowBtn.setFocusable(false);
        syncNowBtn.setBounds(mainContentWidth - 220, 570, 200, 40);
        configurePanel.add(syncNowBtn);

        setupContainerPanel.add(configurePanel);

        // ===========================================================================
        // === BOTTOM STATUS BAR ===
        // ===========================================================================
        JLabel bottomStatusLabel = new JLabel("Status: ESP32 Hub Connected");
        bottomStatusLabel.setFont(mainFontBold);
        bottomStatusLabel.setForeground(statusGreen);
        bottomStatusLabel.setBounds(10, 710, 400, 30);
        setupContainerPanel.add(bottomStatusLabel);


        // Add tabs to main pane
        mainTabs.addTab("Setup & Connection", setupContainerPanel);
        mainTabs.addTab("Analysis", new JPanel()); // Placeholder
        mainTabs.addTab("Report", new JPanel());   // Placeholder

        // Add main tabbed pane to frame
        add(mainTabs);
        setVisible(true);
    }

    // ===========================================================================
    // === HELPER METHODS FOR UI STYLING ===
    // ===========================================================================

    /** Creates a standard TitledBorder consistent with the design. */
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), title);
        border.setTitleFont(titleFont);
        border.setTitleColor(Color.BLACK);
        return border;
    }

    /** Helper to add the gray, read-only data fields in the profile panel. */
    private void addProfileField(JPanel panel, String text, int yPos) {
        JLabel field = new JLabel(" " + text);
        field.setFont(mainFontBold);
        field.setOpaque(true);
        field.setBackground(fieldColor);
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        field.setBounds(10, yPos, 300, 30);
        panel.add(field);
    }

    /** Helper to style the small '...' and refresh buttons. */
    private void styleSmallButton(JButton btn) {
        btn.setBorder(null);
        btn.setBackground(Color.WHITE);
        btn.setFocusable(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        // Make it look flat/transparent until hovered (optional enhancement)
        btn.setContentAreaFilled(false);
        btn.setOpaque(true); // Needed for background color to show if contentAreaFilled is true
    }

    /** Creates a JTable with the predefined visual style (row height, fonts, centering). */
    private JTable createStyledTable(Object[][] data, String[] cols) {
        JTable table = new JTable(data, cols);
        table.setFont(mainFontBold);
        table.getTableHeader().setFont(mainFontBold);
        table.setRowHeight(35); // Taller rows as seen in image
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
        table.setGridColor(Color.BLACK);

        // Center align all cells by default
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // Center align header
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    // ===========================================================================
    // === INNER CLASSES (RENDERERS & UI) ===
    // ===========================================================================

    /** Renderer from base code: Colors text Green/Red based on "Connected" status. */
    class StatusColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                String text = value.toString().toLowerCase();
                if (text.equals("connected")) {
                    setForeground(statusGreen);
                } else if (text.equals("disconnected")) {
                    setForeground(statusRed);
                } else {
                    setForeground(Color.BLACK);
                }
            }
            setHorizontalAlignment(JLabel.CENTER);
            return c;
        }
    }

    /** Custom renderer for the "Time Sync" column to handle Green/Orange/Red states. */
    class TimeSyncRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // Reset color
            setForeground(Color.BLACK);

            if (value != null) {
                String text = value.toString();
                // Check if this is the HUB row (usually row 0). The HUB row in the image
                // just has green text, not the full status message.
                Object sensorId = table.getValueAt(row, 0);

                if (sensorId != null && sensorId.toString().contains("HUB")) {
                    // The HUB just shows green text in the image
                     setForeground(statusGreen);
                } else {
                    // Normal sensors show colored status
                    if (text.contains("Synced")) {
                        setForeground(statusGreen);
                    } else if (text.contains("Aligning")) {
                        setForeground(statusOrange);
                    } else if (text.contains("Error")) {
                        setForeground(statusRed);
                    }
                }
            }
            setHorizontalAlignment(JLabel.CENTER);
            return c;
        }
    }

    /** Fake renderer to make cells look like they have a dropdown arrow even when not editing. */
    class ComboBoxLookRenderer extends DefaultTableCellRenderer {
         // Using a unicode character for a down arrow
        private final String ARROW = " ▼";
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Don't add arrow to the HUB row columns in the image
            Object sensorId = table.getValueAt(row, 0);
            if (sensorId != null && sensorId.toString().contains("HUB")) {
                 return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            String textVal = (value == null) ? "" : value.toString() + ARROW;
            Component c = super.getTableCellRendererComponent(table, textVal, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);
            return c;
        }
    }


    /** Custom UI for JTabbedPane to replicate the blue highlight on selection from base code. */
    class FoldingTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            g.setColor(isSelected ? new Color(100, 149, 237) : bgColor); // Cornflower blueish for selection
            g.fillRect(x, y, w, h);
            // Add a black border around tabs to match image style
            g.setColor(Color.BLACK);
            g.drawRect(x, y, w, h);
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, java.awt.FontMetrics metrics, int tabIndex, String title, java.awt.Rectangle textRect, boolean isSelected) {
            g.setFont(font);
            g.setColor(Color.BLACK); // Text always black in the image
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            // Adjust tab insets to make them look boxier like the image
            tabInsets = new Insets(10, 20, 10, 20);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(5,0,0,0);
        }
    }

    public static void main(String[] args) {
        // Set cross-platform look and feel so borders look consistent
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Run the application
        javax.swing.SwingUtilities.invokeLater(() -> new ConfigureSensors());
    }
}