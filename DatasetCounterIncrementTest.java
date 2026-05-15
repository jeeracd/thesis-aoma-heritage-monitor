import java.util.UUID;
import java.util.prefs.Preferences;

public final class DatasetCounterIncrementTest {
    public static void main(String[] args) {
        UUID projectId = UUID.randomUUID();
        ProjectDatasetIdStore.deleteProjectDatasets(projectId);

        String a = ProjectDatasetIdStore.getOrCreateDatasetId(projectId, "#S1");
        String b = ProjectDatasetIdStore.getOrCreateDatasetId(projectId, "#S2");
        assertTrue(!a.isBlank() && !b.isBlank(), "Dataset IDs must be created");
        assertTrue(!a.equals(b), "Dataset IDs must be unique");
        assertTrue(seq(b) == seq(a) + 1, "Sequence must increment (S1 -> S2)");

        Preferences p = Preferences.userRoot().node("aoma-heritage-monitor/datasets");
        p.remove("global.seq");

        String c = ProjectDatasetIdStore.getOrCreateDatasetId(projectId, "#S3");
        assertTrue(seq(c) > Math.max(seq(a), seq(b)), "Sequence must continue after seq reset");

        ProjectDatasetIdStore.deleteProjectDatasets(projectId);
        System.out.println("ALL TESTS PASSED");
    }

    private static long seq(String datasetId) {
        int idx = datasetId.lastIndexOf("-OMA-");
        if (idx < 0) {
            return -1L;
        }
        return Long.parseLong(datasetId.substring(idx + 5));
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}
