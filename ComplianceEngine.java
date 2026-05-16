import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class ComplianceEngine {

    public enum Status {
        PASS, WARN, FAIL, NO_BASELINE, NO_MATCH, BASELINE_SET
    }

    private ComplianceEngine() {}

    /**
     * Run NSCP/NBC compliance check against stored baseline.
     *
     * @param outDir      OMA output directory (contains summary.properties + modal_properties.csv)
     * @param baselinePath path to global baseline frequencies file
     * @param setBaseline if true, write current frequencies as new baseline (returns BASELINE_SET)
     */
    public static Status apply(Path outDir, Path baselinePath, boolean setBaseline) throws IOException {
        ComplianceRules rules = ComplianceRules.load(outDir);
        Path modalCsv = resolveModalCsv(outDir);
        if (modalCsv == null || !Files.isRegularFile(modalCsv)) {
            writeSummaryKey(outDir, "compliance_status", Status.NO_MATCH.name());
            return Status.NO_MATCH;
        }

        List<double[]> modes = loadModes(modalCsv);
        if (modes.isEmpty()) {
            writeSummaryKey(outDir, "compliance_status", Status.NO_MATCH.name());
            return Status.NO_MATCH;
        }

        if (setBaseline) {
            writeBaseline(baselinePath, modes);
            writeSummaryKey(outDir, "compliance_status", Status.BASELINE_SET.name());
            return Status.BASELINE_SET;
        }

        List<Double> baselineFns = loadBaselineFns(baselinePath);
        if (baselineFns.isEmpty()) {
            writeSummaryKey(outDir, "compliance_status", Status.NO_BASELINE.name());
            return Status.NO_BASELINE;
        }

        Status overall = Status.PASS;
        boolean anyMatch = false;
        for (double[] mode : modes) {
            double fn = mode[0];
            double xi = mode[1];
            double nearest = nearestBaselineFn(baselineFns, fn, rules.matchFrequencyToleranceHz);
            if (Double.isNaN(nearest)) {
                continue;
            }
            anyMatch = true;
            double shift = Math.abs(fn - nearest) / nearest;
            Status fnStatus = evalFn(shift, rules);
            Status xiStatus = evalXi(xi, rules);
            overall = aggregate(overall, aggregate(fnStatus, xiStatus));
        }

        if (!anyMatch) {
            writeSummaryKey(outDir, "compliance_status", Status.NO_MATCH.name());
            return Status.NO_MATCH;
        }
        writeSummaryKey(outDir, "compliance_status", overall.name());
        return overall;
    }

    private static Status evalFn(double shiftRatio, ComplianceRules r) {
        if (shiftRatio >= r.maxFrequencyShiftRatioFail) return Status.FAIL;
        if (shiftRatio >= r.maxFrequencyShiftRatioWarn) return Status.WARN;
        return Status.PASS;
    }

    private static Status evalXi(double xi, ComplianceRules r) {
        if (xi < r.minDampingFail) return Status.FAIL;
        if (xi < r.minDampingWarn) return Status.WARN;
        return Status.PASS;
    }

    private static Status aggregate(Status a, Status b) {
        return rank(a) >= rank(b) ? a : b;
    }

    private static int rank(Status s) {
        switch (s) {
            case FAIL: return 4;
            case NO_MATCH: return 3;
            case WARN: return 2;
            case PASS: return 1;
            default: return 0;
        }
    }

    static List<Double> loadBaselineFns(Path baselinePath) {
        List<Double> list = new ArrayList<>();
        if (!Files.exists(baselinePath)) return list;
        try (BufferedReader r = Files.newBufferedReader(baselinePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    list.add(Double.parseDouble(line));
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException ignored) {}
        return list;
    }

    static double nearestBaselineFn(List<Double> baselines, double fn, double toleranceHz) {
        double best = Double.NaN;
        double bestDist = toleranceHz;
        for (double b : baselines) {
            double dist = Math.abs(fn - b);
            if (dist <= bestDist) {
                best = b;
                bestDist = dist;
            }
        }
        return best;
    }

    private static void writeBaseline(Path baselinePath, List<double[]> modes) throws IOException {
        Files.createDirectories(baselinePath.getParent());
        StringBuilder sb = new StringBuilder("# OMA baseline natural frequencies (Hz)\n");
        for (double[] m : modes) {
            sb.append(m[0]).append('\n');
        }
        Files.writeString(baselinePath, sb.toString(), StandardCharsets.UTF_8);
    }

    static void writeSummaryKey(Path outDir, String key, String value) throws IOException {
        Path summaryPath = outDir.resolve("summary.properties");
        List<String> lines = Files.exists(summaryPath)
                ? new ArrayList<>(Files.readAllLines(summaryPath, StandardCharsets.UTF_8))
                : new ArrayList<>();
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(key + "=")) {
                lines.set(i, key + "=" + value);
                found = true;
                break;
            }
        }
        if (!found) lines.add(key + "=" + value);
        Files.write(summaryPath, lines, StandardCharsets.UTF_8);
    }

    private static Path resolveModalCsv(Path outDir) throws IOException {
        Path summaryPath = outDir.resolve("summary.properties");
        if (!Files.exists(summaryPath)) return null;
        Properties props = new Properties();
        try (BufferedReader r = Files.newBufferedReader(summaryPath, StandardCharsets.UTF_8)) {
            props.load(r);
        }
        String csvPath = props.getProperty("modal_properties_csv");
        if (csvPath == null || csvPath.isBlank()) return null;
        return Path.of(csvPath);
    }

    private static List<double[]> loadModes(Path csvPath) throws IOException {
        List<double[]> modes = new ArrayList<>();
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) return modes;
        String[] headers = splitCsvLine(lines.get(0));
        int fnIdx = indexOf(headers, "frequency_hz");
        if (fnIdx < 0) fnIdx = indexOf(headers, "fn_hz");
        if (fnIdx < 0) fnIdx = indexOf(headers, "fn");
        if (fnIdx < 0) fnIdx = indexOf(headers, "frequency");
        int xiIdx = indexOf(headers, "xi");
        if (xiIdx < 0) xiIdx = indexOf(headers, "damping");
        if (xiIdx < 0) xiIdx = indexOf(headers, "damping_ratio");
        if (fnIdx < 0) return modes;
        for (int i = 1; i < lines.size(); i++) {
            String[] row = splitCsvLine(lines.get(i));
            try {
                double fn = parseDoubleSafe(get(row, fnIdx), Double.NaN);
                double xi = xiIdx >= 0 ? parseDoubleSafe(get(row, xiIdx), 0.0) : 0.0;
                if (!Double.isNaN(fn) && fn > 0) modes.add(new double[]{fn, xi});
            } catch (Exception ignored) {}
        }
        return modes;
    }

    static String[] splitCsvLine(String line) {
        return line == null ? new String[0] : line.split(",", -1);
    }

    static int indexOf(String[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static String get(String[] row, int idx) {
        return (row != null && idx >= 0 && idx < row.length) ? row[idx].trim() : "";
    }

    static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    static double parseDoubleSafe(String s, double def) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; }
    }

    static String setCsvValue(String line, String[] headers, String colName, String value) {
        int idx = indexOf(headers, colName);
        if (idx < 0) return line;
        String[] parts = splitCsvLine(line);
        if (idx < parts.length) parts[idx] = value;
        return String.join(",", parts);
    }
}
