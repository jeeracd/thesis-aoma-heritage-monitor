import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public final class OmaResultsModel {
    public enum Severity {
        OK,
        WARNING,
        CRITICAL
    }

    public record ModeRow(
            int modeIndex,
            double frequencyHz,
            double dampingRatio,
            double mpc,
            double mpd,
            double phiAccelX,
            double phiAccelY,
            double phiAccelZ,
            Severity severity
    ) {
    }

    private final Path outDir;
    private final List<ModeRow> modes;
    private final Properties summary;
    private final double[][] macMatrix;

    private OmaResultsModel(Path outDir, List<ModeRow> modes, Properties summary, double[][] macMatrix) {
        this.outDir = outDir;
        this.modes = modes;
        this.summary = summary;
        this.macMatrix = macMatrix;
    }

    public Path outDir() {
        return outDir;
    }

    public List<ModeRow> modes() {
        return modes;
    }

    public Properties summary() {
        return summary;
    }

    public double[][] macMatrix() {
        return macMatrix;
    }

    public int issuesCount() {
        int c = 0;
        for (ModeRow r : modes) {
            if (r.severity() != Severity.OK) {
                c++;
            }
        }
        return c;
    }

    public static Optional<Path> findLatestOutDir() {
        Path base = Path.of(System.getProperty("user.home"), ".aoma-heritage-monitor", "pyoma2-results");
        if (!Files.isDirectory(base)) {
            return Optional.empty();
        }
        try {
            return Files.list(base)
                    .filter(Files::isDirectory)
                    .max(Comparator.comparingLong(p -> {
                        try {
                            return Long.parseLong(p.getFileName().toString());
                        } catch (Exception ex) {
                            try {
                                return Files.getLastModifiedTime(p).toMillis();
                            } catch (Exception ex2) {
                                return 0L;
                            }
                        }
                    }));
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    public static List<Path> listAvailableOutDirs() {
        Path base = Path.of(System.getProperty("user.home"), ".aoma-heritage-monitor", "pyoma2-results");
        if (!Files.isDirectory(base)) {
            return List.of();
        }
        try {
            List<Path> dirs = Files.list(base)
                    .filter(Files::isDirectory)
                    .sorted((a, b) -> Long.compare(sortKey(b), sortKey(a)))
                    .toList();
            return List.copyOf(dirs);
        } catch (IOException ex) {
            return List.of();
        }
    }

    private static long sortKey(Path p) {
        try {
            return Long.parseLong(p.getFileName().toString());
        } catch (Exception ex) {
            try {
                return Files.getLastModifiedTime(p).toMillis();
            } catch (Exception ex2) {
                return 0L;
            }
        }
    }

    public static OmaResultsModel loadLatestOrEmpty() {
        Optional<Path> p = findLatestOutDir();
        return p.map(OmaResultsModel::loadFromDirOrEmpty).orElseGet(() -> new OmaResultsModel(null, List.of(), new Properties(), new double[0][0]));
    }

    public static OmaResultsModel loadFromDirOrEmpty(Path outDir) {
        if (outDir == null || !Files.isDirectory(outDir)) {
            return new OmaResultsModel(null, List.of(), new Properties(), new double[0][0]);
        }
        try {
            return loadFromDir(outDir);
        } catch (Exception ex) {
            return new OmaResultsModel(outDir, List.of(), new Properties(), new double[0][0]);
        }
    }

    public static OmaResultsModel loadFromDir(Path outDir) throws IOException {
        Objects.requireNonNull(outDir, "outDir");
        Properties props = loadPropertiesRaw(outDir.resolve("summary.properties"));

        Path csv = outDir.resolve("modal_properties.csv");
        List<ModeRow> rows = new ArrayList<>();
        if (Files.isRegularFile(csv)) {
            try (BufferedReader in = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
                String header = in.readLine();
                if (header != null) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String[] parts = splitCsvLine(line);
                        if (parts.length < 8) {
                            continue;
                        }
                        int mode = parseInt(parts[0]);
                        double fn = parseDouble(parts[1]);
                        double xi = parseDouble(parts[2]);
                        double mpc = parseDouble(parts[3]);
                        double mpd = parseDouble(parts[4]);
                        double px = parseDouble(parts[5]);
                        double py = parseDouble(parts[6]);
                        double pz = parseDouble(parts[7]);
                        Severity sev = validate(fn, xi, mpc, mpd);
                        rows.add(new ModeRow(mode, fn, xi, mpc, mpd, px, py, pz, sev));
                    }
                }
            }
        }

        rows.sort(Comparator.comparingInt(ModeRow::modeIndex));
        double[][] mac = computeMac(rows);
        return new OmaResultsModel(outDir, List.copyOf(rows), props, mac);
    }

    private static Properties loadPropertiesRaw(Path path) throws IOException {
        Properties props = new Properties();
        if (path == null || !Files.isRegularFile(path)) {
            return props;
        }
        try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = in.readLine()) != null) {
                String s = line.trim();
                if (s.isEmpty()) {
                    continue;
                }
                if (s.startsWith("#") || s.startsWith("!")) {
                    continue;
                }
                int idx = s.indexOf('=');
                if (idx < 0) {
                    idx = s.indexOf(':');
                }
                if (idx < 0) {
                    continue;
                }
                String key = s.substring(0, idx).trim();
                String value = s.substring(idx + 1).trim();
                props.setProperty(key, value);
            }
        }
        return props;
    }

    public File resolveFileFromSummary(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        String v = summary.getProperty(key);
        if (v == null || v.isBlank()) {
            return null;
        }
        File f = new File(v);
        if (f.isAbsolute()) {
            return f;
        }
        if (outDir == null) {
            return f;
        }
        return outDir.resolve(v).toFile();
    }

    private static Severity validate(double fn, double xi, double mpc, double mpd) {
        boolean freqBad = !(Double.isFinite(fn) && fn > 0 && fn < 1000);
        boolean xiBad = !(Double.isFinite(xi) && xi >= 0 && xi <= 0.20);
        boolean mpcBad = !(Double.isFinite(mpc) && mpc >= 0 && mpc <= 1.0);
        boolean mpdBad = !(Double.isFinite(mpd) && Math.abs(mpd) <= 1.0);
        int bad = 0;
        if (freqBad) bad++;
        if (xiBad) bad++;
        if (mpcBad) bad++;
        if (mpdBad) bad++;
        if (bad == 0) {
            return Severity.OK;
        }
        if (bad >= 2 || freqBad) {
            return Severity.CRITICAL;
        }
        return Severity.WARNING;
    }

    private static String[] splitCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                quoted = !quoted;
            } else if (ch == ',' && !quoted) {
                out.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        out.add(cur.toString().trim());
        return out.toArray(new String[0]);
    }

    private static double parseDouble(String s) {
        if (s == null) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception ex) {
            return Double.NaN;
        }
    }

    private static int parseInt(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private static double[][] computeMac(List<ModeRow> modes) {
        if (modes == null || modes.size() < 2) {
            return new double[0][0];
        }
        int n = modes.size();
        double[][] phi = new double[n][3];
        for (int i = 0; i < n; i++) {
            ModeRow r = modes.get(i);
            phi[i][0] = r == null ? 0.0 : r.phiAccelX();
            phi[i][1] = r == null ? 0.0 : r.phiAccelY();
            phi[i][2] = r == null ? 0.0 : r.phiAccelZ();
        }

        double[][] out = new double[n][n];
        for (int i = 0; i < n; i++) {
            double nii = dot(phi[i], phi[i]);
            for (int j = 0; j < n; j++) {
                double njj = dot(phi[j], phi[j]);
                double nij = dot(phi[i], phi[j]);
                double denom = nii * njj;
                if (denom <= 0 || !Double.isFinite(denom)) {
                    out[i][j] = Double.NaN;
                } else {
                    double v = (nij * nij) / denom;
                    out[i][j] = Double.isFinite(v) ? v : Double.NaN;
                }
            }
        }
        return out;
    }

    private static double dot(double[] a, double[] b) {
        if (a == null || b == null) {
            return 0.0;
        }
        int n = Math.min(a.length, b.length);
        double s = 0.0;
        for (int i = 0; i < n; i++) {
            s += a[i] * b[i];
        }
        return s;
    }
}
