import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public final class SpectrogramCsvExport {
    private SpectrogramCsvExport() {}

    public static void writeRows(File file, SpectrogramTableModel model, int[] modelRows) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        if (model == null) {
            throw new IOException("No data to export.");
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("time_sec,freq_hz,amplitude_db,flag");
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
                out.print(t);
                out.print(",");
                out.print(f);
                out.print(",");
                out.print(db);
                out.print(",");
                out.println(flag == null ? "" : flag);
            }
        }
    }
}

