import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class EngineerBldgStatusOverviewScrollTest {
    public static void main(String[] args) throws Exception {
        runCase(1);
        runCase(10);
        runCase(60);
        System.out.println("ALL TESTS PASSED");
    }

    private static void runCase(int n) throws Exception {
        Path tmp = Files.createTempDirectory("aoma-project-scroll");
        Path repo = tmp.resolve("projects.ser");
        ProjectRepository.setRepositoryFile(repo);

        for (int i = 0; i < n; i++) {
            ProjectRepository.createProject(
                    "Project " + i,
                    "Building " + i,
                    "1900-01-01",
                    "",
                    "",
                    "",
                    "Address " + i,
                    ""
            );
        }

        JFrame[] ref = new JFrame[1];
        JScrollPane[] sp = new JScrollPane[1];

        SwingUtilities.invokeAndWait(() -> {
            EngineerBldgStatusOverview ui = new EngineerBldgStatusOverview();
            ui.setSize(1400, 850);
            ui.validate();
            ui.setSize(980, 720);
            ui.validate();
            ref[0] = ui;
            sp[0] = findProjectsScrollPane(ui.getRootPane());
            assertTrue(sp[0] != null, "Projects scroll pane not found for n=" + n);
        });

        Thread.sleep(150);

        SwingUtilities.invokeAndWait(() -> {
            JScrollBar bar = sp[0].getVerticalScrollBar();
            assertTrue(bar != null, "Vertical scrollbar missing for n=" + n);
            boolean shouldScroll = n >= 50;
            boolean hasOverflow = bar.getMaximum() - bar.getVisibleAmount() > 0;
            if (shouldScroll) {
                assertTrue(hasOverflow, "Expected overflow for n=" + n);
            }
            ref[0].dispose();
        });
    }

    private static JScrollPane findProjectsScrollPane(Container c) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JScrollPane pane) {
                Component view = pane.getViewport() == null ? null : pane.getViewport().getView();
                if (view == EngineerBldgStatusOverview.projectsContainer) {
                    return pane;
                }
            }
            if (child instanceof Container cc) {
                JScrollPane found = findProjectsScrollPane(cc);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

