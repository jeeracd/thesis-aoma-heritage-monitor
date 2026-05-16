import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public final class NodeHealthPanel extends JPanel {

    private static final Color ONLINE_BG   = new Color(220, 252, 231);
    private static final Color ONLINE_FG   = new Color(21, 128, 61);
    private static final Color DEGRADED_BG = new Color(254, 249, 195);
    private static final Color DEGRADED_FG = new Color(161, 98, 7);
    private static final Color OFFLINE_BG  = new Color(254, 226, 226);
    private static final Color OFFLINE_FG  = new Color(185, 28, 28);
    private static final Color UNKNOWN_BG  = new Color(243, 244, 246);
    private static final Color UNKNOWN_FG  = Color.DARK_GRAY;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Map<String, Card> cards = new LinkedHashMap<>();

    private final class Card extends JPanel {
        final String nodeId;
        final JLabel statusDot;
        final JLabel statusText;
        final JLabel rssiValue;
        final JLabel lossValue;
        final JLabel lastSeenValue;
        final JLabel tsfValue;
        final JLabel interpValue;
        final JLabel updatedAt;

        Card(String nodeId) {
            this.nodeId = nodeId;
            setLayout(new BorderLayout(0, 4));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(10, 12, 10, 12)));
            setBackground(UNKNOWN_BG);

            JPanel header = new JPanel(new BorderLayout(6, 0));
            header.setOpaque(false);

            statusDot = new JLabel("●");
            statusDot.setFont(new Font("Arial", Font.PLAIN, 18));
            statusDot.setForeground(UNKNOWN_FG);

            JLabel idLabel = new JLabel("ESP32 Node " + nodeId);
            idLabel.setFont(new Font("Arial", Font.BOLD, 13));

            statusText = new JLabel("—");
            statusText.setFont(new Font("Arial", Font.BOLD, 12));
            statusText.setForeground(UNKNOWN_FG);
            statusText.setHorizontalAlignment(SwingConstants.RIGHT);

            header.add(statusDot, BorderLayout.WEST);
            header.add(idLabel, BorderLayout.CENTER);
            header.add(statusText, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(3, 4, 6, 4));
            grid.setOpaque(false);

            rssiValue    = metric(grid, "RSSI (dBm)",  "—");
            lossValue    = metric(grid, "Loss (%)",    "—");
            lastSeenValue= metric(grid, "Last Seen",   "—");
            tsfValue     = metric(grid, "TSF Sync",    "—");
            interpValue  = metric(grid, "Interpolated","—");
            updatedAt    = metric(grid, "Updated",     "—");

            add(grid, BorderLayout.CENTER);
        }

        private JLabel metric(JPanel parent, String label, String init) {
            JPanel cell = new JPanel(new BorderLayout(0, 1));
            cell.setOpaque(false);
            JLabel lbl = new JLabel(label, SwingConstants.LEFT);
            lbl.setFont(new Font("Arial", Font.PLAIN, 10));
            lbl.setForeground(Color.GRAY);
            JLabel val = new JLabel(init, SwingConstants.LEFT);
            val.setFont(new Font("Arial", Font.BOLD, 12));
            cell.add(lbl, BorderLayout.NORTH);
            cell.add(val, BorderLayout.CENTER);
            parent.add(cell);
            return val;
        }

        void applyState(NodeHealthMonitor.NodeState s) {
            Color bg, fg;
            String label;
            switch (s.status()) {
                case ONLINE:   bg = ONLINE_BG;   fg = ONLINE_FG;   label = "ONLINE";   break;
                case DEGRADED: bg = DEGRADED_BG; fg = DEGRADED_FG; label = "DEGRADED"; break;
                default:       bg = OFFLINE_BG;  fg = OFFLINE_FG;  label = "OFFLINE";  break;
            }
            setBackground(bg);
            statusDot.setForeground(fg);
            statusText.setText(label);
            statusText.setForeground(fg);
            rssiValue.setText(s.rssiDbm() + " dBm");
            lossValue.setText(String.format("%.1f%%", s.lossPercent()));
            lastSeenValue.setText(s.lastSeenMs() + " ms");
            tsfValue.setText(s.tsfSync() ? "Yes" : "No");
            interpValue.setText(s.interpolated() ? "Yes" : "No");
            updatedAt.setText(TIME_FMT.format(LocalTime.now()));

            rssiValue.setForeground(s.rssiDbm() < -80 ? OFFLINE_FG : Color.BLACK);
            lossValue.setForeground(s.lossPercent() >= 10 ? OFFLINE_FG
                    : s.lossPercent() >= 5 ? DEGRADED_FG : Color.BLACK);
        }

        void applyUnknown() {
            setBackground(UNKNOWN_BG);
            statusDot.setForeground(UNKNOWN_FG);
            statusText.setText("NO DATA");
            statusText.setForeground(UNKNOWN_FG);
            rssiValue.setText("—");
            lossValue.setText("—");
            lastSeenValue.setText("—");
            tsfValue.setText("—");
            interpValue.setText("—");
            updatedAt.setText("—");
        }
    }

    public NodeHealthPanel() {
        setLayout(new GridLayout(0, 4, 10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(249, 250, 251));

        for (int i = 1; i <= NodeHealthMonitor.NODE_COUNT; i++) {
            String id = "S" + i;
            Card card = new Card(id);
            cards.put(id, card);
            add(card);
        }
    }

    public void updateStates(Map<String, NodeHealthMonitor.NodeState> states) {
        SwingUtilities.invokeLater(() -> {
            for (Card card : cards.values()) {
                NodeHealthMonitor.NodeState s = states.get(card.nodeId);
                if (s != null) {
                    card.applyState(s);
                } else {
                    card.applyUnknown();
                }
                card.repaint();
            }
        });
    }

    public int countByStatus(NodeHealthMonitor.NodeStatus target,
                             Map<String, NodeHealthMonitor.NodeState> states) {
        int count = 0;
        for (NodeHealthMonitor.NodeState s : states.values()) {
            if (s.status() == target) count++;
        }
        return count;
    }
}
