import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class SpectrogramCsvExport {
    private SpectrogramCsvExport() {}

    public static void writeRows(File file, SpectrogramTableModel model, int[] modelRows) throws IOException {
        writeRows(file, model, modelRows, null);
    }
 
    public static void writeRows(File file, SpectrogramTableModel model, int[] modelRows, CsvModalParameters modalParameters) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        if (model == null) {
            throw new IOException("No data to export.");
        }
        String mpTitle = modalParameters == null ? "" : modalParameters.title();
        String mpEncoding = modalParameters == null ? "" : modalParameters.charset().name();
        String mpDelimiter = modalParameters == null ? "" : String.valueOf(modalParameters.delimiter());
 
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            out.println("modal_title,modal_encoding,modal_delimiter,time_sec,freq_hz,amplitude_db,flag");
            if (modelRows == null) {
                return;
            }
            for (int r : modelRows) {
                double t = model.getTimeSecAtRow(r);
                double f = model.getFreqHzAtRow(r);
                double db = model.getDbAtRow(r);
                String flag = model.getFlagAtRow(r);
                if (!Double.isFinite(db)) {
                    continue;
                }
                out.print(csvCell(mpTitle));
                out.print(",");
                out.print(csvCell(mpEncoding));
                out.print(",");
                out.print(csvCell(mpDelimiter));
                out.print(",");
                out.print(t);
                out.print(",");
                out.print(f);
                out.print(",");
                out.print(db);
                out.print(",");
                out.println(csvCell(flag == null ? "" : flag));
            }
        }
    }
 
    private static String csvCell(String s) {
        if (s == null) {
            return "";
        }
        boolean needsQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ',' || ch == '"' || ch == '\n' || ch == '\r') {
                needsQuotes = true;
                break;
            }
        }
        if (!needsQuotes) {
            return s;
        }
        StringBuilder out = new StringBuilder();
        out.append('"');
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '"') {
                out.append("\"\"");
            } else {
                out.append(ch);
            }
        }
        out.append('"');
        return out.toString();
    }
}

