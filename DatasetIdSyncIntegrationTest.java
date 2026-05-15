import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class DatasetIdSyncIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempDirectory("aoma-dataset-sync");
        Path repo = tmp.resolve("projects.ser");
        ProjectRepository.setRepositoryFile(repo);

        UUID projectId = ProjectRepository.createProject(
                "Test Project",
                "Test Building",
                "1900-01-01",
                "",
                "",
                "",
                "Test Address",
                ""
        );

        JFrame[] ref = new JFrame[1];
        JLabel[] datasetLabelRef = new JLabel[1];

        SwingUtilities.invokeAndWait(() -> {
            EngineerBldgStatusOverview ui = new EngineerBldgStatusOverview();
            ref[0] = ui;
            datasetLabelRef[0] = findDatasetLabelForProject(ui, projectId);
            assertTrue(datasetLabelRef[0] != null, "Dataset label not found for project row");
        });

        String datasetId = ProjectDatasetIdStore.setActiveDatasetForSession(projectId, "#9999999999999");
        assertTrue(!datasetId.isBlank(), "Dataset id must be assigned");

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            String txt = datasetLabelRef[0].getText();
            assertTrue(txt.contains(datasetId), "Dataset label did not update after store change");
            ref[0].dispose();
        });

        ProjectDatasetIdStore.deleteProjectDatasets(projectId);
        System.out.println("ALL TESTS PASSED");
    }

    private static JLabel findDatasetLabelForProject(EngineerBldgStatusOverview ui, UUID projectId) {
        Container root = ui.getContentPane();
        return findDatasetLabelRecursive(root, projectId);
    }

    private static JLabel findDatasetLabelRecursive(Container c, UUID projectId) {
        if (c == null) {
            return null;
        }
        for (Component child : c.getComponents()) {
            if (child instanceof JPanel p) {
                Object id = p.getClientProperty("projectId");
                Object label = p.getClientProperty("datasetLabel");
                if (projectId.equals(id) && label instanceof JLabel l) {
                    return l;
                }
            }
            if (child instanceof Container cc) {
                JLabel found = findDatasetLabelRecursive(cc, projectId);
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
