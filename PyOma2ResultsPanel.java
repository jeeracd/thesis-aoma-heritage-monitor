import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

public final class PyOma2ResultsPanel extends JPanel {
    public interface Runner {
        PyOma2Runner.RunResult run(File csvFile, Path outDir, Double fsHz);
    }

    private final Frame owner;
    private final Runner runner;
    private final JTextField fsField = new JTextField();
    private final JButton runButton = new JButton("Run PyOMA2");
    private final JButton openFolderButton = new JButton("Open Output Folder");
    private final JLabel statusLabel = new JLabel("");

    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel stabilizationLabel = new JLabel("", SwingConstants.CENTER);
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

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.DARK_GRAY);
        top.add(statusLabel, gbc);

        add(top, BorderLayout.NORTH);

        tabs.addTab("Stabilization", wrap(stabilizationLabel));
        tabs.addTab("FRF (CMIF)", wrap(cmifLabel));
        tabs.addTab("Mode Shapes", wrap(modeShapesLabel));
        tabs.addTab("MAC", wrap(macLabel));
        add(tabs, BorderLayout.CENTER);

        openFolderButton.setEnabled(false);

        runButton.addActionListener(e -> run());
        openFolderButton.addActionListener(e -> openOutputFolder());

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

    public void setSourceCsv(File csv) {
        this.sourceCsv = csv;
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
        setStatus("Running PyOMA2...", false);

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
                        setStatus(result.message(), true);
                        if (owner != null) {
                            Toast.show(owner, "PyOMA2 failed", new Color(160, 40, 40), 2200);
                        }
                        return;
                    }
                    setStatus(result.message(), false);
                    if (owner != null) {
                        Toast.show(owner, "PyOMA2 results ready", new Color(0, 128, 0), 1600);
                    }
                    openFolderButton.setEnabled(true);
                    applySummary(result.summary());
                } catch (Exception ex) {
                    setStatus(ex.getMessage() == null ? "PyOMA2 failed." : ex.getMessage(), true);
                } finally {
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
        setImageFromPath(stabilizationLabel, props.getProperty("stabilization_png"));
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

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg == null ? "" : msg);
        statusLabel.setForeground(error ? new Color(160, 40, 40) : Color.DARK_GRAY);
    }
}

