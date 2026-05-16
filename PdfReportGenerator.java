import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public final class PdfReportGenerator {

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private PdfReportGenerator() {}

    /**
     * Generate a PDF report for an OMA session.
     *
     * @param outDir OMA output directory (contains summary.properties + modal_properties.csv)
     * @return the generated PDF File inside outDir
     */
    public static File generate(Path outDir) throws IOException {
        ReportIdentity.ensureGlobalDefault();
        ReportIdentity id = ReportIdentity.load(outDir);
        Properties props = loadProps(outDir.resolve("summary.properties"));

        String complianceStatus = props.getProperty("compliance_status", "NO_BASELINE");
        String sessionId = outDir.getFileName().toString();
        String timestamp = TS.format(LocalDateTime.now());

        List<String> lines = new ArrayList<>();
        lines.add(id.studyTitle);
        lines.add(id.institution);
        if (!id.degree.isBlank()) lines.add(id.degree);
        if (!id.authors.isBlank()) lines.add("Authors: " + id.authors);
        if (!id.thesisAdviser.isBlank()) lines.add("Adviser: " + id.thesisAdviser);
        lines.add("");
        lines.add("OMA SESSION: " + sessionId);
        lines.add("Generated:   " + timestamp);
        lines.add("Input CSV:   " + trim(props.getProperty("input_csv", "-")));
        lines.add("Sampling fs: " + trim(props.getProperty("fs_hz", "-")) + " Hz");
        lines.add("");
        lines.add("=== COMPLIANCE STATUS: " + complianceStatus + " ===");
        lines.add("    " + recommendedAction(complianceStatus));
        lines.add("");
        lines.add("MODAL PROPERTIES");
        lines.add("----------------");

        Path modalCsvPath = resolveAcceptedCsv(outDir, props);
        if (modalCsvPath != null && Files.isRegularFile(modalCsvPath)) {
            List<String[]> rows = readCsv(modalCsvPath);
            if (!rows.isEmpty()) {
                lines.add(formatRow(rows.get(0)));
                lines.add(repeat('-', 60));
                for (int i = 1; i < rows.size(); i++) {
                    lines.add(formatRow(rows.get(i)));
                }
            }
        } else {
            lines.add("(no modal data available)");
        }

        lines.add("");
        lines.add(repeat('=', 60));
        lines.add(id.reportFooterNote);

        File out = outDir.resolve("aoma_report.pdf").toFile();
        SimplePdfWriter.writeTextPage(out, id.studyTitle, lines);
        writeSummaryKey(outDir, "report_pdf", fp(out.toPath()));
        return out;
    }

    private static String formatRow(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append("  |  ");
            String v = cols[i].trim();
            sb.append(String.format("%-18s", v.length() > 18 ? v.substring(0, 18) : v));
        }
        return sb.toString();
    }

    private static String repeat(char c, int n) {
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    static String recommendedAction(String status) {
        if (status == null) return "Consult a licensed structural engineer.";
        switch (status) {
            case "PASS":         return "No action required. Continue routine monitoring.";
            case "WARN":         return "Review structural condition. Consider detailed inspection.";
            case "FAIL":         return "CRITICAL: Immediate structural assessment recommended.";
            case "BASELINE_SET": return "Baseline established. Future runs will compare against this.";
            case "NO_BASELINE":  return "Run analysis again to establish a baseline.";
            default:             return "Consult a licensed structural engineer.";
        }
    }

    static List<String[]> readCsvComplianceOnly(Path csv) throws IOException {
        return readCsv(csv);
    }

    static List<String[]> readCsv(Path csv) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (String line : Files.readAllLines(csv, StandardCharsets.UTF_8)) {
            rows.add(splitCsvLine(line));
        }
        return rows;
    }

    static String[] splitCsvLine(String line) {
        return line == null ? new String[0] : line.split(",", -1);
    }

    static int idx(String[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static String val(String[] row, int i) {
        return (row != null && i >= 0 && i < row.length) ? row[i].trim() : "";
    }

    static Properties loadProps(Path p) throws IOException {
        Properties props = new Properties();
        if (Files.exists(p)) {
            try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                props.load(r);
            }
        }
        return props;
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

    static Path resolveAcceptedCsv(Path outDir, Properties props) {
        String path = props.getProperty("modal_properties_csv");
        if (path == null || path.isBlank()) return null;
        return Path.of(path);
    }

    static Path resolveRulesJson(Path outDir, Properties props) {
        return outDir.resolve("compliance-rules.properties");
    }

    static Path resolveBaselineJson(Path outDir, Properties props) {
        return Path.of(System.getProperty("user.home"),
                ".aoma-heritage-monitor", "oma-baseline.csv");
    }

    static Path resolvePoleJson(Path outDir, Properties props) {
        String path = props.getProperty("stabilization_poles_csv");
        if (path == null || path.isBlank()) return null;
        return Path.of(path);
    }

    static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String fp(Path p) {
        return p == null ? "" : p.toString().replace("\\", "/");
    }
}
