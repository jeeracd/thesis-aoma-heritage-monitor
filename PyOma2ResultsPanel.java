import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PyOma2ResultsPanel extends JPanel {
    public interface Runner {
        PyOma2Runner.RunResult run(File csvFile, Path outDir, Double fsHz);
    }

    public interface OnRunComplete {
        void onSuccess(Path outDir);
    }

    private static final Color SUCCESS_COLOR = new Color(21, 128, 61);

    private static final Color BADGE_PASS   = new Color(21, 128, 61);
    private static final Color BADGE_WARN   = new Color(161, 98, 7);
    private static final Color BADGE_FAIL   = new Color(160, 40, 40);
    private static final Color BADGE_BLUE   = new Color(29, 78, 216);
    private static final Color BADGE_GRAY   = Color.DARK_GRAY;

    private final Frame owner;
    private final Runner runner;
    private final JTextField fsField = new JTextField();
    private final JButton runButton = new JButton("Run PyOMA2");
    private final JButton openFolderButton = new JButton("Open Output Folder");
    private final JButton generateReportButton = new JButton("Generate Report");
    private final JLabel statusLabel = new JLabel("");
    private final JProgressBar spinner = new JProgressBar();
    private final JLabel complianceBadge = new JLabel("", SwingConstants.LEFT);

    private OnRunComplete onRunComplete;

    private final JTabbedPane tabs = new JTabbedPane();
    private final StabilizationScatterPanel scatterPanel = new StabilizationScatterPanel();
    private final JLabel cmifLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel modeShapesLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel macLabel = new JLabel("", SwingConstants.CENTER);

    private File sourceCsv;
    private Path lastOutDir;
    private boolean running;
    private Runnable removeCsvListener = () -> {};

    public PyOma2ResultsPanel(Frame owner) {
        this(owner, PyOma2Runner::run);
    }

    public PyOma2ResultsPanel(Frame owner, Runner runner) {
        this.owner = owner;
        this.runner = runner == null ? PyOma2Runner::run : runner;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel fsLbl = new JLabel("fs (Hz):");
        fsLbl.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(fsLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fsField.setFont(new Font("Arial", Font.PLAIN, 12));
        top.add(fsField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        runButton.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(runButton, gbc);

        gbc.gridx = 3;
        openFolderButton.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(openFolderButton, gbc);

        gbc.gridx = 4;
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateReportButton.setForeground(new Color(0, 100, 180));
        top.add(generateReportButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.DARK_GRAY);
        top.add(statusLabel, gbc);

        gbc.gridy = 2;
        spinner.setIndeterminate(true);
        spinner.setPreferredSize(new Dimension(0, 4));
        spinner.setVisible(false);
        top.add(spinner, gbc);

        gbc.gridy = 3;
        complianceBadge.setFont(new Font("Arial", Font.BOLD, 12));
        complianceBadge.setForeground(BADGE_GRAY);
        top.add(complianceBadge, gbc);

        add(top, BorderLayout.NORTH);

        tabs.addTab("Stabilization", scatterPanel);
        tabs.addTab("FRF (CMIF)", wrap(cmifLabel));
        tabs.addTab("Mode Shapes", wrap(modeShapesLabel));
        tabs.addTab("MAC", wrap(macLabel));
        add(tabs, BorderLayout.CENTER);

        openFolderButton.setEnabled(false);
        generateReportButton.setEnabled(false);

        runButton.addActionListener(e -> run());
        openFolderButton.addActionListener(e -> openOutputFolder());
        generateReportButton.addActionListener(e -> openReportExport());

        removeCsvListener = AppSession.addLastUploadedCsvListener(() -> SwingUtilities.invokeLater(() -> {
            File csv = AppSession.getLastUploadedCsv();
            if (csv != null) {
                setSourceCsv(csv);
            } else {
                setSourceCsv(null);
            }
        }));
        File initial = AppSession.getLastUploadedCsv();
        if (initial != null) {
            setSourceCsv(initial);
        }
    }

    public void setOnRunComplete(OnRunComplete cb) {
        this.onRunComplete = cb;
    }

    public void setSourceCsv(File csv) {
        this.sourceCsv = csv;
        CsvFileValidator.CsvProfile profile = AppSession.getLastUploadedCsvProfile();
        boolean kpi = profile == CsvFileValidator.CsvProfile.KPI_LOG;
        fsField.setEnabled(!kpi);
        runButton.setEnabled(!kpi);
        if (kpi) {
            setStatus("KPI log CSV detected. OMA run is skipped; use CAD Results → Raw CSV/QA/Time Series/Timeline.", false);
        } else {
            setStatus("", false);
        }
        if (csv == null) {
            scatterPanel.clear();
        }
        maybeAutoRun();
    }

    @Override
    public void removeNotify() {
        try {
            removeCsvListener.run();
        } catch (Exception ignored) {
        }
        removeCsvListener = () -> {};
        super.removeNotify();
    }

    private JScrollPane wrap(JLabel label) {
        label.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane sp = new JScrollPane(label);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void run() {
        if (running) {
            return;
        }
        File csv = sourceCsv;
        if (csv == null) {
            setStatus("No CSV selected.", true);
            return;
        }
        Double fs = parseFs();
        Path outDir = Path.of(System.getProperty("user.home"), ".aoma-heritage-monitor", "pyoma2-results", String.valueOf(System.currentTimeMillis()));
        lastOutDir = outDir;
        running = true;
        AppSession.markPyOma2RunStartedForCurrentCsv();

        runButton.setEnabled(false);
        openFolderButton.setEnabled(false);
        generateReportButton.setEnabled(false);
        spinner.setVisible(true);
        setStatus("Running PyOMA2 analysis… this may take a moment.", false);

        SwingWorker<PyOma2Runner.RunResult, Void> worker = new SwingWorker<>() {
            @Override
            protected PyOma2Runner.RunResult doInBackground() {
                return runner.run(csv, outDir, fs);
            }

            @Override
            protected void done() {
                try {
                    PyOma2Runner.RunResult result = get();
                    if (!result.ok()) {
                        String msg = result.message();
                        if (msg == null || msg.isBlank()) {
                            msg = "PyOMA2 pipeline failed with no error detail. Check that Python is installed and pyoma2_oma_results.py is in the working directory.";
                        }
                        setStatus(msg, true);
                        if (owner != null) {
                            Toast.show(owner, "PyOMA2 failed", new Color(160, 40, 40), 2200);
                        }
                        return;
                    }
                    setStatusSuccess("Analysis complete. Results loaded.");
                    if (owner != null) {
                        Toast.show(owner, "PyOMA2 results ready", new Color(0, 128, 0), 1600);
                    }
                    openFolderButton.setEnabled(true);
                    generateReportButton.setEnabled(true);
                    applySummary(result.summary());
                    if (onRunComplete != null) {
                        onRunComplete.onSuccess(lastOutDir);
                    }
                    PyOma2ResultsPanel.this.runCompliance(lastOutDir);
                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    setStatus(msg == null ? "PyOMA2 failed. Check the console for details." : msg, true);
                } finally {
                    spinner.setVisible(false);
                    runButton.setEnabled(true);
                    running = false;
                }
            }
        };
        worker.execute();
    }

    private void maybeAutoRun() {
        File csv = sourceCsv;
        if (csv == null) {
            return;
        }
        CsvFileValidator.CsvProfile profile = AppSession.getLastUploadedCsvProfile();
        if (profile == CsvFileValidator.CsvProfile.KPI_LOG) {
            long importSeq = AppSession.getLastUploadedCsvSequence();
            long runSeq = AppSession.getLastPyOma2RunSequence();
            if (runSeq >= importSeq) {
                return;
            }
            if (!csv.equals(AppSession.getLastUploadedCsv())) {
                return;
            }
            Path outDir = Path.of(System.getProperty("user.home"), ".aoma-heritage-monitor", "pyoma2-results", String.valueOf(System.currentTimeMillis()));
            lastOutDir = outDir;
            AppSession.markPyOma2RunStartedForCurrentCsv();
            PyOma2Runner.RunResult r = PyOma2Runner.writeKpiOnlyResults(csv, outDir);
            setStatus(r.message(), !r.ok());
            if (r.ok()) {
                openFolderButton.setEnabled(true);
                applySummary(r.summary());
            }
            return;
        }
        long importSeq = AppSession.getLastUploadedCsvSequence();
        long runSeq = AppSession.getLastPyOma2RunSequence();
        if (runSeq >= importSeq) {
            return;
        }
        if (!csv.equals(AppSession.getLastUploadedCsv())) {
            return;
        }
        run();
    }

    private void applySummary(Properties props) {
        String polesCsv = props.getProperty("stabilization_poles_csv");
        String modalCsv = props.getProperty("modal_properties_csv");
        scatterPanel.loadFromPaths(polesCsv, modalCsv);
        setImageFromPath(cmifLabel, props.getProperty("cmif_png"));
        setImageFromPath(modeShapesLabel, props.getProperty("mode_shapes_png"));
        setImageFromPath(macLabel, props.getProperty("mac_png"));
    }

    private void setImageFromPath(JLabel label, String path) {
        if (path == null || path.isBlank()) {
            label.setIcon(null);
            label.setText("No image.");
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            label.setIcon(null);
            label.setText("Missing: " + f.getName());
            return;
        }
        ImageIcon icon = new ImageIcon(path);
        int maxW = Math.max(400, getWidth() - 40);
        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            int w = Math.min(icon.getIconWidth(), maxW);
            int h = (int) Math.round(icon.getIconHeight() * (w / (double) icon.getIconWidth()));
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setText("");
        } else {
            label.setIcon(icon);
            label.setText("");
        }
    }

    private void openOutputFolder() {
        Path dir = lastOutDir;
        if (dir == null) {
            setStatus("Output folder is not available.", true);
            return;
        }
        try {
            Desktop.getDesktop().open(dir.toFile());
        } catch (Exception ex) {
            setStatus("Failed to open output folder.", true);
        }
    }

    private Double parseFs() {
        String s = fsField.getText();
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            double v = Double.parseDouble(s.trim());
            if (v <= 0) {
                return null;
            }
            return v;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void openReportExport() {
        RoleMenuBar.Role role = AppSession.getActiveRole();
        if (role == RoleMenuBar.Role.HEAD) {
            new HeadConfigureReportExport();
        } else {
            new EngineerConfigureReportExport();
        }
    }

    private void runCompliance(Path outDir) {
        if (outDir == null) return;
        complianceBadge.setText("Compliance: checking…");
        complianceBadge.setForeground(BADGE_GRAY);
        new SwingWorker<ComplianceEngine.Status, Void>() {
            @Override
            protected ComplianceEngine.Status doInBackground() throws Exception {
                Path baseline = Path.of(System.getProperty("user.home"),
                        ".aoma-heritage-monitor", "oma-baseline.csv");
                boolean setBaseline = !Files.exists(baseline);
                return ComplianceEngine.apply(outDir, baseline, setBaseline);
            }
            @Override
            protected void done() {
                try {
                    ComplianceEngine.Status status = get();
                    String label;
                    Color color;
                    switch (status) {
                        case PASS:         label = "Compliance: PASS";         color = BADGE_PASS; break;
                        case WARN:         label = "Compliance: WARN";         color = BADGE_WARN; break;
                        case FAIL:         label = "Compliance: FAIL";         color = BADGE_FAIL; break;
                        case BASELINE_SET: label = "Compliance: BASELINE SET"; color = BADGE_BLUE; break;
                        case NO_MATCH:     label = "Compliance: NO MATCH";     color = BADGE_GRAY; break;
                        default:           label = "Compliance: NO BASELINE";  color = BADGE_GRAY; break;
                    }
                    complianceBadge.setText(label);
                    complianceBadge.setForeground(color);
                } catch (Exception ex) {
                    complianceBadge.setText("Compliance: error");
                    complianceBadge.setForeground(BADGE_GRAY);
                }
            }
        }.execute();
    }

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg == null ? "" : msg);
        statusLabel.setForeground(error ? new Color(160, 40, 40) : Color.DARK_GRAY);
    }

    private void setStatusSuccess(String msg) {
        statusLabel.setText(msg == null ? "" : msg);
        statusLabel.setForeground(SUCCESS_COLOR);
    }
}

