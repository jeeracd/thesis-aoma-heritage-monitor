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
import java.util.List;

public final class CsvFileValidator {
    public static final long MAX_BYTES = 50L * 1024L * 1024L;
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
}

