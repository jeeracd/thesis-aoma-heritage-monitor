import java.time.Instant;
import java.util.UUID;

public final class ProjectDetailsControllerTest {
    public static void main(String[] args) {
        testToggleAndDirty();
        testCancelReverts();
        testValidation();
        testSaveSuccessResets();
        testSaveFailureRevertsDraft();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testToggleAndDirty() {
        Project p = sampleProject();
        ProjectDetailsController c = new ProjectDetailsController(p);
        assertEquals(false, c.isEditing(), "starts not editing");
        assertEquals(false, c.isDirty(), "starts not dirty");

        c.enterEditMode();
        assertEquals(true, c.isEditing(), "entered edit mode");

        c.setDraftDetails(
                "New Project Name",
                p.getBuildingName(),
                p.getDateConstructed(),
                p.getMaterialsUsed(),
                p.getFunction(),
                p.getConservationStatus(),
                p.getAddress(),
                p.getDescription()
        );
        assertEquals(true, c.isDirty(), "draft becomes dirty after change");
    }

    private static void testCancelReverts() {
        Project p = sampleProject();
        ProjectDetailsController c = new ProjectDetailsController(p);
        c.enterEditMode();
        c.setDraftDetails(
                "Changed",
                "Changed",
                "1901-01-01",
                "",
                "",
                "",
                "",
                ""
        );
        c.cancelEdits();
        assertEquals(false, c.isEditing(), "cancel exits edit mode");
        assertEquals(false, c.isDirty(), "cancel clears dirty");
        assertEquals(p.getProjectName(), c.getDraft().getProjectName(), "cancel restores baseline");
    }

    private static void testValidation() {
        Project p = sampleProject();
        ProjectDetailsController c = new ProjectDetailsController(p);
        c.enterEditMode();

        c.setDraftDetails(
                " ",
                p.getBuildingName(),
                p.getDateConstructed(),
                "",
                "",
                "",
                "",
                ""
        );
        assertThrows(IllegalArgumentException.class, c::validateDraft, "project name required");

        c.setDraftDetails(
                p.getProjectName(),
                p.getBuildingName(),
                "not-a-date",
                "",
                "",
                "",
                "",
                ""
        );
        assertThrows(IllegalArgumentException.class, c::validateDraft, "date format required");
    }

    private static void testSaveSuccessResets() {
        Project p = sampleProject();
        ProjectDetailsController c = new ProjectDetailsController(p);
        c.enterEditMode();
        c.setDraftDetails(
                "Updated",
                p.getBuildingName(),
                p.getDateConstructed(),
                p.getMaterialsUsed(),
                p.getFunction(),
                p.getConservationStatus(),
                p.getAddress(),
                p.getDescription()
        );
        Project persisted = p.withUpdatedDetails(
                "Updated",
                p.getBuildingName(),
                p.getDateConstructed(),
                p.getMaterialsUsed(),
                p.getFunction(),
                p.getConservationStatus(),
                p.getAddress(),
                p.getDescription()
        );
        c.applySaveSuccess(persisted);
        assertEquals(false, c.isEditing(), "save success exits edit mode");
        assertEquals(false, c.isDirty(), "save success clears dirty");
        assertEquals("Updated", c.getBaseline().getProjectName(), "baseline updated");
    }

    private static void testSaveFailureRevertsDraft() {
        Project p = sampleProject();
        ProjectDetailsController c = new ProjectDetailsController(p);
        c.enterEditMode();
        c.setDraftDetails(
                "Updated",
                p.getBuildingName(),
                p.getDateConstructed(),
                p.getMaterialsUsed(),
                p.getFunction(),
                p.getConservationStatus(),
                p.getAddress(),
                p.getDescription()
        );
        c.applySaveFailureRevert();
        assertEquals(false, c.isDirty(), "save failure revert clears dirty");
        assertEquals(p.getProjectName(), c.getDraft().getProjectName(), "draft reverted");
    }

    private static Project sampleProject() {
        return new Project(
                UUID.randomUUID(),
                Instant.now(),
                "Project Name",
                "Building Name",
                "1901-01-01",
                "Stone",
                "Museum",
                "Protected",
                "Address",
                "Description"
        );
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void assertThrows(Class<? extends Throwable> clazz, ThrowingRunnable r, String msg) {
        try {
            r.run();
        } catch (Throwable t) {
            if (clazz.isInstance(t)) {
                return;
            }
            throw new AssertionError(msg + " (threw " + t.getClass().getName() + ")");
        }
        throw new AssertionError(msg + " (did not throw)");
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(msg + " (expected " + expected + " but got " + actual + ")");
        }
    }
}

