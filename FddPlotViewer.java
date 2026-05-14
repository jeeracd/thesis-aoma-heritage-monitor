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
        plot.setResult(result);
        applyRangeToPlot();
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
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 6, 8));
 
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rangeLabel.setForeground(Color.DARK_GRAY);
 
        JLabel minLbl = new JLabel("Min Hz:");
        minLbl.setFont(new Font("Arial", Font.PLAIN, 12));
 
        JLabel maxLbl = new JLabel("Max Hz:");
        maxLbl.setFont(new Font("Arial", Font.PLAIN, 12));
 
        minHzField.setMaximumSize(new Dimension(90, 26));
        maxHzField.setMaximumSize(new Dimension(90, 26));
 
        GridBagConstraints c0 = new GridBagConstraints();
        c0.gridx = 0;
        c0.gridy = 0;
        c0.gridwidth = 7;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.HORIZONTAL;
        c0.insets = new Insets(0, 0, 6, 0);
        p.add(rangeLabel, c0);
 
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.insets = new Insets(0, 6, 0, 6);
        c.anchor = GridBagConstraints.WEST;
 
        int x = 0;
        c.gridx = x++;
        p.add(minLbl, c);
        c.gridx = x++;
        p.add(minHzField, c);
        c.gridx = x++;
        p.add(maxLbl, c);
        c.gridx = x++;
        p.add(maxHzField, c);
        c.gridx = x++;
        p.add(applyBtn, c);
        c.gridx = x++;
        p.add(resetBtn, c);
 
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = x;
        filler.gridy = 1;
        filler.weightx = 1.0;
        filler.fill = GridBagConstraints.HORIZONTAL;
        p.add(Box.createHorizontalStrut(1), filler);
 
        return p;
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

