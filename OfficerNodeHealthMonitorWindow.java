import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class OfficerNodeHealthMonitorWindow extends JFrame {

    private final JComboBox<String> portCombo  = new JComboBox<>();
    private final JButton refreshPorts = new JButton("Refresh Ports");
    private final JButton connect      = new JButton("Connect");
    private final JButton disconnect   = new JButton("Disconnect");
    private final JCheckBox simulator  = new JCheckBox("Use Simulator");
    private final JLabel status        = new JLabel("Not connected.");

    private final JLabel onlineCount   = new JLabel("0");
    private final JLabel degradedCount = new JLabel("0");
    private final JLabel offlineCount  = new JLabel("0");

    private final NodeHealthPanel panel = new NodeHealthPanel();
    private NodeHealthMonitor monitor;

    public OfficerNodeHealthMonitorWindow() {
        setTitle("AOMA-Heritage Monitor — Node Health Monitor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        RoleMenuBar.install(this, RoleMenuBar.Role.OFFICER);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadPorts();
        simulator.addActionListener(e -> portCombo.setEnabled(!simulator.isSelected()));
        connect.addActionListener(e -> doConnect());
        disconnect.addActionListener(e -> doDisconnect());
        refreshPorts.addActionListener(e -> loadPorts());
        disconnect.setEnabled(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                if (monitor != null) monitor.stop();
            }
        });

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 14, 6, 14));

        JLabel title = new JLabel(
                "Automated - Operational Modal Analysis to Monitor the Safety and Serviceability of Heritage Buildings",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildCenter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(0, 10, 0, 10));

        // --- top control bar ---
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controlBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(4, 4, 4, 4)));

        JLabel sectionTitle = new JLabel("Node Health Monitor");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 15));
        controlBar.add(sectionTitle);
        controlBar.add(Box.createHorizontalStrut(20));

        portCombo.setPreferredSize(new Dimension(160, 28));
        portCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        controlBar.add(new JLabel("Port:"));
        controlBar.add(portCombo);
        controlBar.add(refreshPorts);
        controlBar.add(simulator);
        controlBar.add(connect);
        controlBar.add(disconnect);

        wrapper.add(controlBar, BorderLayout.NORTH);

        // --- summary strip ---
        JPanel summary = new JPanel(new GridLayout(1, 3, 8, 0));
        summary.setBorder(new EmptyBorder(8, 0, 8, 0));
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        summary.add(summaryCard("Online",   onlineCount,   new Color(21, 128, 61)));
        summary.add(summaryCard("Degraded", degradedCount, new Color(161, 98, 7)));
        summary.add(summaryCard("Offline",  offlineCount,  new Color(185, 28, 28)));

        JPanel north = new JPanel(new BorderLayout());
        north.add(controlBar, BorderLayout.NORTH);
        north.add(summary, BorderLayout.SOUTH);
        wrapper.add(north, BorderLayout.NORTH);

        // --- scrollable node cards ---
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel summaryCard(String label, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 2));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(6, 12, 6, 12)));
        card.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(color);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(120, 120, 120)),
                new EmptyBorder(6, 12, 6, 12)));

        status.setFont(new Font("Arial", Font.BOLD, 13));
        status.setForeground(Color.DARK_GRAY);
        footer.add(status, BorderLayout.WEST);

        JLabel hint = new JLabel("8 slave ESP32 nodes monitored in real time");
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        hint.setForeground(Color.GRAY);
        footer.add(hint, BorderLayout.EAST);
        return footer;
    }

    private void loadPorts() {
        portCombo.removeAllItems();
        java.util.List<String> ports = NodeHealthMonitor.listSystemPortNames();
        if (ports.isEmpty()) {
            portCombo.addItem("(no ports found)");
        } else {
            for (String p : ports) portCombo.addItem(p);
        }
    }

    private void doConnect() {
        if (monitor != null) monitor.stop();
        monitor = new NodeHealthMonitor(states -> SwingUtilities.invokeLater(
                () -> onUpdate(states)));

        if (simulator.isSelected()) {
            monitor.startSimulator();
            status.setText("Simulator running — 8 nodes active.");
            status.setForeground(new Color(21, 128, 61));
        } else {
            String port = (String) portCombo.getSelectedItem();
            if (port == null || port.startsWith("(")) {
                status.setText("No serial port selected.");
                status.setForeground(new Color(185, 28, 28));
                return;
            }
            try {
                monitor.startSerial(port);
                status.setText("Connected to " + port + " — listening for node data.");
                status.setForeground(new Color(21, 128, 61));
            } catch (Exception ex) {
                status.setText("Failed to open " + port + ": " + ex.getMessage());
                status.setForeground(new Color(185, 28, 28));
                monitor = null;
                return;
            }
        }
        connect.setEnabled(false);
        disconnect.setEnabled(true);
        portCombo.setEnabled(false);
        simulator.setEnabled(false);
        refreshPorts.setEnabled(false);
    }

    private void doDisconnect() {
        if (monitor != null) {
            monitor.stop();
            monitor = null;
        }
        status.setText("Disconnected.");
        status.setForeground(Color.DARK_GRAY);
        connect.setEnabled(true);
        disconnect.setEnabled(false);
        portCombo.setEnabled(!simulator.isSelected());
        simulator.setEnabled(true);
        refreshPorts.setEnabled(true);
        onlineCount.setText("0");
        degradedCount.setText("0");
        offlineCount.setText("0");
    }

    void onUpdate(Map<String, NodeHealthMonitor.NodeState> states) {
        panel.updateStates(states);
        onlineCount.setText(String.valueOf(
                panel.countByStatus(NodeHealthMonitor.NodeStatus.ONLINE, states)));
        degradedCount.setText(String.valueOf(
                panel.countByStatus(NodeHealthMonitor.NodeStatus.DEGRADED, states)));
        offlineCount.setText(String.valueOf(
                panel.countByStatus(NodeHealthMonitor.NodeStatus.OFFLINE, states)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OfficerNodeHealthMonitorWindow::new);
    }
}
