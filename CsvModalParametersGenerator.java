import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
 
public final class CsvModalParametersGenerator {
    private CsvModalParametersGenerator() {}
 
    public static CsvModalParameters generate(File csv) throws IOException {
        return generate(csv, 5000);
    }
 
    public static CsvModalParameters generate(File csv, int sampleRows) throws IOException {
        if (csv == null) {
            throw new IOException("No CSV file provided.");
        }
        CsvFileValidator.ValidationResult vr = CsvFileValidator.validate(csv);
        if (!vr.valid()) {
            throw new IOException(vr.message());
        }
 
        Charset charset = detectCharset(csv);
        Sniff sniff = sniff(csv, charset);
        if (sniff.blockFormat) {
            return generateBlockFormat(csv, charset, sampleRows);
        }
        return generateDelimited(csv, charset, sniff.delimiter, sampleRows);
    }
 
    private static CsvModalParameters generateBlockFormat(File csv, Charset charset, int sampleRows) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), charset))) {
            ArrayList<String> headers = new ArrayList<>();
            String line;
            boolean foundSeparator = false;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                if (t.contains("####")) {
                    foundSeparator = true;
                    break;
                }
                headers.add(t);
            }
 
            if (headers.isEmpty()) {
                throw new CsvParseException("Block-format CSV missing header names before separator.");
            }
            if (!foundSeparator) {
                throw new CsvParseException("Block-format CSV missing separator after headers.");
            }
 
            long rowCount = 0;
            int maxCols = 0;
            boolean inRecord = true;
            ArrayList<String> record = new ArrayList<>();
            ArrayList<List<String>> samples = new ArrayList<>();
 
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                if (t.contains("####")) {
                    if (inRecord && !record.isEmpty()) {
                        rowCount++;
                        maxCols = Math.max(maxCols, record.size());
                        if (rowCount <= sampleRows) {
                            samples.add(List.copyOf(record));
                        }
                        record.clear();
                    }
                    inRecord = true;
                    continue;
                }
 
                if (!inRecord) {
                    continue;
                }
 
                record.add(t);
            }
 
            if (!record.isEmpty()) {
                rowCount++;
                maxCols = Math.max(maxCols, record.size());
                if (rowCount <= sampleRows) {
                    samples.add(List.copyOf(record));
                }
            }
 
            if (maxCols <= 0) {
                throw new CsvParseException("Block-format CSV contained headers but no data records.");
            }
 
            List<String> usedHeaders = reconcileBlockHeaders(headers, maxCols);
            ColumnStats[] stats = initStats(maxCols);
            for (List<String> rec : samples) {
                observeRow(stats, rec, maxCols);
            }
            return buildParameters(csv, charset, '\n', true, usedHeaders, rowCount, stats);
        } catch (CsvParseException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
 
    private static List<String> reconcileBlockHeaders(List<String> headers, int maxCols) {
        if (headers == null || headers.isEmpty()) {
            return defaultHeaders(maxCols);
        }
        if (headers.size() == maxCols) {
            return normalizeHeaders(headers);
        }
        if (headers.size() == maxCols + 1 && headers.get(0).trim().equalsIgnoreCase("timestamp")) {
            return normalizeHeaders(headers.subList(1, headers.size()));
        }
        if (headers.size() > maxCols) {
            return normalizeHeaders(headers.subList(headers.size() - maxCols, headers.size()));
        }
        ArrayList<String> out = new ArrayList<>(normalizeHeaders(headers));
        while (out.size() < maxCols) {
            out.add("col" + (out.size() + 1));
        }
        return out;
    }
 
    private static CsvModalParameters generateDelimited(File csv, Charset charset, char delimiter, int sampleRows) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), charset))) {
            CsvRecordReader rr = new CsvRecordReader(br, delimiter);
            List<String> first = rr.nextRecord();
            if (first == null) {
                throw new CsvParseException("CSV file is empty.");
            }
            while (isAllEmpty(first)) {
                first = rr.nextRecord();
                if (first == null) {
                    throw new CsvParseException("CSV file is empty.");
                }
            }
 
            List<String> second = rr.nextRecord();
            while (second != null && isAllEmpty(second)) {
                second = rr.nextRecord();
            }
 
            boolean hasHeader = looksLikeHeader(first, second);
            List<String> headers;
            int cols;
            if (hasHeader) {
                headers = normalizeHeaders(first);
                cols = headers.size();
                if (!allUniqueNonEmpty(headers)) {
                    headers = defaultHeaders(cols);
                    hasHeader = false;
                }
            } else {
                cols = first.size();
                headers = defaultHeaders(cols);
            }
 
            ColumnStats[] stats = initStats(cols);
            long rowCount = 0;
 
            if (!hasHeader) {
                rowCount++;
                observeRow(stats, first, cols);
            }
 
            if (second != null) {
                if (second.size() != cols) {
                    throw new CsvParseException("Malformed CSV: expected " + cols + " columns but found " + second.size() + ".");
                }
                rowCount++;
                if (rowCount <= sampleRows) {
                    observeRow(stats, second, cols);
                }
            }
 
            List<String> rec;
            while ((rec = rr.nextRecord()) != null) {
                if (isAllEmpty(rec)) {
                    continue;
                }
                if (rec.size() != cols) {
                    throw new CsvParseException("Malformed CSV: expected " + cols + " columns but found " + rec.size() + ".");
                }
                rowCount++;
                if (rowCount <= sampleRows) {
                    observeRow(stats, rec, cols);
                }
            }
 
            return buildParameters(csv, charset, delimiter, false, headers, rowCount, stats);
        } catch (CsvParseException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
 
    private static CsvModalParameters buildParameters(
            File csv,
            Charset charset,
            char delimiter,
            boolean blockFormat,
            List<String> headers,
            long rowCount,
            ColumnStats[] stats
    ) {
        ArrayList<CsvModalParameters.Field> fields = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            ColumnStats cs = stats[i];
            CsvModalParameters.DataType type = cs.inferType();
            String displayFormat = cs.inferDisplayFormat(type);
            ArrayList<String> rules = new ArrayList<>();
            if (cs.nonEmptyCount > 0 && cs.emptyCount == 0) {
                rules.add("required");
            }
            cs.appendTypeSpecificRules(type, rules);
 
            fields.add(new CsvModalParameters.Field(
                    headers.get(i),
                    CsvModalParameters.normalizeKey(headers.get(i), i),
                    type,
                    displayFormat,
                    rules
            ));
        }
 
        String title = csv.getName();
        return new CsvModalParameters(
                title,
                charset,
                delimiter,
                blockFormat,
                headers.size(),
                rowCount,
                fields
        );
    }
 
    private static ColumnStats[] initStats(int cols) {
        ColumnStats[] stats = new ColumnStats[cols];
        for (int i = 0; i < cols; i++) {
            stats[i] = new ColumnStats();
        }
        return stats;
    }
 
    private static void observeRow(ColumnStats[] stats, List<String> rec, int cols) {
        for (int c = 0; c < cols; c++) {
            String v = c < rec.size() ? rec.get(c) : "";
            stats[c].observe(v);
        }
    }
 
    private static boolean isAllEmpty(List<String> rec) {
        if (rec == null) {
            return true;
        }
        for (String s : rec) {
            if (s != null && !s.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
 
    private static List<String> normalizeHeaders(List<String> raw) {
        ArrayList<String> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < raw.size(); i++) {
            String h = raw.get(i) == null ? "" : raw.get(i).trim();
            if (h.isEmpty()) {
                h = "col" + (i + 1);
            }
            String base = h;
            int n = 2;
            while (seen.contains(h.toLowerCase(Locale.ROOT))) {
                h = base + "_" + n;
                n++;
            }
            seen.add(h.toLowerCase(Locale.ROOT));
            out.add(h);
        }
        return out;
    }
 
    private static boolean allUniqueNonEmpty(List<String> headers) {
        if (headers == null || headers.isEmpty()) {
            return false;
        }
        HashSet<String> seen = new HashSet<>();
        for (String h : headers) {
            if (h == null || h.trim().isEmpty()) {
                return false;
            }
            String k = h.trim().toLowerCase(Locale.ROOT);
            if (seen.contains(k)) {
                return false;
            }
            seen.add(k);
        }
        return true;
    }
 
    private static List<String> defaultHeaders(int cols) {
        ArrayList<String> h = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            h.add("col" + (i + 1));
        }
        return h;
    }
 
    private static boolean looksLikeHeader(List<String> first, List<String> second) {
        if (first == null || first.isEmpty()) {
            return false;
        }
        if (second == null || second.isEmpty()) {
            return false;
        }
        if (first.size() != second.size()) {
            return false;
        }
 
        int firstTexty = 0;
        int secondTyped = 0;
        for (int i = 0; i < first.size(); i++) {
            String a = safe(first.get(i));
            String b = safe(second.get(i));
            if (a.isEmpty()) {
                continue;
            }
            if (looksLikeIdentifier(a)) {
                firstTexty++;
            }
            if (inferScalarType(b) != ScalarType.TEXT) {
                secondTyped++;
            }
        }
        return firstTexty >= Math.max(1, first.size() / 2) && secondTyped >= Math.max(1, second.size() / 2);
    }
 
    private static boolean looksLikeIdentifier(String s) {
        if (s == null) {
            return false;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return false;
        }
        int letters = 0;
        for (int i = 0; i < t.length(); i++) {
            if (Character.isLetter(t.charAt(i))) {
                letters++;
            }
        }
        return letters >= 1;
    }
 
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
 
    private static Charset detectCharset(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            in.mark(4);
            int b1 = in.read();
            int b2 = in.read();
            int b3 = in.read();
            int b4 = in.read();
            in.reset();
 
            if (b1 == 0xEF && b2 == 0xBB && b3 == 0xBF) {
                return StandardCharsets.UTF_8;
            }
            if (b1 == 0xFE && b2 == 0xFF) {
                return StandardCharsets.UTF_16BE;
            }
            if (b1 == 0xFF && b2 == 0xFE) {
                return StandardCharsets.UTF_16LE;
            }
        }
 
        byte[] sample = readSampleBytes(file, 64 * 1024);
        if (sample.length == 0) {
            return StandardCharsets.UTF_8;
        }
        if (canDecodeStrict(sample, StandardCharsets.UTF_8)) {
            return StandardCharsets.UTF_8;
        }
        Charset win1252 = Charset.forName("windows-1252");
        if (canDecodeStrict(sample, win1252)) {
            return win1252;
        }
        return StandardCharsets.ISO_8859_1;
    }
 
    private static byte[] readSampleBytes(File file, int max) throws IOException {
        int limit = Math.max(256, max);
        byte[] buf = new byte[limit];
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            int n = in.read(buf);
            if (n <= 0) {
                return new byte[0];
            }
            return Arrays.copyOf(buf, n);
        }
    }
 
    private static boolean canDecodeStrict(byte[] bytes, Charset charset) {
        CharsetDecoder dec = charset.newDecoder();
        dec.onMalformedInput(CodingErrorAction.REPORT);
        dec.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            dec.decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
 
    private record Sniff(boolean blockFormat, char delimiter) {}
 
    private static Sniff sniff(File file, Charset charset) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null && lines.size() < 50) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                lines.add(line);
            }
            for (String l : lines) {
                if (l.contains("####")) {
                    return new Sniff(true, '\n');
                }
            }
            char delim = detectDelimiter(lines);
            return new Sniff(false, delim);
        }
    }
 
    private static char detectDelimiter(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return ',';
        }
        char[] candidates = new char[]{',', ';', '\t', '|'};
        double bestScore = -1;
        char best = ',';
 
        for (char cand : candidates) {
            int checked = 0;
            int sum = 0;
            int sumSq = 0;
            for (String l : lines) {
                if (l == null) {
                    continue;
                }
                int cnt = countDelimiterOutsideQuotes(l, cand);
                if (cnt <= 0) {
                    continue;
                }
                int fields = cnt + 1;
                sum += fields;
                sumSq += fields * fields;
                checked++;
            }
            if (checked == 0) {
                continue;
            }
            double mean = sum / (double) checked;
            double var = (sumSq / (double) checked) - (mean * mean);
            double score = mean - (var * 0.8);
            if (score > bestScore) {
                bestScore = score;
                best = cand;
            }
        }
        return best;
    }
 
    private static int countDelimiterOutsideQuotes(String line, char delimiter) {
        boolean inQuotes = false;
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == delimiter && !inQuotes) {
                count++;
            }
        }
        return count;
    }
 
    private static final class CsvRecordReader {
        private final BufferedReader br;
        private final char delimiter;
        private int pushback = -1;
        private long recordIndex = 0;
 
        private CsvRecordReader(BufferedReader br, char delimiter) {
            this.br = Objects.requireNonNull(br, "br");
            this.delimiter = delimiter;
        }
 
        List<String> nextRecord() throws IOException, CsvParseException {
            ArrayList<String> record = new ArrayList<>();
            StringBuilder field = new StringBuilder();
            boolean inQuotes = false;
            boolean haveAnyChar = false;
 
            while (true) {
                int r = read();
                if (r == -1) {
                    if (inQuotes) {
                        throw new CsvParseException("Malformed CSV: unclosed quote in record " + (recordIndex + 1) + ".");
                    }
                    if (!haveAnyChar && record.isEmpty() && field.length() == 0) {
                        return null;
                    }
                    record.add(field.toString());
                    recordIndex++;
                    return record;
                }
 
                char ch = (char) r;
                haveAnyChar = true;
 
                if (ch == '"') {
                    if (inQuotes) {
                        int n = read();
                        if (n == '"') {
                            field.append('"');
                        } else {
                            inQuotes = false;
                            unread(n);
                        }
                    } else {
                        if (field.length() == 0) {
                            inQuotes = true;
                        } else {
                            field.append('"');
                        }
                    }
                    continue;
                }
 
                if (ch == delimiter && !inQuotes) {
                    record.add(field.toString());
                    field.setLength(0);
                    continue;
                }
 
                if ((ch == '\n' || ch == '\r') && !inQuotes) {
                    if (ch == '\r') {
                        int n = read();
                        if (n != '\n') {
                            unread(n);
                        }
                    }
                    record.add(field.toString());
                    recordIndex++;
                    return record;
                }
 
                field.append(ch);
            }
        }
 
        private int read() throws IOException {
            if (pushback != -1) {
                int t = pushback;
                pushback = -1;
                return t;
            }
            return br.read();
        }
 
        private void unread(int ch) {
            if (ch == -1) {
                return;
            }
            pushback = ch;
        }
    }
 
    private enum ScalarType { TEXT, INTEGER, DECIMAL, DATE, DATETIME, BOOLEAN, EMPTY }
 
    private static ScalarType inferScalarType(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) {
            return ScalarType.EMPTY;
        }
        if (looksBoolean(s)) {
            return ScalarType.BOOLEAN;
        }
        if (looksInteger(s)) {
            return ScalarType.INTEGER;
        }
        if (looksDecimal(s)) {
            return ScalarType.DECIMAL;
        }
        if (tryParseDateTime(s) != null) {
            return ScalarType.DATETIME;
        }
        if (tryParseDate(s) != null) {
            return ScalarType.DATE;
        }
        return ScalarType.TEXT;
    }
 
    private static boolean looksBoolean(String s) {
        String t = s.trim().toLowerCase(Locale.ROOT);
        return t.equals("true") || t.equals("false") || t.equals("yes") || t.equals("no") || t.equals("0") || t.equals("1");
    }
 
    private static boolean looksInteger(String s) {
        String t = s.trim();
        if (t.isEmpty()) {
            return false;
        }
        for (int i = 0; i < t.length(); i++) {
            char ch = t.charAt(i);
            if (i == 0 && (ch == '+' || ch == '-')) {
                continue;
            }
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }
 
    private static boolean looksDecimal(String s) {
        String t = s.trim();
        if (t.isEmpty()) {
            return false;
        }
        try {
            double v = Double.parseDouble(t);
            return Double.isFinite(v);
        } catch (NumberFormatException e) {
            return false;
        }
    }
 
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("uuuu/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/uuuu"),
            DateTimeFormatter.ofPattern("MM/dd/uuuu")
    };
 
    private static final DateTimeFormatter[] DATETIME_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss"),
            DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss")
    };
 
    private static LocalDate tryParseDate(String s) {
        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                return LocalDate.parse(s, f);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
 
    private static LocalDateTime tryParseDateTime(String s) {
        for (DateTimeFormatter f : DATETIME_FORMATS) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
 
    private static final class ColumnStats {
        long emptyCount = 0;
        long nonEmptyCount = 0;
        long intCount = 0;
        long decCount = 0;
        long boolCount = 0;
        long dateCount = 0;
        long dateTimeCount = 0;
        long textCount = 0;
 
        long maxLen = 0;
        long minLong = Long.MAX_VALUE;
        long maxLong = Long.MIN_VALUE;
        double minDouble = Double.POSITIVE_INFINITY;
        double maxDouble = Double.NEGATIVE_INFINITY;
        int maxDecimals = 0;
 
        LocalDate minDate = null;
        LocalDate maxDate = null;
        LocalDateTime minDateTime = null;
        LocalDateTime maxDateTime = null;
 
        Map<String, Integer> dateFormatCounts = new HashMap<>();
        Map<String, Integer> dateTimeFormatCounts = new HashMap<>();
 
        void observe(String raw) {
            String s = raw == null ? "" : raw.trim();
            if (s.isEmpty()) {
                emptyCount++;
                return;
            }
            nonEmptyCount++;
            maxLen = Math.max(maxLen, s.length());
 
            if (looksBoolean(s)) {
                boolCount++;
                return;
            }
            if (looksInteger(s)) {
                intCount++;
                try {
                    long v = Long.parseLong(s);
                    minLong = Math.min(minLong, v);
                    maxLong = Math.max(maxLong, v);
                    minDouble = Math.min(minDouble, (double) v);
                    maxDouble = Math.max(maxDouble, (double) v);
                } catch (NumberFormatException ignored) {
                }
                return;
            }
            if (looksDecimal(s)) {
                decCount++;
                int decs = decimalPlaces(s);
                maxDecimals = Math.max(maxDecimals, decs);
                try {
                    double v = Double.parseDouble(s);
                    if (Double.isFinite(v)) {
                        minDouble = Math.min(minDouble, v);
                        maxDouble = Math.max(maxDouble, v);
                    }
                } catch (NumberFormatException ignored) {
                }
                return;
            }
 
            LocalDateTime dt = tryParseDateTime(s);
            if (dt != null) {
                dateTimeCount++;
                if (minDateTime == null || dt.isBefore(minDateTime)) {
                    minDateTime = dt;
                }
                if (maxDateTime == null || dt.isAfter(maxDateTime)) {
                    maxDateTime = dt;
                }
                String fmt = pickDateTimeFormat(s);
                if (fmt != null) {
                    dateTimeFormatCounts.merge(fmt, 1, Integer::sum);
                }
                return;
            }
 
            LocalDate d = tryParseDate(s);
            if (d != null) {
                dateCount++;
                if (minDate == null || d.isBefore(minDate)) {
                    minDate = d;
                }
                if (maxDate == null || d.isAfter(maxDate)) {
                    maxDate = d;
                }
                String fmt = pickDateFormat(s);
                if (fmt != null) {
                    dateFormatCounts.merge(fmt, 1, Integer::sum);
                }
                return;
            }
 
            textCount++;
        }
 
        CsvModalParameters.DataType inferType() {
            long n = nonEmptyCount;
            if (n == 0) {
                return CsvModalParameters.DataType.TEXT;
            }
            long numeric = intCount + decCount;
            if (numeric >= Math.max(1, (long) (n * 0.90))) {
                if (decCount > 0) {
                    return CsvModalParameters.DataType.DECIMAL;
                }
                return CsvModalParameters.DataType.INTEGER;
            }
            if (dateTimeCount >= Math.max(1, (long) (n * 0.80))) {
                return CsvModalParameters.DataType.DATETIME;
            }
            if (dateCount >= Math.max(1, (long) (n * 0.80))) {
                return CsvModalParameters.DataType.DATE;
            }
            if (boolCount >= Math.max(1, (long) (n * 0.90))) {
                return CsvModalParameters.DataType.BOOLEAN;
            }
            return CsvModalParameters.DataType.TEXT;
        }
 
        String inferDisplayFormat(CsvModalParameters.DataType type) {
            return switch (type) {
                case INTEGER -> "0";
                case DECIMAL -> decimalFormat(maxDecimals);
                case DATE -> bestDateFormat();
                case DATETIME -> bestDateTimeFormat();
                case BOOLEAN -> "true/false";
                default -> "";
            };
        }
 
        void appendTypeSpecificRules(CsvModalParameters.DataType type, List<String> rules) {
            if (type == CsvModalParameters.DataType.INTEGER || type == CsvModalParameters.DataType.DECIMAL) {
                if (Double.isFinite(minDouble) && Double.isFinite(maxDouble) && nonEmptyCount > 0) {
                    rules.add("min=" + trimNumeric(minDouble));
                    rules.add("max=" + trimNumeric(maxDouble));
                }
                return;
            }
            if (type == CsvModalParameters.DataType.DATE) {
                if (minDate != null && maxDate != null) {
                    rules.add("minDate=" + minDate);
                    rules.add("maxDate=" + maxDate);
                }
                String fmt = bestDateFormat();
                if (!fmt.isEmpty()) {
                    rules.add("pattern=" + fmt);
                }
                return;
            }
            if (type == CsvModalParameters.DataType.DATETIME) {
                if (minDateTime != null && maxDateTime != null) {
                    rules.add("minDateTime=" + minDateTime);
                    rules.add("maxDateTime=" + maxDateTime);
                }
                String fmt = bestDateTimeFormat();
                if (!fmt.isEmpty()) {
                    rules.add("pattern=" + fmt);
                }
                return;
            }
            if (type == CsvModalParameters.DataType.TEXT) {
                if (maxLen > 0) {
                    rules.add("maxLength=" + maxLen);
                }
            }
        }
 
        private static String decimalFormat(int maxDecimals) {
            int d = Math.min(Math.max(maxDecimals, 0), 6);
            if (d == 0) {
                return "0";
            }
            StringBuilder sb = new StringBuilder("0.");
            for (int i = 0; i < d; i++) {
                sb.append("0");
            }
            return sb.toString();
        }
 
        private static int decimalPlaces(String s) {
            int dot = s.indexOf('.');
            if (dot < 0) {
                return 0;
            }
            int end = s.length();
            int exp = Math.max(s.indexOf('e'), s.indexOf('E'));
            if (exp > 0) {
                end = exp;
            }
            int dec = end - dot - 1;
            return Math.max(dec, 0);
        }
 
        private static String trimNumeric(double v) {
            if (!Double.isFinite(v)) {
                return "";
            }
            if (Math.abs(v - Math.rint(v)) < 1e-12) {
                return Long.toString((long) Math.rint(v));
            }
            String s = String.format(Locale.ROOT, "%.6f", v);
            int i = s.length() - 1;
            while (i > 0 && s.charAt(i) == '0') {
                i--;
            }
            if (s.charAt(i) == '.') {
                i--;
            }
            return s.substring(0, i + 1);
        }
 
        private String bestDateFormat() {
            if (dateFormatCounts.isEmpty()) {
                return "YYYY-MM-DD";
            }
            return bestKey(dateFormatCounts);
        }
 
        private String bestDateTimeFormat() {
            if (dateTimeFormatCounts.isEmpty()) {
                return "YYYY-MM-DDTHH:MM:SS";
            }
            return bestKey(dateTimeFormatCounts);
        }
 
        private static String bestKey(Map<String, Integer> counts) {
            String best = "";
            int bestN = -1;
            for (Map.Entry<String, Integer> e : counts.entrySet()) {
                if (e.getValue() > bestN) {
                    bestN = e.getValue();
                    best = e.getKey();
                }
            }
            return best;
        }
 
        private static String pickDateFormat(String s) {
            for (DateTimeFormatter f : DATE_FORMATS) {
                try {
                    LocalDate.parse(s, f);
                    if (f == DateTimeFormatter.ISO_LOCAL_DATE) {
                        return "YYYY-MM-DD";
                    }
                    return f.toString();
                } catch (DateTimeParseException ignored) {
                }
            }
            return null;
        }
 
        private static String pickDateTimeFormat(String s) {
            for (DateTimeFormatter f : DATETIME_FORMATS) {
                try {
                    LocalDateTime.parse(s, f);
                    if (f == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                        return "YYYY-MM-DDTHH:MM:SS";
                    }
                    return f.toString();
                } catch (DateTimeParseException ignored) {
                }
            }
            return null;
        }
    }
 
    private static final class CsvParseException extends Exception {
        private CsvParseException(String message) {
            super(message);
        }
    }
}
