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
    private final JCheckBox selectAll = new JCheckBox("Select all");
    private final JSpinner decimalsSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 9, 1));
    private final JComboBox<String> scaleCombo = new JComboBox<>(new String[]{"dB (log)", "Linear"});
    private final JButton exportSelectedBtn = new JButton("Export selected...");
    private final JButton exportWindowBtn = new JButton("Export window...");
    private final JButton paramsBtn = new JButton("Parameters...");
    private final JButton resetWindowBtn = new JButton("Full view");

    private SpectrogramViewWindow currentWindow = new SpectrogramViewWindow(0, 0, 0, 0);
    private volatile File sourceCsv;
    private volatile boolean updatingSelectAll;

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
        sp.getViewport().setBackground(Color.WHITE);
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
        table.clearSelection();
        applyFilters();
        updateWindowLabel();
        updateSelectAllState();
    }
 
    public void setSourceCsv(File csv) {
        this.sourceCsv = csv;
    }
 
    JCheckBox getSelectAllForTesting() {
        return selectAll;
    }
 
    JTable getTableForTesting() {
        return table;
    }

    @Override
    public void onViewWindowChanged(SpectrogramViewWindow window) {
        if (window == null) {
            return;
        }
        this.currentWindow = window;
        model.setViewWindow(window);
        table.clearSelection();
        updateWindowLabel();
        updateSelectAllState();
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

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

        minField.setMaximumSize(new Dimension(80, 26));
        maxField.setMaximumSize(new Dimension(80, 26));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        decimalsSpinner.setMaximumSize(new Dimension(70, 26));
        scaleCombo.setMaximumSize(new Dimension(110, 26));

        Insets inTop = new Insets(2, 6, 2, 6);
        Insets inRow = new Insets(2, 6, 2, 6);

        GridBagConstraints c0 = new GridBagConstraints();
        c0.gridx = 0;
        c0.gridy = 0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.HORIZONTAL;
        c0.insets = inTop;
        p.add(windowLabel, c0);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 1;
        c1.gridy = 0;
        c1.weightx = 0;
        c1.anchor = GridBagConstraints.EAST;
        c1.insets = inTop;
        p.add(resetWindowBtn, c1);

        int x = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.insets = inRow;
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
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(searchField, c);

        x = 0;
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridy = 2;
        c2.insets = inRow;
        c2.anchor = GridBagConstraints.WEST;

        c2.gridx = x++;
        p.add(decLbl, c2);
        c2.gridx = x++;
        p.add(decimalsSpinner, c2);
        c2.gridx = x++;
        p.add(scaleCombo, c2);
        c2.gridx = x++;
        p.add(selectAll, c2);
        c2.gridx = x++;
        c2.anchor = GridBagConstraints.EAST;
        p.add(exportSelectedBtn, c2);
        c2.gridx = x++;
        p.add(exportWindowBtn, c2);
        c2.gridx = x++;
        p.add(paramsBtn, c2);

        return p;
    }

    private void configureTable() {
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setBackground(Color.WHITE);

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setMinWidth(110);
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

        selectAll.addActionListener(e -> toggleSelectAll());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectAllState();
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
        paramsBtn.addActionListener(e -> generateAndShowParameters());
    }

    private void applyFilters() {
        Double min = parseDoubleOrNull(minField.getText());
        Double max = parseDoubleOrNull(maxField.getText());
        model.setValueFilter(min, max);
        model.setAnomaliesOnly(anomaliesOnly.isSelected());
        model.setSearchText(searchField.getText());
        updateWindowLabel();
        updateSelectAllState();
    }
 
    private void toggleSelectAll() {
        if (updatingSelectAll) {
            return;
        }
        updatingSelectAll = true;
        try {
            if (selectAll.isSelected()) {
                int n = table.getRowCount();
                if (n > 0) {
                    table.setRowSelectionInterval(0, n - 1);
                }
            } else {
                table.clearSelection();
            }
        } finally {
            updatingSelectAll = false;
            updateSelectAllState();
        }
    }
 
    private void updateSelectAllState() {
        if (updatingSelectAll) {
            return;
        }
        int n = table.getRowCount();
        int sel = table.getSelectedRowCount();
        selectAll.setEnabled(n > 0);
        updatingSelectAll = true;
        try {
            selectAll.setSelected(n > 0 && sel == n);
        } finally {
            updatingSelectAll = false;
        }
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
        File csv = sourceCsv != null ? sourceCsv : AppSession.getLastUploadedCsv();
        CsvModalParameters mp = null;
        if (csv != null) {
            try {
                mp = CsvModalParametersGenerator.generate(csv, 2000);
            } catch (Exception ex) {
                Toast.show(w, "Exporting without modal parameters", new Color(80, 80, 80), 1800);
                mp = null;
            }
        }
        try {
            SpectrogramCsvExport.writeRows(target, model, modelRows, mp);
            Toast.show(w, "Exported successfully", new Color(0, 128, 0), 1800);
        } catch (Exception ex) {
            Toast.show(w, "Export failed", new Color(160, 40, 40), 2200);
        }
    }
 
    private void generateAndShowParameters() {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame f ? f : null;
        File csv = sourceCsv != null ? sourceCsv : AppSession.getLastUploadedCsv();
        CsvFileValidator.ValidationResult vr = CsvFileValidator.validate(csv);
        if (!vr.valid()) {
            Toast.show(w, vr.message(), new Color(160, 40, 40), 2400);
            return;
        }
 
        paramsBtn.setEnabled(false);
        Toast.show(w, "Generating parameters...", new Color(60, 60, 60), 1200);
 
        SwingWorker<CsvModalParameters, Void> worker = new SwingWorker<>() {
            @Override
            protected CsvModalParameters doInBackground() throws Exception {
                return CsvModalParametersGenerator.generate(csv);
            }
 
            @Override
            protected void done() {
                paramsBtn.setEnabled(true);
                try {
                    CsvModalParameters p = get();
                    Toast.show(w, "Modal parameters generated", new Color(0, 128, 0), 1600);
                    CsvModalParametersDialog dlg = new CsvModalParametersDialog(owner, csv, p);
                    dlg.setVisible(true);
                } catch (Exception ex) {
                    Toast.show(w, "Failed to generate parameters", new Color(160, 40, 40), 2400);
                    JOptionPane.showMessageDialog(
                            SpectrogramDataTableViewer.this,
                            ex.getMessage() == null ? "Unable to parse CSV." : ex.getMessage(),
                            "CSV Parsing Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
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

