import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CsvFileValidator {
    public static final long MAX_BYTES = 50L * 1024L * 1024L;
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TS_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private CsvFileValidator() {}

    public enum CsvProfile {
        SIGNAL,
        KPI_LOG,
        BLOCK_DELIMITED,
        UNKNOWN
    }

    public static ValidationResult validate(File file) {
        if (file == null) {
            return ValidationResult.error("No file selected.");
        }
        Path p = file.toPath();
        if (!Files.exists(p)) {
            return ValidationResult.error("Selected file does not exist.");
        }
        if (!Files.isRegularFile(p)) {
            return ValidationResult.error("Selected path is not a file.");
        }
        if (!Files.isReadable(p)) {
            return ValidationResult.error("Permission denied: file is not readable.");
        }
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".csv")) {
            return ValidationResult.error("Please select a .csv file.");
        }
        try {
            long size = Files.size(p);
            if (size > MAX_BYTES) {
                return ValidationResult.error("File exceeds 50MB limit.");
            }
        } catch (IOException e) {
            return ValidationResult.error("Unable to read file size.");
        }
        return ValidationResult.success();
    }

    public static DetailedValidation validateDetailed(File file) {
        ValidationResult base = validate(file);
        if (!base.valid()) {
            return new DetailedValidation(base, CsvProfile.UNKNOWN, List.of(), "");
        }
        CsvProfile profile = detectProfile(file);
        List<String> warnings = new ArrayList<>();
        String summary = "";

        if (profile == CsvProfile.KPI_LOG) {
            try (BufferedReader in = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                String header = in.readLine();
                if (header == null) {
                    return new DetailedValidation(ValidationResult.error("CSV is empty."), CsvProfile.KPI_LOG, List.of(), "");
                }
                String[] cols = splitCsvLine(header);
                int tsCol = findCol(cols, "timestamp");
                int modeCol = findCol(cols, "mode_active");
                int eventCol = findCol(cols, "event_marker");
                int anomalyCol = findCol(cols, "anomaly_flag");
                if (tsCol < 0 || modeCol < 0) {
                    return new DetailedValidation(ValidationResult.error("KPI CSV must include at least: timestamp, mode_active."), CsvProfile.KPI_LOG, List.of(), "");
                }
                int rows = 0;
                int gaps = 0;
                long prevMin = Long.MIN_VALUE;
                boolean has8 = false;
                int anomalyRows = 0;
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] parts = splitCsvLine(line);
                    rows++;
                    if (modeCol < parts.length) {
                        try {
                            int m = Integer.parseInt(parts[modeCol].trim());
                            if (m == 8) {
                                has8 = true;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    if (anomalyCol >= 0 && anomalyCol < parts.length) {
                        if ("1".equals(parts[anomalyCol].trim())) {
                            anomalyRows++;
                        }
                    }
                    if (tsCol < parts.length) {
                        try {
                            LocalDateTime dt = LocalDateTime.parse(parts[tsCol].trim(), TS);
                            long min = dt.getYear() * 525600L + dt.getDayOfYear() * 1440L + dt.getHour() * 60L + dt.getMinute();
                            if (prevMin != Long.MIN_VALUE && (min - prevMin) != 1) {
                                gaps++;
                            }
                            prevMin = min;
                        } catch (Exception ignored) {
                        }
                    }
                    if (rows >= 20000) {
                        warnings.add("Validation scanned first 20,000 rows (file is larger).");
                        break;
                    }
                }
                if (!has8) {
                    warnings.add("Mode 8 not present in CSV (expected 8 distinct modes).");
                }
                if (eventCol < 0) {
                    warnings.add("event_marker column not found; event overlays will be limited.");
                }
                if (anomalyCol < 0) {
                    warnings.add("anomaly_flag column not found; anomaly filtering will be limited.");
                }
                summary = "Detected KPI log CSV. Rows scanned: " + rows + ". Timestamp gaps: " + gaps + ". Anomaly rows: " + anomalyRows + ".";
            } catch (Exception ex) {
                warnings.add("Detailed validation failed: " + ex.getMessage());
            }
        }

        return new DetailedValidation(base, profile, List.copyOf(warnings), summary);
    }

    public static CsvProfile detectProfile(File file) {
        if (file == null) {
            return CsvProfile.UNKNOWN;
        }
        try {
            String head = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            int cut = Math.min(head.length(), 8192);
            String s = head.substring(0, cut);
            if (s.contains("########")) {
                return CsvProfile.BLOCK_DELIMITED;
            }
        } catch (Exception ignored) {
        }
        try (BufferedReader in = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String header = in.readLine();
            if (header == null) {
                return CsvProfile.UNKNOWN;
            }
            String h = header.toLowerCase();
            if (h.contains("mode_active") && h.contains("event_marker") && h.contains("anomaly_flag")) {
                return CsvProfile.KPI_LOG;
            }
            return CsvProfile.SIGNAL;
        } catch (Exception ex) {
            return CsvProfile.UNKNOWN;
        }
    }

    private static int findCol(String[] cols, String name) {
        if (cols == null || name == null) {
            return -1;
        }
        String n = name.trim().toLowerCase();
        for (int i = 0; i < cols.length; i++) {
            String c = cols[i] == null ? "" : cols[i].trim().toLowerCase();
            if (n.equals(c)) {
                return i;
            }
        }
        return -1;
    }

    private static String[] splitCsvLine(String line) {
        if (line == null) {
            return new String[0];
        }
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

    public record ValidationResult(boolean valid, String message) {
        public static ValidationResult success() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message == null ? "" : message);
        }
    }

    public record DetailedValidation(ValidationResult base, CsvProfile profile, List<String> warnings, String summary) {
    }

    public record QaArtifacts(Path jsonPath, Path csvPath, ImportQaReport report) {
    }

    public static QaArtifacts writeImportQaReport(File file, Path outDir, ImportQaRules rules) throws IOException {
        ImportQaReport report = generateImportQaReport(file, rules == null ? ImportQaRules.defaults() : rules);
        Path jsonPath = ImportQaReportWriter.writeJson(report, outDir);
        Path csvPath = ImportQaReportWriter.writeCsv(report, outDir);
        return new QaArtifacts(jsonPath, csvPath, report);
    }

    public static ImportQaReport generateImportQaReport(File file, ImportQaRules rules) {
        ValidationResult base = validate(file);
        if (!base.valid()) {
            return new ImportQaReport(CsvProfile.UNKNOWN, file == null ? "" : file.getAbsolutePath(), 0, 0, "", "", 0, 0.0, List.of(), 0, 0, 0, Map.of(),
                    List.of(new ImportQaReport.Check("FILE_VALID", "File is readable and within size limits", "valid=true", "valid=false", false, base.message())), 0.0);
        }

        CsvProfile profile = detectProfile(file);
        ImportQaRules r = rules == null ? ImportQaRules.defaults() : rules;

        int maxRows = Math.max(1000, r.maxRowsToScan());

        int rows = 0;
        int colsCount = 0;
        String startTs = "";
        String endTs = "";
        int gaps = 0;
        double gapPct = 0.0;
        Set<Integer> modes = new LinkedHashSet<>();
        int anomalyRows = 0;
        int warnRows = 0;
        int failRows = 0;
        Map<String, Integer> eventCounts = new LinkedHashMap<>();
        List<ImportQaReport.Check> checks = new ArrayList<>();

        try (BufferedReader in = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String header = in.readLine();
            if (header == null) {
                checks.add(new ImportQaReport.Check("EMPTY_FILE", "CSV contains at least one header row", "non-empty", "empty", false, "No header row found."));
                return finalizeReport(profile, file, rows, colsCount, startTs, endTs, gaps, gapPct, modes, anomalyRows, warnRows, failRows, eventCounts, checks);
            }
            String[] cols = splitCsvLine(header);
            colsCount = cols.length;
            int tsCol = findAnyCol(cols, List.of("timestamp", "time", "t"));
            int modeCol = findAnyCol(cols, List.of("mode_active", "mode"));
            int modeLabelCol = findAnyCol(cols, List.of("mode_label", "mode_name"));
            int eventCol = findAnyCol(cols, List.of("event_marker", "event"));
            int anomalyCol = findAnyCol(cols, List.of("anomaly_flag", "anomaly"));
            int healthCol = findAnyCol(cols, List.of("sensor_health", "health"));

            checks.add(requiredColumnsCheck(profile, tsCol, modeCol, cols));

            long prevT = Long.MIN_VALUE;
            long expectedDt = Long.MIN_VALUE;
            List<Long> diffs = new ArrayList<>();

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = splitCsvLine(line);
                rows++;

                if (tsCol >= 0 && tsCol < parts.length) {
                    String ts = parts[tsCol].trim();
                    if (rows == 1) {
                        startTs = ts;
                    }
                    endTs = ts;
                    long t = parseTimestampMillis(ts);
                    if (t > 0 && prevT > 0) {
                        long dt = t - prevT;
                        if (dt > 0) {
                            if (diffs.size() < 600) {
                                diffs.add(dt);
                            }
                        }
                    }
                    if (t > 0) {
                        if (prevT > 0 && expectedDt > 0) {
                            double tol = Math.max(0.0, r.intervalTolerancePercent());
                            double hi = expectedDt * (1.0 + (tol / 100.0));
                            if (t - prevT > hi) {
                                gaps++;
                            }
                        }
                        prevT = t;
                    }
                }

                if (modeCol >= 0 && modeCol < parts.length) {
                    try {
                        int m = Integer.parseInt(parts[modeCol].trim());
                        if (m > 0) {
                            modes.add(m);
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (anomalyCol >= 0 && anomalyCol < parts.length) {
                    if ("1".equals(parts[anomalyCol].trim())) {
                        anomalyRows++;
                    }
                }

                if (eventCol >= 0 && eventCol < parts.length) {
                    String ev = parts[eventCol].trim();
                    if (!ev.isEmpty()) {
                        eventCounts.put(ev, eventCounts.getOrDefault(ev, 0) + 1);
                    }
                }

                if (healthCol >= 0 && healthCol < parts.length) {
                    String h = parts[healthCol].trim().toUpperCase();
                    if ("WARN".equals(h)) {
                        warnRows++;
                    } else if ("FAIL".equals(h)) {
                        failRows++;
                    }
                }

                if (rows == 1200 && expectedDt == Long.MIN_VALUE) {
                    expectedDt = inferExpectedDtMs(r.expectedIntervalMs(), diffs);
                }

                if (rows >= maxRows) {
                    checks.add(new ImportQaReport.Check("SCAN_LIMIT", "QA scan covers the full dataset (or is explicitly limited)", "rowsScanned>=totalRows", "rowsScanned=" + rows, true, "Scan capped at " + rows + " rows for performance."));
                    break;
                }
            }

            if (expectedDt == Long.MIN_VALUE) {
                expectedDt = inferExpectedDtMs(r.expectedIntervalMs(), diffs);
            }

            if (rows > 1) {
                gapPct = (gaps * 100.0) / Math.max(1, (rows - 1));
            }

            if (tsCol < 0) {
                checks.add(new ImportQaReport.Check("TIME_COLUMN", "Timestamp column exists for gap validation", "present", "missing", false, "No timestamp/time column found."));
            }

            checks.add(gapPercentCheck(r, gaps, gapPct, expectedDt));
            checks.add(sensorFailRateCheck(r, rows, healthCol >= 0, failRows));
            checks.addAll(requiredEventChecks(r, eventCol >= 0, eventCounts));
            if (r.requireMode8()) {
                boolean ok = modes.contains(8);
                checks.add(new ImportQaReport.Check("MODE8_PRESENT", "CSV contains mode 8 at least once", "present", ok ? "present" : "missing", ok, ""));
            }

            if (profile == CsvProfile.KPI_LOG && modeLabelCol >= 0 && modeCol >= 0) {
                ImportQaReport.Check c = modeLabelConsistencyCheck(file.toPath(), maxRows, cols, modeCol, modeLabelCol);
                if (c != null) {
                    checks.add(c);
                }
            }
        } catch (Exception ex) {
            checks.add(new ImportQaReport.Check("READ_ERROR", "CSV can be scanned for QA", "readable", "error", false, ex.getMessage() == null ? "" : ex.getMessage()));
        }

        return finalizeReport(profile, file, rows, colsCount, startTs, endTs, gaps, gapPct, modes, anomalyRows, warnRows, failRows, eventCounts, checks);
    }

    private static ImportQaReport finalizeReport(
            CsvProfile profile,
            File file,
            int rows,
            int colsCount,
            String startTs,
            String endTs,
            int gaps,
            double gapPct,
            Set<Integer> modes,
            int anomalyRows,
            int warnRows,
            int failRows,
            Map<String, Integer> eventCounts,
            List<ImportQaReport.Check> checks
    ) {
        int failed = 0;
        for (ImportQaReport.Check c : checks) {
            if (c != null && !c.passed()) {
                failed++;
            }
        }
        double score = Math.max(0.0, 100.0 - (failed * 20.0));
        return new ImportQaReport(
                profile,
                file == null ? "" : file.getAbsolutePath(),
                rows,
                colsCount,
                startTs,
                endTs,
                gaps,
                gapPct,
                List.copyOf(modes),
                anomalyRows,
                warnRows,
                failRows,
                Map.copyOf(eventCounts),
                List.copyOf(checks),
                score
        );
    }

    private static ImportQaReport.Check requiredColumnsCheck(CsvProfile profile, int tsCol, int modeCol, String[] cols) {
        if (profile == CsvProfile.KPI_LOG) {
            boolean ok = tsCol >= 0 && modeCol >= 0;
            return new ImportQaReport.Check("REQUIRED_COLUMNS", "KPI log CSV has required columns", "timestamp,mode_active", ok ? "present" : "missing", ok, ok ? "" : ("header=" + String.join(",", cols)));
        }
        boolean ok = tsCol >= 0;
        return new ImportQaReport.Check("REQUIRED_COLUMNS", "Signal CSV has required columns", "timestamp", ok ? "present" : "missing", ok, ok ? "" : ("header=" + String.join(",", cols)));
    }

    private static long parseTimestampMillis(String s) {
        if (s == null) {
            return 0L;
        }
        String v = s.trim();
        if (v.isEmpty()) {
            return 0L;
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(v, TS_MS);
            return dt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(v, TS);
            return dt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static long inferExpectedDtMs(Double expectedIntervalMs, List<Long> diffs) {
        if (expectedIntervalMs != null && expectedIntervalMs > 0) {
            return Math.round(expectedIntervalMs);
        }
        if (diffs == null || diffs.isEmpty()) {
            return Long.MIN_VALUE;
        }
        diffs.sort(Long::compare);
        return diffs.get(diffs.size() / 2);
    }

    private static ImportQaReport.Check gapPercentCheck(ImportQaRules r, int gaps, double gapPct, long expectedDt) {
        double thr = Math.max(0.0, r.maxGapPercent());
        boolean ok = gapPct <= thr;
        String exp = expectedDt > 0 ? (expectedDt + "ms") : "unknown";
        return new ImportQaReport.Check("TIME_GAP_PERCENT", "Timestamp continuity meets gap threshold", "gapPercent<=" + thr + "%", String.format("%.6f%% (gaps=%d, expectedDt=%s)", gapPct, gaps, exp), ok, "");
    }

    private static ImportQaReport.Check sensorFailRateCheck(ImportQaRules r, int rows, boolean hasHealth, int failRows) {
        double thr = Math.max(0.0, r.maxSensorFailPercent());
        if (!hasHealth) {
            return new ImportQaReport.Check("SENSOR_FAIL_RATE", "Sensor health failure rate is within threshold", "failPercent<=" + thr + "%", "N/A", false, "sensor_health column missing");
        }
        double pct = rows > 0 ? (failRows * 100.0) / rows : 0.0;
        boolean ok = pct <= thr;
        return new ImportQaReport.Check("SENSOR_FAIL_RATE", "Sensor health failure rate is within threshold", "failPercent<=" + thr + "%", String.format("%.6f%% (fail=%d)", pct, failRows), ok, "");
    }

    private static List<ImportQaReport.Check> requiredEventChecks(ImportQaRules r, boolean hasEventCol, Map<String, Integer> eventCounts) {
        List<String> req = r.requiredEventMarkers() == null ? List.of() : r.requiredEventMarkers();
        if (req.isEmpty()) {
            return List.of(new ImportQaReport.Check("REQUIRED_EVENTS", "Required event markers present (none configured)", "none", "none", true, ""));
        }
        List<ImportQaReport.Check> out = new ArrayList<>();
        if (!hasEventCol) {
            out.add(new ImportQaReport.Check("REQUIRED_EVENTS", "Required event markers present", String.join("|", req), "N/A", false, "event_marker column missing"));
            return out;
        }
        for (String m : req) {
            String key = m == null ? "" : m.trim();
            if (key.isEmpty()) {
                continue;
            }
            int c = eventCounts.getOrDefault(key, 0);
            boolean ok = c > 0;
            out.add(new ImportQaReport.Check("EVENT_" + key, "Event marker present: " + key, "count>0", "count=" + c, ok, ""));
        }
        return out;
    }

    private static int findAnyCol(String[] cols, List<String> names) {
        if (cols == null || names == null) {
            return -1;
        }
        for (String n : names) {
            int idx = findCol(cols, n);
            if (idx >= 0) {
                return idx;
            }
        }
        return -1;
    }

    private static ImportQaReport.Check modeLabelConsistencyCheck(Path p, int maxRows, String[] cols, int modeCol, int modeLabelCol) {
        Map<Integer, String> map = Map.of(
                1, "Idle / Ambient",
                2, "Low Occupancy",
                3, "High Occupancy",
                4, "Maintenance / Inspection",
                5, "Traffic Influence",
                6, "Windy Conditions",
                7, "Rain Event",
                8, "Extreme Event / Alert"
        );
        int mismatches = 0;
        int scanned = 0;
        try (BufferedReader in = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            in.readLine();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = splitCsvLine(line);
                scanned++;
                if (modeCol < parts.length && modeLabelCol < parts.length) {
                    try {
                        int m = Integer.parseInt(parts[modeCol].trim());
                        String lab = parts[modeLabelCol].trim();
                        String exp = map.get(m);
                        if (exp != null && !exp.equals(lab)) {
                            mismatches++;
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (scanned >= Math.min(maxRows, 50_000)) {
                    break;
                }
            }
        } catch (Exception ex) {
            return new ImportQaReport.Check("MODE_LABELS", "Mode labels are consistent with mode ids", "consistent", "error", false, ex.getMessage() == null ? "" : ex.getMessage());
        }
        boolean ok = mismatches == 0;
        return new ImportQaReport.Check("MODE_LABELS", "Mode labels are consistent with mode ids", "mismatches=0", "mismatches=" + mismatches, ok, scanned >= 50_000 ? "Checked first 50,000 rows." : "");
    }
}

