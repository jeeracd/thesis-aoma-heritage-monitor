import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ProjectRepository {
    private static final Logger LOG = Logger.getLogger(ProjectRepository.class.getName());
    private static final Object LOCK = new Object();

    private static Path repositoryFile = defaultRepositoryFile();
    private static List<Project> cache = null;

    private ProjectRepository() {}

    public static Path getRepositoryFile() {
        synchronized (LOCK) {
            return repositoryFile;
        }
    }

    public static void setRepositoryFile(Path file) {
        synchronized (LOCK) {
            repositoryFile = file;
            cache = null;
        }
    }

    public static List<Project> getAll() {
        synchronized (LOCK) {
            ensureLoaded();
            return Collections.unmodifiableList(new ArrayList<>(cache));
        }
    }

    public static Optional<Project> findById(UUID id) {
        synchronized (LOCK) {
            ensureLoaded();
            for (Project p : cache) {
                if (p.getId().equals(id)) {
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        }
    }

    public static UUID createProject(
            String projectName,
            String buildingName,
            String dateConstructed,
            String materialsUsed,
            String function,
            String conservationStatus,
            String address,
            String description
    ) throws IOException {
        ProjectValidation.validateNewProject(projectName, buildingName, dateConstructed);

        synchronized (LOCK) {
            ensureLoaded();
            Project p = new Project(
                    UUID.randomUUID(),
                    Instant.now(),
                    projectName.trim(),
                    buildingName.trim(),
                    dateConstructed.trim(),
                    nullToEmpty(materialsUsed).trim(),
                    nullToEmpty(function).trim(),
                    nullToEmpty(conservationStatus).trim(),
                    nullToEmpty(address).trim(),
                    nullToEmpty(description).trim()
            );
            cache.add(p);
            try {
                persistLocked();
                return p.getId();
            } catch (IOException e) {
                cache.remove(cache.size() - 1);
                throw e;
            }
        }
    }

    public static void updateProject(Project updated) throws IOException {
        synchronized (LOCK) {
            ensureLoaded();
            Project previous = null;
            int previousIndex = -1;
            for (int i = 0; i < cache.size(); i++) {
                if (cache.get(i).getId().equals(updated.getId())) {
                    previous = cache.get(i);
                    previousIndex = i;
                    cache.set(i, updated);
                    try {
                        persistLocked();
                        return;
                    } catch (IOException e) {
                        cache.set(previousIndex, previous);
                        throw e;
                    }
                }
            }
            cache.add(updated);
            try {
                persistLocked();
            } catch (IOException e) {
                cache.remove(cache.size() - 1);
                throw e;
            }
        }
    }

    private static void ensureLoaded() {
        if (cache != null) {
            return;
        }
        AppMonitoring.init();
        cache = new ArrayList<>();
        try {
            cache.addAll(loadFromDiskLocked());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to load projects from disk: " + repositoryFile, e);
            cache = new ArrayList<>();
        }
    }

    private static List<Project> loadFromDiskLocked() throws IOException {
        Path file = repositoryFile;
        if (!Files.exists(file)) {
            return List.of();
        }
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            Object o = in.readObject();
            if (o instanceof List<?> list) {
                ArrayList<Project> out = new ArrayList<>();
                for (Object item : list) {
                    if (item instanceof Project p) {
                        out.add(p);
                    }
                }
                return out;
            }
            return List.of();
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize projects", e);
        }
    }

    private static void persistLocked() throws IOException {
        Path file = repositoryFile;
        Files.createDirectories(file.getParent());

        Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(tmp)))) {
            out.writeObject(new ArrayList<>(cache));
            out.flush();
        }
        try {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Path defaultRepositoryFile() {
        String home = System.getProperty("user.home");
        return Paths.get(home, ".aoma-heritage-monitor", "projects.ser");
    }

    private static String nullToEmpty(String v) {
        return v == null ? "" : v;
    }
}

