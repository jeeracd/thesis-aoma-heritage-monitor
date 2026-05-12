import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public final class ProjectRepositoryTest {
    public static void main(String[] args) throws Exception {
        testCreateAndReload();
        testValidation();
        testPersistenceFailureRollback();
        testConcurrentCreates();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testCreateAndReload() throws Exception {
        Path dir = Files.createTempDirectory("aoma-project-repo-test-");
        Path repo = dir.resolve("projects.ser");
        ProjectRepository.setRepositoryFile(repo);

        assertEquals(0, ProjectRepository.getAll().size(), "repo starts empty");

        UUID id = ProjectRepository.createProject(
                "Project A",
                "Building A",
                "1901-01-01",
                "Stone",
                "Museum",
                "Protected",
                "Address 1",
                "Desc 1"
        );

        List<Project> afterCreate = ProjectRepository.getAll();
        assertEquals(1, afterCreate.size(), "repo has 1 after create");
        assertEquals(id, afterCreate.get(0).getId(), "created id matches");

        ProjectRepository.setRepositoryFile(repo);
        List<Project> afterReload = ProjectRepository.getAll();
        assertEquals(1, afterReload.size(), "repo reload has 1");
        assertEquals("Project A", afterReload.get(0).getProjectName(), "project name persisted");
        assertEquals("Building A", afterReload.get(0).getBuildingName(), "building name persisted");
        assertEquals("1901-01-01", afterReload.get(0).getDateConstructed(), "date persisted");
    }

    private static void testValidation() throws Exception {
        Path dir = Files.createTempDirectory("aoma-project-repo-test-");
        Path repo = dir.resolve("projects.ser");
        ProjectRepository.setRepositoryFile(repo);

        assertThrows(IllegalArgumentException.class, () -> {
            ProjectRepository.createProject(
                    " ",
                    "Building A",
                    "1901-01-01",
                    "",
                    "",
                    "",
                    "",
                    ""
            );
        }, "empty project name invalid");

        assertThrows(IllegalArgumentException.class, () -> {
            ProjectRepository.createProject(
                    "Project A",
                    " ",
                    "1901-01-01",
                    "",
                    "",
                    "",
                    "",
                    ""
            );
        }, "empty building name invalid");

        assertThrows(IllegalArgumentException.class, () -> {
            ProjectRepository.createProject(
                    "Project A",
                    "Building A",
                    " ",
                    "",
                    "",
                    "",
                    "",
                    ""
            );
        }, "empty date constructed invalid");
    }

    private static void testPersistenceFailureRollback() throws Exception {
        Path dir = Files.createTempDirectory("aoma-project-repo-test-");
        Path asDir = dir.resolve("not-a-file");
        Files.createDirectories(asDir);
        ProjectRepository.setRepositoryFile(asDir);

        int before = ProjectRepository.getAll().size();
        assertThrows(IOException.class, () -> {
            ProjectRepository.createProject(
                    "Project B",
                    "Building B",
                    "1901-01-01",
                    "",
                    "",
                    "",
                    "",
                    ""
            );
        }, "create should fail when repository path is a directory");

        int after = ProjectRepository.getAll().size();
        assertEquals(before, after, "cache should roll back on persistence failure");
    }

    private static void testConcurrentCreates() throws Exception {
        Path dir = Files.createTempDirectory("aoma-project-repo-test-");
        Path repo = dir.resolve("projects.ser");
        ProjectRepository.setRepositoryFile(repo);

        int threads = 8;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            new Thread(() -> {
                try {
                    start.await();
                    ProjectRepository.createProject(
                            "P" + idx,
                            "B" + idx,
                            "1901-01-01",
                            "",
                            "",
                            "",
                            "",
                            ""
                    );
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                } finally {
                    done.countDown();
                }
            }).start();
        }

        start.countDown();
        done.await();

        if (failure.get() != null) {
            throw new RuntimeException("concurrent create failed", failure.get());
        }

        assertEquals(threads, ProjectRepository.getAll().size(), "all concurrent creates persisted");

        ProjectRepository.setRepositoryFile(repo);
        assertEquals(threads, ProjectRepository.getAll().size(), "reload keeps all projects");
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void assertThrows(Class<? extends Throwable> clazz, ThrowingRunnable r, String msg) throws Exception {
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

