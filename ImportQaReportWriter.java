import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ImportQaReportWriter {
    private ImportQaReportWriter() {}

    public static Path writeJson(ImportQaReport report, Path outDir) throws IOException {
        Files.createDirectories(outDir);
        Path p = outDir.resolve("import_qa_report.json");
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            w.write(toJson(report));
            w.write("\n");
        }
        return p;
    }

    public static Path writeCsv(ImportQaReport report, Path outDir) throws IOException {
        Files.createDirectories(outDir);
        Path p = outDir.resolve("import_qa_report.csv");
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            w.write("section,field,value\n");
            writeRow(w, "summary", "profile", String.valueOf(report.profile()));
            writeRow(w, "summary", "sourceCsv", report.sourceCsv());
            writeRow(w, "summary", "rowsScanned", String.valueOf(report.rowsScanned()));
            writeRow(w, "summary", "columns", String.valueOf(report.columns()));
            writeRow(w, "summary", "startTimestamp", report.startTimestamp());
            writeRow(w, "summary", "endTimestamp", report.endTimestamp());
            writeRow(w, "summary", "timestampGaps", String.valueOf(report.timestampGaps()));
            writeRow(w, "summary", "gapPercent", String.valueOf(report.gapPercent()));
            writeRow(w, "summary", "modesPresent", Objects.toString(report.modesPresent(), ""));
            writeRow(w, "summary", "anomalyRows", String.valueOf(report.anomalyRows()));
            writeRow(w, "summary", "sensorWarnRows", String.valueOf(report.sensorWarnRows()));
            writeRow(w, "summary", "sensorFailRows", String.valueOf(report.sensorFailRows()));
            writeRow(w, "summary", "qualityScore", String.valueOf(report.qualityScore()));

            for (Map.Entry<String, Integer> e : report.eventCounts().entrySet()) {
                writeRow(w, "events", e.getKey(), String.valueOf(e.getValue()));
            }

            w.write("checks,check_id,passed,threshold,actual,description,details\n");
            for (ImportQaReport.Check c : report.checks()) {
                w.write("checks,");
                w.write(csv(c.id()));
                w.write(",");
                w.write(c.passed() ? "PASS" : "FAIL");
                w.write(",");
                w.write(csv(c.threshold()));
                w.write(",");
                w.write(csv(c.actual()));
                w.write(",");
                w.write(csv(c.description()));
                w.write(",");
                w.write(csv(c.details()));
                w.write("\n");
            }
        }
        return p;
    }

    private static void writeRow(BufferedWriter w, String section, String field, String value) throws IOException {
        w.write(csv(section));
        w.write(",");
        w.write(csv(field));
        w.write(",");
        w.write(csv(value));
        w.write("\n");
    }

    private static String csv(String s) {
        String v = s == null ? "" : s;
        boolean q = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        if (!q) {
            return v;
        }
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }

    private static String toJson(ImportQaReport r) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        add(sb, "profile", String.valueOf(r.profile()));
        add(sb, "sourceCsv", r.sourceCsv());
        add(sb, "rowsScanned", r.rowsScanned());
        add(sb, "columns", r.columns());
        add(sb, "startTimestamp", r.startTimestamp());
        add(sb, "endTimestamp", r.endTimestamp());
        add(sb, "timestampGaps", r.timestampGaps());
        add(sb, "gapPercent", r.gapPercent());
        addArrayInt(sb, "modesPresent", r.modesPresent());
        add(sb, "anomalyRows", r.anomalyRows());
        add(sb, "sensorWarnRows", r.sensorWarnRows());
        add(sb, "sensorFailRows", r.sensorFailRows());
        addObjectCounts(sb, "eventCounts", r.eventCounts());
        addChecks(sb, "checks", r.checks());
        add(sb, "qualityScore", r.qualityScore());
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private static void add(StringBuilder sb, String k, String v) {
        sb.append("\"").append(esc(k)).append("\":");
        sb.append("\"").append(esc(v)).append("\",");
    }

    private static void add(StringBuilder sb, String k, int v) {
        sb.append("\"").append(esc(k)).append("\":").append(v).append(",");
    }

    private static void add(StringBuilder sb, String k, double v) {
        sb.append("\"").append(esc(k)).append("\":").append(Double.isFinite(v) ? v : "null").append(",");
    }

    private static void addArrayInt(StringBuilder sb, String k, List<Integer> vals) {
        sb.append("\"").append(esc(k)).append("\":[");
        if (vals != null) {
            for (int i = 0; i < vals.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(vals.get(i));
            }
        }
        sb.append("],");
    }

    private static void addObjectCounts(StringBuilder sb, String k, Map<String, Integer> m) {
        sb.append("\"").append(esc(k)).append("\":{");
        if (m != null) {
            boolean first = true;
            for (Map.Entry<String, Integer> e : m.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(esc(e.getKey())).append("\":").append(e.getValue());
            }
        }
        sb.append("},");
    }

    private static void addChecks(StringBuilder sb, String k, List<ImportQaReport.Check> checks) {
        sb.append("\"").append(esc(k)).append("\":[");
        List<ImportQaReport.Check> list = checks == null ? List.of() : checks;
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            ImportQaReport.Check c = list.get(i);
            sb.append("{");
            sb.append("\"id\":\"").append(esc(c.id())).append("\",");
            sb.append("\"description\":\"").append(esc(c.description())).append("\",");
            sb.append("\"threshold\":\"").append(esc(c.threshold())).append("\",");
            sb.append("\"actual\":\"").append(esc(c.actual())).append("\",");
            sb.append("\"passed\":").append(c.passed()).append(",");
            sb.append("\"details\":\"").append(esc(c.details())).append("\"");
            sb.append("}");
        }
        sb.append("],");
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\\') sb.append("\\\\");
            else if (ch == '"') sb.append("\\\"");
            else if (ch == '\n') sb.append("\\n");
            else if (ch == '\r') sb.append("\\r");
            else if (ch == '\t') sb.append("\\t");
            else sb.append(ch);
        }
        return sb.toString();
    }
}

