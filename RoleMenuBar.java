import javax.swing.*;
import java.awt.*;

public final class RoleMenuBar {
    public enum Role {
        ENGINEER,
        OFFICER,
        HEAD
    }

    private RoleMenuBar() {}

    public static void install(JFrame frame, Role role) {
        if (frame == null || role == null) {
            return;
        }
        AppWindowManager.register(frame, role);
        frame.setJMenuBar(create(frame, role));
    }

    private static JMenuBar create(JFrame frame, Role role) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.LIGHT_GRAY);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JMenu projects = new JMenu("Projects");
        projects.setFont(new Font("Arial", Font.BOLD, 15));
        JMenu view = new JMenu("View");
        view.setFont(new Font("Arial", Font.BOLD, 15));
        JMenu help = new JMenu("Help");
        help.setFont(new Font("Arial", Font.BOLD, 15));

        buildProjectsMenu(frame, role, projects);
        buildViewMenu(frame, role, view);
        buildHelpMenu(frame, role, help);

        menuBar.add(projects);
        menuBar.add(view);
        menuBar.add(help);

        menuBar.add(Box.createHorizontalGlue());

        JMenu account = new JMenu(roleLabel(role));
        account.setFont(new Font("Arial", Font.BOLD, 13));
        buildAccountMenu(frame, role, account);
        menuBar.add(account);

        return menuBar;
    }

    private static void buildProjectsMenu(JFrame frame, Role role, JMenu menu) {
        JMenuItem newProject = new JMenuItem("New Project");
        newProject.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerEditStructuralDetails::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadEditStructuralDetails::new);
            } else {
                JOptionPane.showMessageDialog(frame, "New Project is not available for this account.");
            }
        });

        JMenuItem openProject = new JMenuItem("Open Project");
        openProject.setEnabled(false);

        JMenuItem importCsv = new JMenuItem("Import Sensor Data (.csv)");
        importCsv.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerImportSensorData::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadImportSensorData::new);
            } else {
                importCsv.setEnabled(false);
            }
        });

        JMenuItem exportPdf = new JMenuItem("Export Report (PDF)");
        exportPdf.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerExportSensorData::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadExportSensorData::new);
            } else {
                exportPdf.setEnabled(false);
            }
        });

        if (role == Role.OFFICER) {
            newProject.setEnabled(false);
            importCsv.setEnabled(false);
            exportPdf.setEnabled(false);
        }

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menu.add(newProject);
        menu.add(openProject);
        menu.addSeparator();
        menu.add(importCsv);
        menu.add(exportPdf);
        menu.addSeparator();
        menu.add(exit);
    }

    private static void buildViewMenu(JFrame frame, Role role, JMenu menu) {
        JMenuItem dashboard = new JMenuItem("Dashboard View");
        dashboard.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerBldgStatusOverview::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadBldgStatusOverview::new);
            } else {
                navigate(frame, OfficerBldgStatusOverview::new);
            }
        });

        JMenuItem setupConnection = new JMenuItem("Setup & Connection");
        setupConnection.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerSetupConnectionWindow::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadSetupConnectionWindow::new);
            } else {
                navigate(frame, OfficerSetupConnectionWindow::new);
            }
        });

        JMenuItem configureSensor = new JMenuItem("Configure Sensor");
        configureSensor.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerConfigureSensorWindow::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadConfigureSensorWindow::new);
            } else {
                navigate(frame, OfficerConfigureSensorWindow::new);
            }
        });

        JMenuItem esp32Status = new JMenuItem("ESP32 Status");
        esp32Status.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerESP32StatusWindow::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadESP32StatusWindow::new);
            } else {
                navigate(frame, OfficerESP32StatusWindow::new);
            }
        });

        JMenuItem vibrationData = new JMenuItem("Vibration Data");
        vibrationData.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerVibrationDataWindow::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadVibrationDataWindow::new);
            } else {
                vibrationData.setEnabled(false);
            }
        });

        JMenuItem omaAnalysisResult = new JMenuItem("OMA Analysis Result");
        omaAnalysisResult.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerOMAAnalysisResult::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadOMAAnalysisResult::new);
            } else {
                omaAnalysisResult.setEnabled(false);
            }
        });

        JMenuItem reportHistory = new JMenuItem("View Report");
        reportHistory.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerViewReportWindow::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadViewReportWindow::new);
            } else {
                reportHistory.setEnabled(false);
            }
        });

        JMenuItem systemLogs = new JMenuItem("System Logs");
        if (role == Role.HEAD) {
            systemLogs.addActionListener(e -> navigate(frame, HeadSystemLogsWindow::new));
        } else {
            systemLogs.setEnabled(false);
        }

        if (role == Role.OFFICER) {
            vibrationData.setEnabled(false);
            omaAnalysisResult.setEnabled(false);
            reportHistory.setEnabled(false);
            systemLogs.setEnabled(false);
        }

        menu.add(dashboard);
        menu.addSeparator();
        menu.add(setupConnection);
        menu.add(configureSensor);
        menu.add(esp32Status);
        menu.addSeparator();
        menu.add(vibrationData);
        menu.add(omaAnalysisResult);
        menu.addSeparator();
        menu.add(reportHistory);
        menu.addSeparator();
        menu.add(systemLogs);
        menu.addSeparator();

        JMenuItem hiddenWindows = new JMenuItem("Hidden Windows...");
        hiddenWindows.addActionListener(e -> AppWindowManager.showHiddenWindowsDialog(frame));
        menu.add(hiddenWindows);
    }

    private static void buildHelpMenu(JFrame frame, Role role, JMenu menu) {
        JMenuItem sensorSetupGuide = new JMenuItem("Sensor Setup Guide");
        sensorSetupGuide.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerSensorSetupGuide::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadSensorSetupGuide::new);
            } else {
                navigate(frame, OfficerSensorSetupGuide::new);
            }
        });

        JMenuItem userDocumentation = new JMenuItem("User Documentation");
        userDocumentation.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerUserDocumentation::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadUserDocumentation::new);
            } else {
                navigate(frame, OfficerUserDocumentation::new);
            }
        });

        JMenuItem aboutAoma = new JMenuItem("About AOMA-Heritage Monitor");
        aboutAoma.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerAboutAOMA::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadAboutAOMA::new);
            } else {
                navigate(frame, OfficerAboutAOMA::new);
            }
        });

        JMenuItem contactSupport = new JMenuItem("Contact Support");
        contactSupport.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerContactSupport::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadContactSupport::new);
            } else {
                navigate(frame, OfficerContactSupport::new);
            }
        });

        menu.add(sensorSetupGuide);
        menu.add(userDocumentation);
        menu.addSeparator();
        menu.add(aboutAoma);
        menu.add(contactSupport);
    }

    private static void buildAccountMenu(JFrame frame, Role role, JMenu menu) {
        JMenuItem userSettings = new JMenuItem("User Settings");
        userSettings.addActionListener(e -> {
            if (role == Role.ENGINEER) {
                navigate(frame, EngineerDashboardUserSettings::new);
            } else if (role == Role.HEAD) {
                navigate(frame, HeadDashboardUserSettings::new);
            } else {
                navigate(frame, OfficerDashboardUserSettings::new);
            }
        });

        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                navigate(frame, UsersLoginOptions::new);
            }
        });

        menu.add(userSettings);
        menu.addSeparator();
        menu.add(logout);
    }

    public static void navigate(JFrame current, Runnable open) {
        SwingUtilities.invokeLater(() -> {
            try {
                open.run();
            } catch (Exception ex) {
                if (current != null) {
                    JOptionPane.showMessageDialog(
                            current,
                            ex.getMessage() == null ? "Failed to open window." : ex.getMessage(),
                            "Navigation Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                return;
            }
            if (current != null) {
                current.dispose();
            }
        });
    }

    private static String roleLabel(Role role) {
        return switch (role) {
            case ENGINEER -> "STRUCTURAL ENGINEER ACCOUNT";
            case OFFICER -> "LGU OFFICER ACCOUNT";
            case HEAD -> "LGU HEAD ACCOUNT";
        };
    }
}
