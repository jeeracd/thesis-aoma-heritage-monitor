import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class DatasetIdOnProjectCreateIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempDirectory("aoma-project-create-dataset");
        Path repo = tmp.resolve("projects.ser");
        ProjectRepository.setRepositoryFile(repo);

        UUID projectId = ProjectRepository.createProject(
                "Project",
                "Building",
                "1900-01-01",
                "",
                "",
                "",
                "Address",
                ""
        );
        ProjectDatasetIdStore.deleteProjectDatasets(projectId);
        String datasetId = ProjectDatasetIdStore.setActiveDatasetForSession(projectId, "PROJECT_INIT");
        assertTrue(!datasetId.isBlank(), "Dataset id should be assigned on project create");

        JFrame[] ref = new JFrame[1];
        JLabel[] datasetLabelRef = new JLabel[1];

        SwingUtilities.invokeAndWait(() -> {
            EngineerBldgStatusOverview ui = new EngineerBldgStatusOverview();
            ref[0] = ui;
            datasetLabelRef[0] = findDatasetLabelForProject(ui.getRootPane(), projectId);
            assertTrue(datasetLabelRef[0] != null, "Dataset label not found");
        });

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            assertTrue(datasetLabelRef[0].getText().contains(datasetId), "Dataset id not shown below View Details");
            ref[0].dispose();
        });

        ProjectDatasetIdStore.deleteProjectDatasets(projectId);
        System.out.println("ALL TESTS PASSED");
    }

    private static JLabel findDatasetLabelForProject(Container c, UUID projectId) {
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
                JLabel found = findDatasetLabelForProject(cc, projectId);
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

