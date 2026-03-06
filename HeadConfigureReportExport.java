import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HeadConfigureReportExport extends JFrame {

    public HeadConfigureReportExport() {
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

        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.PLAIN, 14);

        JLabel sessionLabel = new JLabel("Select Monitoring Session:");
        sessionLabel.setFont(labelFont);

        String[] activeSessions = {
                "#20260224-OMA-005",
        };

        JComboBox<String> sessionCombo = new JComboBox<>(activeSessions);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(sessionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        mainPanel.add(sessionCombo, gbc);

        JLabel reportTypeLabel = new JLabel("Select ONE report Type:");
        reportTypeLabel.setFont(labelFont);

        String[] reportTypes = {
                "Executive Summary (Non-Technical)",
                "Full Technical Audit (Standard)"
        };

        JComboBox<String> reportTypeCombo = new JComboBox<>(reportTypes);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(reportTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        mainPanel.add(reportTypeCombo, gbc);

        JLabel includeLabel = new JLabel("Include Data:");
        includeLabel.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(includeLabel, gbc);

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        JCheckBox nscpCheck = new JCheckBox("NSCP Safety Compliance");
        JCheckBox macCheck = new JCheckBox("MAC Value / Reliability Score");
        JCheckBox vibrationCheck = new JCheckBox("Raw Vibration Logs (Spectrograms)");
        JCheckBox omaCheck = new JCheckBox("OMA Analysis Report");

        reportTypeCombo.addActionListener(e -> {
        String selected = (String) reportTypeCombo.getSelectedItem();

        if (selected.equals("Executive Summary (Non-Technical)")) {

            nscpCheck.setEnabled(true);
            nscpCheck.setSelected(true);

            macCheck.setEnabled(false);
            macCheck.setSelected(false);

            vibrationCheck.setEnabled(false);
            vibrationCheck.setSelected(false);

            omaCheck.setEnabled(true);
            omaCheck.setSelected(true);

        } else {

            nscpCheck.setEnabled(true);
            macCheck.setEnabled(true);
            vibrationCheck.setEnabled(true);
            omaCheck.setEnabled(true);

            nscpCheck.setSelected(true);
            macCheck.setSelected(true);
            vibrationCheck.setSelected(true);
            omaCheck.setSelected(true);
        }
    });

        reportTypeCombo.setSelectedIndex(0);

        checkboxPanel.add(nscpCheck);
        checkboxPanel.add(macCheck);
        checkboxPanel.add(vibrationCheck);
        checkboxPanel.add(omaCheck);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(checkboxPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10,0,20,0));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.RED);

        JButton generateButton = new JButton("Generate & Download");
        generateButton.setForeground(new Color(0,120,0));

        generateButton.addActionListener(e -> {
        String selectedReport = (String) reportTypeCombo.getSelectedItem();
        String selectedSession = (String) sessionCombo.getSelectedItem();

        JOptionPane.showMessageDialog(this,"The " + selectedReport + " with a Monitoring Session of (" 
                + selectedSession + ") has been generated and downloaded!",
                "The file has been generated and downloaded!",
                JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            new HeadExportSensorData();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(generateButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new HeadConfigureReportExport();
    }
}