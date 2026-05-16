import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EngineerConfigureReportExport extends JFrame {

    private static final Path RESULTS_ROOT =
            Path.of(System.getProperty("user.home"), ".aoma-heritage-monitor", "pyoma2-results");
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");

    private static final class SessionItem {
        final Path dir;
        final String label;

        SessionItem(Path dir) {
            this.dir = dir;
            this.label = formatLabel(dir);
        }

        private static String formatLabel(Path dir) {
            String name = dir.getFileName().toString();
            try {
                long ms = Long.parseLong(name);
                LocalDateTime dt = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(ms), ZoneId.systemDefault());
                return DISPLAY_FMT.format(dt) + "  [" + name + "]";
            } catch (NumberFormatException e) {
                return name;
            }
        }

        @Override
        public String toString() { return label; }
    }

    public EngineerConfigureReportExport() {
        setTitle("AOMA-Heritage Monitor - Configure Report Export");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Configure Report Export", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.PLAIN, 14);

        JLabel sessionLabel = new JLabel("Select Monitoring Session:");
        sessionLabel.setFont(labelFont);

        JComboBox<SessionItem> sessionCombo = new JComboBox<>();
        for (SessionItem item : loadSessions()) {
            sessionCombo.addItem(item);
        }
        if (sessionCombo.getItemCount() == 0) {
            sessionCombo.setEnabled(false);
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        mainPanel.add(sessionLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        mainPanel.add(sessionCombo, gbc);

        JLabel reportTypeLabel = new JLabel("Select ONE report Type:");
        reportTypeLabel.setFont(labelFont);

        String[] reportTypes = {
                "Executive Summary (Non-Technical)",
                "Full Technical Audit (Standard)"
        };
        JComboBox<String> reportTypeCombo = new JComboBox<>(reportTypes);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        mainPanel.add(reportTypeLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        mainPanel.add(reportTypeCombo, gbc);

        JLabel includeLabel = new JLabel("Include Data:");
        includeLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(includeLabel, gbc);

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        JCheckBox nscpCheck = new JCheckBox("NSCP Safety Compliance");
        JCheckBox macCheck = new JCheckBox("MAC Value / Reliability Score");
        JCheckBox vibrationCheck = new JCheckBox("Raw Vibration Logs (Spectrograms)");
        JCheckBox omaCheck = new JCheckBox("OMA Analysis Report");

        reportTypeCombo.addActionListener(e -> {
            String selected = (String) reportTypeCombo.getSelectedItem();
            if ("Executive Summary (Non-Technical)".equals(selected)) {
                nscpCheck.setEnabled(true);  nscpCheck.setSelected(true);
                macCheck.setEnabled(false);  macCheck.setSelected(false);
                vibrationCheck.setEnabled(false); vibrationCheck.setSelected(false);
                omaCheck.setEnabled(true);   omaCheck.setSelected(true);
            } else {
                nscpCheck.setEnabled(true);  nscpCheck.setSelected(true);
                macCheck.setEnabled(true);   macCheck.setSelected(true);
                vibrationCheck.setEnabled(true); vibrationCheck.setSelected(true);
                omaCheck.setEnabled(true);   omaCheck.setSelected(true);
            }
        });
        reportTypeCombo.setSelectedIndex(0);

        checkboxPanel.add(nscpCheck);
        checkboxPanel.add(macCheck);
        checkboxPanel.add(vibrationCheck);
        checkboxPanel.add(omaCheck);

        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(checkboxPanel, gbc);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.RED);

        JButton generateButton = new JButton("Generate & Download");
        generateButton.setForeground(new Color(0, 120, 0));

        generateButton.addActionListener(e -> {
            SessionItem selected = (SessionItem) sessionCombo.getSelectedItem();
            if (selected == null) {
                statusLabel.setForeground(new Color(160, 40, 40));
                statusLabel.setText("No session available. Run an OMA analysis first.");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save PDF Report");
            chooser.setSelectedFile(new File("aoma_report.pdf"));
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "PDF Files (*.pdf)", "pdf"));
            int choice = chooser.showSaveDialog(this);
            if (choice != JFileChooser.APPROVE_OPTION) return;
            File dest = chooser.getSelectedFile();
            if (!dest.getName().toLowerCase().endsWith(".pdf")) {
                dest = new File(dest.getAbsolutePath() + ".pdf");
            }
            final File finalDest = dest;
            generateButton.setEnabled(false);
            statusLabel.setForeground(Color.DARK_GRAY);
            statusLabel.setText("Generating report…");
            new SwingWorker<File, Void>() {
                @Override
                protected File doInBackground() throws Exception {
                    return PdfReportGenerator.generate(selected.dir);
                }
                @Override
                protected void done() {
                    generateButton.setEnabled(true);
                    try {
                        File pdf = get();
                        Files.copy(pdf.toPath(), finalDest.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                        statusLabel.setForeground(new Color(21, 128, 61));
                        statusLabel.setText("Saved: " + finalDest.getAbsolutePath());
                        JOptionPane.showMessageDialog(
                                EngineerConfigureReportExport.this,
                                "Report saved to:\n" + finalDest.getAbsolutePath(),
                                "Report Generated",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new EngineerExportSensorData();
                    } catch (Exception ex) {
                        statusLabel.setForeground(new Color(160, 40, 40));
                        statusLabel.setText("Failed: " + ex.getMessage());
                    }
                }
            }.execute();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(generateButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private static List<SessionItem> loadSessions() {
        List<SessionItem> items = new ArrayList<>();
        if (!Files.isDirectory(RESULTS_ROOT)) return items;
        try {
            Files.list(RESULTS_ROOT)
                    .filter(Files::isDirectory)
                    .sorted(Comparator.<Path>comparingLong(p -> {
                        try { return Long.parseLong(p.getFileName().toString()); }
                        catch (NumberFormatException e) { return 0L; }
                    }).reversed())
                    .limit(10)
                    .forEach(p -> items.add(new SessionItem(p)));
        } catch (Exception ignored) {}
        return items;
    }

    public static void main(String[] args) {
        new EngineerConfigureReportExport();
    }
}
