import java.util.UUID;

public final class ProjectDatasetIdStoreTest {
    public static void main(String[] args) {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        ProjectDatasetIdStore.deleteProjectDatasets(p1);
        ProjectDatasetIdStore.deleteProjectDatasets(p2);

        String s1 = "#session-1";
        String s2 = "#session-2";

        String p1d1 = ProjectDatasetIdStore.getOrCreateDatasetId(p1, s1);
        assertTrue(!p1d1.isBlank(), "p1 session1 dataset id should be generated");
        assertTrue(ProjectDatasetIdStore.isValidDatasetId(p1, p1d1), "p1 dataset id must be valid");

        String p1d1b = ProjectDatasetIdStore.getOrCreateDatasetId(p1, s1);
        assertTrue(p1d1.equals(p1d1b), "p1 session1 dataset id must be stable");

        String p1d2 = ProjectDatasetIdStore.getOrCreateDatasetId(p1, s2);
        assertTrue(!p1d2.isBlank(), "p1 session2 dataset id should be generated");
        assertTrue(!p1d2.equals(p1d1), "p1 datasets must be unique across sessions");

        String p2d1 = ProjectDatasetIdStore.getOrCreateDatasetId(p2, s1);
        assertTrue(!p2d1.isBlank(), "p2 session1 dataset id should be generated");
        assertTrue(!p2d1.equals(p1d1), "datasets must be project-specific");

        String active = ProjectDatasetIdStore.setActiveDatasetForSession(p1, s1);
        assertTrue(active.equals(p1d1), "active dataset should match p1 session1 mapping");
        assertTrue(ProjectDatasetIdStore.getActiveDatasetId(p1).equals(p1d1), "active dataset retrieval mismatch");

        ProjectDatasetIdStore.deleteProjectDatasets(p1);
        ProjectDatasetIdStore.deleteProjectDatasets(p2);
        System.out.println("ALL TESTS PASSED");
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

