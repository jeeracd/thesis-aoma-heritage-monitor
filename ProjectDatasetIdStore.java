import javax.swing.SwingUtilities;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ProjectDatasetIdStore {
    public interface ActiveDatasetListener {
        void onActiveDatasetChanged(UUID projectId, String datasetId);
    }

    private static final Logger LOG = Logger.getLogger(ProjectDatasetIdStore.class.getName());
    private static final Object LOCK = new Object();
    private static final Preferences P = Preferences.userRoot().node("aoma-heritage-monitor/datasets");
    private static final List<ActiveDatasetListener> listeners = new ArrayList<>();

    private static final DateTimeFormatter DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private ProjectDatasetIdStore() {}

    public static String getOrCreateDatasetId(UUID projectId, String sessionKey) {
        if (projectId == null || sessionKey == null || sessionKey.isBlank()) {
            return "";
        }
        String project = projectId.toString();
        String encodedSession = encodeKey(sessionKey);
        String key = "project." + project + ".session." + encodedSession;

        synchronized (LOCK) {
            String existing = P.get(key, "");
            if (existing != null && !existing.isBlank()) {
                return existing;
            }
            String created = generateDatasetIdLocked(projectId);
            if (created.isBlank()) {
                return "";
            }
            P.put(key, created);
            try {
                P.flush();
            } catch (BackingStoreException ex) {
                P.remove(key);
                LOG.log(Level.WARNING, "Failed to persist dataset mapping.", ex);
                return "";
            }
            return created;
        }
    }

    public static boolean setActiveDatasetId(UUID projectId, String datasetId) {
        if (projectId == null || datasetId == null || datasetId.isBlank()) {
            return false;
        }
        if (!isValidDatasetId(projectId, datasetId)) {
            return false;
        }
        String key = "project." + projectId + ".active";
        synchronized (LOCK) {
            String prev = P.get(key, "");
            if (datasetId.equals(prev)) {
                return true;
            }
            P.put(key, datasetId);
            try {
                P.flush();
            } catch (BackingStoreException ex) {
                P.put(key, prev == null ? "" : prev);
                LOG.log(Level.WARNING, "Failed to persist active dataset id.", ex);
                return false;
            }
        }
        notifyActiveChanged(projectId, datasetId);
        return true;
    }

    public static String getActiveDatasetId(UUID projectId) {
        if (projectId == null) {
            return "";
        }
        synchronized (LOCK) {
            return P.get("project." + projectId + ".active", "");
        }
    }

    public static boolean clearActiveDatasetId(UUID projectId) {
        if (projectId == null) {
            return false;
        }
        String key = "project." + projectId + ".active";
        synchronized (LOCK) {
            try {
                P.remove(key);
                P.flush();
            } catch (BackingStoreException ex) {
                LOG.log(Level.WARNING, "Failed to clear active dataset id.", ex);
                return false;
            }
        }
        notifyActiveChanged(projectId, "");
        return true;
    }

    public static String ensureActiveDatasetId(UUID projectId) {
        if (projectId == null) {
            return "";
        }
        String active = getActiveDatasetId(projectId);
        if (active != null && !active.isBlank()) {
            return active;
        }
        String fallback = findLatestDatasetIdLocked(projectId);
        if (fallback == null || fallback.isBlank()) {
            fallback = getOrCreateDatasetId(projectId, "PROJECT_INIT");
        }
        if (fallback == null || fallback.isBlank()) {
            return "";
        }
        boolean ok = setActiveDatasetId(projectId, fallback);
        return ok ? fallback : "";
    }

    public static String setActiveDatasetForSession(UUID projectId, String sessionKey) {
        String datasetId = getOrCreateDatasetId(projectId, sessionKey);
        if (datasetId.isBlank()) {
            return "";
        }
        boolean ok = setActiveDatasetId(projectId, datasetId);
        return ok ? datasetId : "";
    }

    public static boolean hasDatasetForSession(UUID projectId, String sessionKey) {
        if (projectId == null || sessionKey == null || sessionKey.isBlank()) {
            return false;
        }
        String key = "project." + projectId + ".session." + encodeKey(sessionKey);
        synchronized (LOCK) {
            String v = P.get(key, "");
            return v != null && !v.isBlank();
        }
    }

    public static Runnable addActiveDatasetListener(ActiveDatasetListener listener) {
        if (listener == null) {
            return () -> {};
        }
        synchronized (LOCK) {
            listeners.add(listener);
        }
        return () -> {
            synchronized (LOCK) {
                listeners.remove(listener);
            }
        };
    }

    public static void deleteProjectDatasets(UUID projectId) {
        if (projectId == null) {
            return;
        }
        String prefix = "project." + projectId + ".";
        synchronized (LOCK) {
            try {
                for (String k : P.keys()) {
                    if (k.startsWith(prefix)) {
                        P.remove(k);
                    }
                }
                P.flush();
            } catch (BackingStoreException ex) {
                LOG.log(Level.WARNING, "Failed to delete project dataset keys.", ex);
            }
        }
        notifyActiveChanged(projectId, "");
    }

    public static boolean isValidDatasetId(UUID projectId, String datasetId) {
        if (projectId == null || datasetId == null) {
            return false;
        }
        String id = datasetId.trim();
        if (id.isEmpty()) {
            return false;
        }
        String expectedPrefix = "#P" + projectId.toString().replace("-", "").substring(0, 8).toUpperCase();
        if (!id.toUpperCase().startsWith(expectedPrefix + "-")) {
            return false;
        }
        return id.matches("^#P[0-9A-F]{8}-\\d{8}-OMA-\\d{3,}$");
    }

    private static void notifyActiveChanged(UUID projectId, String datasetId) {
        List<ActiveDatasetListener> snapshot;
        synchronized (LOCK) {
            snapshot = new ArrayList<>(listeners);
        }
        for (ActiveDatasetListener l : snapshot) {
            try {
                if (SwingUtilities.isEventDispatchThread()) {
                    l.onActiveDatasetChanged(projectId, datasetId);
                } else {
                    SwingUtilities.invokeLater(() -> l.onActiveDatasetChanged(projectId, datasetId));
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static String generateDatasetIdLocked(UUID projectId) {
        long next;
        String seqKey = "global.seq";
        long current = P.getLong(seqKey, 0L);
        long maxExisting = findGlobalMaxSeqLocked();
        current = Math.max(current, maxExisting);
        next = current + 1L;

        String projectPrefix = projectId.toString().replace("-", "").substring(0, 8).toUpperCase();
        String date = DATE.format(LocalDate.now());
        String datasetId = "#P" + projectPrefix + "-" + date + "-OMA-" + String.format(java.util.Locale.US, "%03d", next);

        P.putLong(seqKey, next);
        return datasetId;
    }

    private static long findGlobalMaxSeqLocked() {
        long max = 0L;
        try {
            for (String k : P.keys()) {
                if (!k.contains(".session.")) {
                    continue;
                }
                String v = P.get(k, "");
                long seq = parseSeq(v);
                if (seq > max) {
                    max = seq;
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.WARNING, "Failed to scan dataset keys.", ex);
        }
        return max;
    }

    private static String findLatestDatasetIdLocked(UUID projectId) {
        synchronized (LOCK) {
            String prefix = "project." + projectId + ".session.";
            String best = "";
            long bestSeq = -1L;
            try {
                for (String k : P.keys()) {
                    if (!k.startsWith(prefix)) {
                        continue;
                    }
                    String v = P.get(k, "");
                    if (!isValidDatasetId(projectId, v)) {
                        continue;
                    }
                    long seq = parseSeq(v);
                    if (seq > bestSeq) {
                        bestSeq = seq;
                        best = v;
                    }
                }
            } catch (BackingStoreException ex) {
                LOG.log(Level.WARNING, "Failed to find latest dataset id.", ex);
                return "";
            }
            return best;
        }
    }

    private static long parseSeq(String datasetId) {
        if (datasetId == null) {
            return -1L;
        }
        int idx = datasetId.lastIndexOf("-OMA-");
        if (idx < 0) {
            return -1L;
        }
        String tail = datasetId.substring(idx + 5).trim();
        try {
            return Long.parseLong(tail);
        } catch (Exception ignored) {
            return -1L;
        }
    }

    private static String encodeKey(String raw) {
        byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
