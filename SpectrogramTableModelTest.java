import java.nio.file.Files;
import java.nio.file.Path;

public final class SpectrogramTableModelTest {
    public static void main(String[] args) throws Exception {
        testWindowRowCount();
        testFilteringBuildsIndex();
        testExportWritesCsv();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testWindowRowCount() {
        SpectrogramData data = smallData();
        SpectrogramTableModel m = new SpectrogramTableModel();
        m.setSpectrogram(data);
        assertTrue(m.getRowCount() == 20, "full window row count should be bins*frames");

        m.setViewWindow(new SpectrogramViewWindow(0, 2, 0, 25));
        assertTrue(m.getRowCount() == 6, "subwindow row count should match selected bins/frames");
    }

    private static void testFilteringBuildsIndex() {
        SpectrogramData data = smallData();
        SpectrogramTableModel m = new SpectrogramTableModel();
        m.setSpectrogram(data);
        m.setViewWindow(new SpectrogramViewWindow(0, 4, 0, 50));
        m.setValueFilter(-5.0, 15.0);
        int n = m.getRowCount();
        assertTrue(n > 0 && n < 20, "filtered row count should shrink");
        for (int i = 0; i < n; i++) {
            double db = m.getDbAtRow(i);
            assertTrue(db >= -5.0 && db <= 15.0, "filtered rows must respect bounds");
        }
    }

    private static void testExportWritesCsv() throws Exception {
        SpectrogramData data = smallData();
        SpectrogramTableModel m = new SpectrogramTableModel();
        m.setSpectrogram(data);
        int[] rows = new int[]{0, 1, 2};

        Path dir = Files.createTempDirectory("spec-table-");
        Path out = dir.resolve("export.csv");
        SpectrogramCsvExport.writeRows(out.toFile(), m, rows);
        String txt = Files.readString(out);
        assertTrue(txt.startsWith("time_sec,freq_hz,amplitude_db,flag"), "csv header expected");
        assertTrue(txt.split("\n").length >= 2, "csv should have data lines");
    }

    private static SpectrogramData smallData() {
        int bins = 4;
        int frames = 5;
        double[][] db = new double[bins][frames];
        for (int k = 0; k < bins; k++) {
            for (int f = 0; f < frames; f++) {
                db[k][f] = k * 10 + f;
            }
        }
        return new SpectrogramData(db, bins, frames, 8, 8, 2, 100.0, 4.0, -100.0, 0.0);
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

