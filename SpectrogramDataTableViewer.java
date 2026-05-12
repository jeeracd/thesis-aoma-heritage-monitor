import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;

public final class SpectrogramDataTableViewer extends JPanel implements SpectrogramViewWindowListener {
    private final SpectrogramTableModel model = new SpectrogramTableModel();
    private final JTable table = new JTable(model);

    private final JLabel windowLabel = new JLabel("");
    private final JTextField minField = new JTextField(6);
    private final JTextField maxField = new JTextField(6);
    private final JTextField searchField = new JTextField(10);
    private final JCheckBox anomaliesOnly = new JCheckBox("Anomalies only");
    private final JSpinner decimalsSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 9, 1));
    private final JComboBox<String> scaleCombo = new JComboBox<>(new String[]{"dB (log)", "Linear"});
    private final JButton exportSelectedBtn = new JButton("Export selected...");
    private final JButton exportWindowBtn = new JButton("Export window...");
    private final JButton resetWindowBtn = new JButton("Full view");

    private SpectrogramViewWindow currentWindow = new SpectrogramViewWindow(0, 0, 0, 0);

    public SpectrogramDataTableViewer() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setPreferredSize(new Dimension(850, 340));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        JPanel controls = buildControls();
        add(controls, BorderLayout.NORTH);

        configureTable();
        JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setPreferredSize(new Dimension(850, 290));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        add(sp, BorderLayout.CENTER);

        wireControls();
        updateWindowLabel();
    }

    public void setSpectrogram(SpectrogramData data) {
        model.setSpectrogram(data);
        currentWindow = SpectrogramViewWindow.full(data);
        model.setViewWindow(currentWindow);
        applyFilters();
        updateWindowLabel();
    }

    @Override
    public void onViewWindowChanged(SpectrogramViewWindow window) {
        if (window == null) {
            return;
        }
        this.currentWindow = window;
        model.setViewWindow(window);
        updateWindowLabel();
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        windowLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        windowLabel.setForeground(Color.DARK_GRAY);

        JLabel filterLbl = new JLabel("dB filter:");
        filterLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel toLbl = new JLabel("to");
        toLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel searchLbl = new JLabel("Search flag:");
        searchLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel decLbl = new JLabel("Decimals:");
        decLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        Insets in = new Insets(2, 6, 2, 6);
        int x = 0;

        GridBagConstraints c0 = new GridBagConstraints();
        c0.gridx = 0;
        c0.gridy = 0;
        c0.gridwidth = 9;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.HORIZONTAL;
        c0.insets = new Insets(4, 6, 2, 6);
        p.add(windowLabel, c0);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 9;
        c1.gridy = 0;
        c1.weightx = 0;
        c1.insets = new Insets(4, 6, 2, 6);
        p.add(resetWindowBtn, c1);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.insets = in;
        c.anchor = GridBagConstraints.WEST;

        c.gridx = x++;
        p.add(filterLbl, c);
        c.gridx = x++;
        p.add(minField, c);
        c.gridx = x++;
        p.add(toLbl, c);
        c.gridx = x++;
        p.add(maxField, c);
        c.gridx = x++;
        p.add(anomaliesOnly, c);
        c.gridx = x++;
        p.add(searchLbl, c);
        c.gridx = x++;
        p.add(searchField, c);
        c.gridx = x++;
        p.add(decLbl, c);
        c.gridx = x++;
        p.add(decimalsSpinner, c);
        c.gridx = x++;
        p.add(scaleCombo, c);
        c.gridx = x++;
        p.add(exportSelectedBtn, c);
        c.gridx = x++;
        p.add(exportWindowBtn, c);

        return p;
    }

    private void configureTable() {
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
    }

    private void wireControls() {
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        minField.getDocument().addDocumentListener(dl);
        maxField.getDocument().addDocumentListener(dl);
        searchField.getDocument().addDocumentListener(dl);

        anomaliesOnly.addActionListener(e -> applyFilters());
        decimalsSpinner.addChangeListener(e -> model.setDecimals((Integer) decimalsSpinner.getValue()));
        scaleCombo.addActionListener(e -> {
            if (scaleCombo.getSelectedIndex() == 0) {
                model.setAmplitudeScale(SpectrogramTableModel.AmplitudeScale.DB);
            } else {
                model.setAmplitudeScale(SpectrogramTableModel.AmplitudeScale.LINEAR);
            }
        });

        resetWindowBtn.addActionListener(e -> {
            SpectrogramData d = model.getSpectrogram();
            if (d != null) {
                onViewWindowChanged(SpectrogramViewWindow.full(d));
            }
        });

        exportSelectedBtn.addActionListener(e -> exportSelected());
        exportWindowBtn.addActionListener(e -> exportWindow());
    }

    private void applyFilters() {
        Double min = parseDoubleOrNull(minField.getText());
        Double max = parseDoubleOrNull(maxField.getText());
        model.setValueFilter(min, max);
        model.setAnomaliesOnly(anomaliesOnly.isSelected());
        model.setSearchText(searchField.getText());
        updateWindowLabel();
    }

    private void exportSelected() {
        if (model.getSpectrogram() == null) {
            Toast.show(SwingUtilities.getWindowAncestor(this), "No data to export", new Color(160, 40, 40), 2200);
            return;
        }
        int[] viewRows = table.getSelectedRows();
        if (viewRows == null || viewRows.length == 0) {
            Toast.show(SwingUtilities.getWindowAncestor(this), "No rows selected", new Color(160, 40, 40), 2200);
            return;
        }
        int[] modelRows = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; i++) {
            modelRows[i] = table.convertRowIndexToModel(viewRows[i]);
        }
        exportRows(modelRows);
    }

    private void exportWindow() {
        if (model.getSpectrogram() == null) {
            Toast.show(SwingUtilities.getWindowAncestor(this), "No data to export", new Color(160, 40, 40), 2200);
            return;
        }
        int n = model.getRowCount();
        int[] rows = new int[n];
        for (int i = 0; i < n; i++) {
            rows[i] = i;
        }
        exportRows(rows);
    }

    private void exportRows(int[] modelRows) {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame f ? f : null;
        File target = NativeFilePicker.pickSaveCsvFile(owner, "Export Spectral Data", "spectrogram_data.csv");
        if (target == null) {
            Toast.show(w, "Export canceled", new Color(80, 80, 80), 1600);
            return;
        }
        try {
            SpectrogramCsvExport.writeRows(target, model, modelRows);
            Toast.show(w, "Exported successfully", new Color(0, 128, 0), 1800);
        } catch (Exception ex) {
            Toast.show(w, "Export failed", new Color(160, 40, 40), 2200);
        }
    }

    private void updateWindowLabel() {
        SpectrogramData d = model.getSpectrogram();
        if (d == null) {
            windowLabel.setText("Spectral table");
            return;
        }
        double t0 = currentWindow.timeStartSec();
        double t1 = currentWindow.timeEndSec();
        double f0 = currentWindow.freqStartHz();
        double f1 = currentWindow.freqEndHz();
        windowLabel.setText(String.format("Window: %.2fs–%.2fs, %.1fHz–%.1fHz", t0, t1, f0, f1));
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
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

