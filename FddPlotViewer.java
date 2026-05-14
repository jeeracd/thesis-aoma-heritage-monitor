import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
 
public final class FddPlotViewer extends JPanel {
    private final FddPlotPanel plot = new FddPlotPanel();
 
    private final JTextField minHzField = new JTextField(6);
    private final JTextField maxHzField = new JTextField(6);
    private final JLabel rangeLabel = new JLabel("");
    private final JButton applyBtn = new JButton("Apply");
    private final JButton resetBtn = new JButton("Reset");
 
    private Double minHz;
    private Double maxHz;

    private java.util.List<FddPeak> peaks = java.util.List.of();
    private volatile FddResult lastResult;

    public interface PeakSelectionListener {
        void onPeakSelected(FddPeak peak);
    }

    private volatile PeakSelectionListener peakSelectionListener;
 
    public FddPlotViewer() {
        setLayout(new BorderLayout());
        setOpaque(false);
 
        JPanel controls = buildControls();
        add(controls, BorderLayout.NORTH);
        add(plot, BorderLayout.CENTER);
 
        wire();
        updateRangeLabel();
    }
 
    public void setStatusText(String text) {
        plot.setStatusText(text);
    }
 
    public void setResult(FddResult result) {
        lastResult = result;
        plot.setResult(result);
        applyRangeToPlot();

        peaks = FddPeakAnalysis.pickPeaks(result, 12, 6.0, 0.25);
        plot.setPeaks(peaks);

        plot.setPeakSelectionListener(binIndex -> {
            plot.setSelectedPeakBinIndex(binIndex);
            FddPeak p = findPeakByBinIndex(binIndex);
            PeakSelectionListener l = peakSelectionListener;
            if (l != null && p != null) {
                l.onPeakSelected(p);
            }
        });
    }

    public java.util.List<FddPeak> getPeaks() {
        return peaks;
    }

    public FddResult getResult() {
        return lastResult;
    }

    public void setPeakSelectionListener(PeakSelectionListener listener) {
        this.peakSelectionListener = listener;
    }

    public void selectPeakBinIndex(int binIndex) {
        plot.setSelectedPeakBinIndex(binIndex);
    }

    Double getMinHzForTesting() {
        return minHz;
    }
 
    Double getMaxHzForTesting() {
        return maxHz;
    }
 
    JTextField getMinHzFieldForTesting() {
        return minHzField;
    }
 
    JTextField getMaxHzFieldForTesting() {
        return maxHzField;
    }
 
    JButton getApplyBtnForTesting() {
        return applyBtn;
    }
 
    private JPanel buildControls() {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(8, 8, 6, 8));

        rangeLabel.setFont(UiControlMetrics.CONTROL_FONT);
        rangeLabel.setForeground(Color.DARK_GRAY);
        rangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel minLbl = new JLabel("Min Hz:");
        minLbl.setFont(UiControlMetrics.CONTROL_FONT);

        JLabel maxLbl = new JLabel("Max Hz:");
        maxLbl.setFont(UiControlMetrics.CONTROL_FONT);

        minLbl.setLabelFor(minHzField);
        maxLbl.setLabelFor(maxHzField);

        minHzField.setPreferredSize(new Dimension(90, UiControlMetrics.CONTROL_HEIGHT));
        maxHzField.setPreferredSize(new Dimension(90, UiControlMetrics.CONTROL_HEIGHT));

        applyBtn.setMnemonic('A');
        resetBtn.setMnemonic('S');

