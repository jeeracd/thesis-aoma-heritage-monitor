import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
 
public record CsvModalParameters(
        String title,
        Charset charset,
        char delimiter,
        boolean blockFormat,
        int columnCount,
        long rowCount,
        List<Field> fields
) {
    public CsvModalParameters {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(charset, "charset");
        Objects.requireNonNull(fields, "fields");
    }
 
    public enum DataType {
        TEXT,
        INTEGER,
        DECIMAL,
        DATE,
        DATETIME,
        BOOLEAN
    }
 
    public record Field(
            String csvHeader,
            String key,
            DataType type,
            String displayFormat,
            List<String> validationRules
    ) {
        public Field {
            Objects.requireNonNull(csvHeader, "csvHeader");
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(displayFormat, "displayFormat");
            Objects.requireNonNull(validationRules, "validationRules");
        }
    }
 
    public String toJsonLikeString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"title\": ").append(jsonString(title)).append(",\n");
        sb.append("  \"charset\": ").append(jsonString(charset.name())).append(",\n");
        sb.append("  \"delimiter\": ").append(jsonString(String.valueOf(delimiter))).append(",\n");
        sb.append("  \"blockFormat\": ").append(blockFormat).append(",\n");
        sb.append("  \"rowCount\": ").append(rowCount).append(",\n");
        sb.append("  \"columnCount\": ").append(columnCount).append(",\n");
        sb.append("  \"fields\": [\n");
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            sb.append("    {\n");
            sb.append("      \"csvHeader\": ").append(jsonString(f.csvHeader())).append(",\n");
            sb.append("      \"key\": ").append(jsonString(f.key())).append(",\n");
            sb.append("      \"type\": ").append(jsonString(f.type().name().toLowerCase(Locale.ROOT))).append(",\n");
            sb.append("      \"displayFormat\": ").append(jsonString(f.displayFormat())).append(",\n");
            sb.append("      \"validationRules\": ").append(jsonStringArray(f.validationRules())).append("\n");
            sb.append("    }");
            if (i < fields.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }
 
    private static String jsonString(String s) {
        if (s == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder();
        out.append("\"");
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\\' || ch == '"') {
                out.append('\\').append(ch);
            } else if (ch == '\n') {
                out.append("\\n");
            } else if (ch == '\r') {
                out.append("\\r");
            } else if (ch == '\t') {
                out.append("\\t");
            } else {
                out.append(ch);
            }
        }
        out.append("\"");
        return out.toString();
    }
 
    private static String jsonStringArray(List<String> items) {
        if (items == null) {
            return "[]";
        }
        StringJoiner j = new StringJoiner(", ", "[", "]");
        for (String s : items) {
            j.add(jsonString(s == null ? "" : s));
        }
        return j.toString();
    }
 
    public static String normalizeKey(String header, int index) {
        String h = header == null ? "" : header.trim();
        if (h.isEmpty()) {
            return "col" + (index + 1);
        }
        String cleaned = h.replaceAll("[^A-Za-z0-9]+", " ").trim();
        if (cleaned.isEmpty()) {
            return "col" + (index + 1);
        }
        String[] parts = cleaned.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.isEmpty()) {
                continue;
            }
            if (i == 0) {
                sb.append(p.substring(0, 1).toLowerCase(Locale.ROOT));
                if (p.length() > 1) {
                    sb.append(p.substring(1));
                }
            } else {
                sb.append(p.substring(0, 1).toUpperCase(Locale.ROOT));
                if (p.length() > 1) {
                    sb.append(p.substring(1));
                }
            }
        }
        String key = sb.toString();
        if (key.isEmpty() || !Character.isLetter(key.charAt(0))) {
            key = "col" + (index + 1);
        }
        return key;
    }
 
    public static List<String> rules(String... rules) {
        ArrayList<String> out = new ArrayList<>();
        if (rules == null) {
            return out;
        }
        for (String r : rules) {
            if (r == null) {
                continue;
            }
            String t = r.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }
}
