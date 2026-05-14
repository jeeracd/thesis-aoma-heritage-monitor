import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class OmaInterpretationPanel extends JPanel {
    private final JLabel header = new JLabel("OMA Interpretation");
    private final JTextArea text = new JTextArea();

    private volatile int peaksCount;

    public OmaInterpretationPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        header.setFont(new Font("Arial", Font.BOLD, 16));

        text.setFont(new Font("Arial", Font.PLAIN, 13));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setBorder(null);

        add(header, BorderLayout.NORTH);
        add(text, BorderLayout.CENTER);

        setPeaksCount(0);
        setSelectedPeak(null);
    }

    public void setPeaksCount(int count) {
        this.peaksCount = Math.max(0, count);
        if (text.getText() == null || text.getText().isBlank()) {
            setSelectedPeak(null);
        }
    }

    public void setSelectedPeak(FddPeak peak) {
        if (peak == null) {
            String base = "Detected peaks: " + peaksCount + ".\n\n"
                    + "Select a peak in the Natural Frequencies plot or the Damping table to view its estimated damping and mode shape.\n\n"
                    + "Best practice: validate candidate modes by checking damping plausibility, mode-shape consistency across channels, and stability across estimator settings.";
            text.setText(base);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Selected peak ").append(peak.peakIndex()).append("\n");
        sb.append("Frequency: ").append(formatHz(peak.frequencyHz())).append(" Hz\n");
        sb.append("SVD1 magnitude: ").append(String.format("%.2f", peak.svd1Db())).append(" dB\n");
        if (Double.isFinite(peak.dampingRatioPercent())) {
            sb.append("Estimated damping: ").append(String.format("%.2f", peak.dampingRatioPercent())).append(" %\n");
            sb.append("Bandwidth (-3 dB): ").append(formatHz(peak.bandwidthHz())).append(" Hz\n");
        } else {
            sb.append("Estimated damping: N/A (insufficient bandwidth crossing)\n");
        }
        sb.append("\nNotes:\n");
        sb.append("- Peaks are candidates; confirm they represent physical modes.\n");
        sb.append("- Damping here uses a simple half-power approximation and may be unreliable for closely spaced or heavily damped modes.");
        text.setText(sb.toString());
    }

    private static String formatHz(double hz) {
        if (!Double.isFinite(hz)) {
            return "";
        }
        if (hz >= 100) {
            return String.format("%.0f", hz);
        }
        if (hz >= 10) {
            return String.format("%.1f", hz);
        }
        if (hz >= 1) {
            return String.format("%.2f", hz);
        }
        return String.format("%.3f", hz);
    }
}

