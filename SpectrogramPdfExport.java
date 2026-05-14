import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SpectrogramPdfExport {
    private SpectrogramPdfExport() {}

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

        ArrayList<String> lines = new ArrayList<>();
        if (!mpTitle.isEmpty()) {
            lines.add("Source CSV: " + mpTitle);
        }
        if (!mpEncoding.isEmpty() || !mpDelimiter.isEmpty()) {
            lines.add("Encoding: " + mpEncoding + "  Delimiter: " + mpDelimiter);
        }
        lines.add("");
        lines.add("modal_title | modal_encoding | modal_delimiter | time_sec | freq_hz | amplitude_db | flag");

        int limit = 800;
        int written = 0;
        if (modelRows != null) {
            for (int r : modelRows) {
                double t = model.getTimeSecAtRow(r);
                double f = model.getFreqHzAtRow(r);
                double db = model.getDbAtRow(r);
                String flag = model.getFlagAtRow(r);
                if (!Double.isFinite(db)) {
                    continue;
                }
                lines.add(mpTitle + " | " + mpEncoding + " | " + mpDelimiter + " | " + t + " | " + f + " | " + db + " | " + (flag == null ? "" : flag));
                written++;
                if (written >= limit) {
                    lines.add("...");
                    lines.add("(truncated: " + written + " rows shown)");
                    break;
                }
            }
        }
        SimplePdfWriter.writeTextPage(file, "Spectral Data Export", lines);
    }
}