        JPanel row1 = new JPanel(new BorderLayout(UiControlMetrics.HGAP, 0));
        row1.setOpaque(false);
        UiControlMetrics.setRowMaxHeight(row1);
        row1.add(rangeLabel, BorderLayout.CENTER);
        outer.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, UiControlMetrics.HGAP, UiControlMetrics.VGAP));
        row2.setOpaque(false);
        UiControlMetrics.setRowMaxHeight(row2);
        row2.add(minLbl);
        row2.add(minHzField);
        row2.add(maxLbl);
        row2.add(maxHzField);
        row2.add(applyBtn);
        row2.add(resetBtn);
        outer.add(row2);

        UiControlMetrics.applyControlFont(rangeLabel, minHzField, maxHzField, applyBtn, resetBtn);
        UiControlMetrics.setPreferredHeight(applyBtn, UiControlMetrics.CONTROL_HEIGHT);
        UiControlMetrics.setPreferredHeight(resetBtn, UiControlMetrics.CONTROL_HEIGHT);

        return outer;
    }
 
    private void wire() {
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateRangeLabelPreview(); }
            @Override public void removeUpdate(DocumentEvent e) { updateRangeLabelPreview(); }
            @Override public void changedUpdate(DocumentEvent e) { updateRangeLabelPreview(); }
        };
        minHzField.getDocument().addDocumentListener(dl);
        maxHzField.getDocument().addDocumentListener(dl);
 
        applyBtn.addActionListener(e -> applyFromInputs());
        resetBtn.addActionListener(e -> {
            minHz = null;
            maxHz = null;
            minHzField.setText("");
            maxHzField.setText("");
            applyRangeToPlot();
            updateRangeLabel();
        });
    }
 
    private void applyFromInputs() {
        Double a = parseDoubleOrNull(minHzField.getText());
        Double b = parseDoubleOrNull(maxHzField.getText());
 
        if (a != null && a < 0) {
            showError("Minimum frequency must be ≥ 0.");
            return;
        }
        if (b != null && b < 0) {
            showError("Maximum frequency must be ≥ 0.");
            return;
        }
        if (a != null && b != null && !(b > a)) {
            showError("Minimum frequency must be less than maximum frequency.");
            return;
        }
        minHz = a;
        maxHz = b;
        applyRangeToPlot();
        updateRangeLabel();
        Window w = SwingUtilities.getWindowAncestor(this);
        Toast.show(w, "Frequency range updated", new Color(0, 128, 0), 1400);
    }
 
    private void applyRangeToPlot() {
        plot.setFrequencyBounds(minHz, maxHz);
    }

    private FddPeak findPeakByBinIndex(int binIndex) {
        for (FddPeak p : peaks) {
            if (p.binIndex() == binIndex) {
                return p;
            }
        }
        return null;
    }
 
    private void updateRangeLabelPreview() {
        Double a = parseDoubleOrNull(minHzField.getText());
        Double b = parseDoubleOrNull(maxHzField.getText());
        if (a != null && b != null && !(b > a)) {
            rangeLabel.setText("Frequency range: invalid (min must be < max)");
            rangeLabel.setForeground(new Color(160, 40, 40));
            return;
        }
        rangeLabel.setForeground(Color.DARK_GRAY);
        if (a == null && b == null) {
            rangeLabel.setText("Frequency range: full");
        } else {
            String lo = a == null ? "0" : formatHz(a);
            String hi = b == null ? "max" : formatHz(b);
            rangeLabel.setText("Frequency range: " + lo + " Hz to " + hi + " Hz");
        }
    }
 
    private void updateRangeLabel() {
        rangeLabel.setForeground(Color.DARK_GRAY);
        if (minHz == null && maxHz == null) {
            rangeLabel.setText("Frequency range: full");
        } else {
            String lo = minHz == null ? "0" : formatHz(minHz);
            String hi = maxHz == null ? "max" : formatHz(maxHz);
            rangeLabel.setText("Frequency range: " + lo + " Hz to " + hi + " Hz");
        }
    }
 
    private void showError(String msg) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Toast.show(w, msg, new Color(160, 40, 40), 2200);
    }
 
    private static Double parseDoubleOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return null;
        }
        try {
            double v = Double.parseDouble(t);
            if (!Double.isFinite(v)) {
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
    }
 
    private static String formatHz(double hz) {
        if (hz >= 100.0) {
            return String.format("%.0f", hz);
        }
        if (hz >= 10.0) {
            return String.format("%.1f", hz);
        }
        if (hz >= 1.0) {
            return String.format("%.2f", hz);
        }
        return String.format("%.3f", hz);
    }
}

