import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class AppSession {
    private static volatile File lastUploadedCsv;
    private static volatile CsvFileValidator.CsvProfile lastUploadedCsvProfile = CsvFileValidator.CsvProfile.UNKNOWN;
    private static volatile long lastUploadedCsvSeq;
    private static volatile long lastPyOma2RunSeq;
    private static volatile UUID activeProjectId;
    private static volatile RoleMenuBar.Role activeRole;
    private static final Object csvLock = new Object();
    private static final List<Runnable> lastUploadedCsvListeners = new ArrayList<>();

    private AppSession() {}

    public static void setLastUploadedCsv(File file) {
        synchronized (csvLock) {
            lastUploadedCsv = file;
            lastUploadedCsvProfile = CsvFileValidator.detectProfile(file);
            lastUploadedCsvSeq++;
        }
        notifyLastUploadedCsvListeners();
    }

    public static File getLastUploadedCsv() {
        return lastUploadedCsv;
    }

    public static CsvFileValidator.CsvProfile getLastUploadedCsvProfile() {
        return lastUploadedCsvProfile;
    }

    public static long getLastUploadedCsvSequence() {
        return lastUploadedCsvSeq;
    }

    public static long getLastPyOma2RunSequence() {
        return lastPyOma2RunSeq;
    }

    public static void markPyOma2RunStartedForCurrentCsv() {
        lastPyOma2RunSeq = lastUploadedCsvSeq;
    }

    public static Runnable addLastUploadedCsvListener(Runnable listener) {
        if (listener == null) {
            return () -> {};
        }
        synchronized (csvLock) {
            lastUploadedCsvListeners.add(listener);
        }
        return () -> {
            synchronized (csvLock) {
                lastUploadedCsvListeners.remove(listener);
            }
        };
    }

    private static void notifyLastUploadedCsvListeners() {
        List<Runnable> snapshot;
        synchronized (csvLock) {
            snapshot = new ArrayList<>(lastUploadedCsvListeners);
        }
        for (Runnable r : snapshot) {
            try {
                r.run();
            } catch (Exception ignored) {
            }
        }
    }

    public static void setActiveProjectId(UUID projectId) {
        activeProjectId = projectId;
    }

    public static UUID getActiveProjectId() {
        return activeProjectId;
    }

    public static void setActiveRole(RoleMenuBar.Role role) {
        activeRole = role;
    }

    public static RoleMenuBar.Role getActiveRole() {
        return activeRole;
    }
}

