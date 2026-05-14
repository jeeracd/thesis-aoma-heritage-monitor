import java.nio.file.Files;
import java.nio.file.Path;

public final class SpectrogramExportFormatsTest {
    public static void main(String[] args) throws Exception {
        testExcelXmlExport();
        testPdfExport();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testExcelXmlExport() throws Exception {
        SpectrogramTableModel m = new SpectrogramTableModel();
        m.setSpectrogram(smallData());

        CsvModalParameters mp = new CsvModalParameters(
                "sample_data_sensor.csv",
                java.nio.charset.StandardCharsets.UTF_8,
                ',',
                false,
                4,
                20,
                java.util.List.of()
        );

        Path dir = Files.createTempDirectory("spec-xls-");
        Path out = dir.resolve("export.xml");
        SpectrogramExcelExport.writeRows(out.toFile(), m, new int[]{0, 1, 2}, mp);
        String xml = Files.readString(out);
        assertTrue(xml.contains("<Workbook"), "excel xml workbook expected");
        assertTrue(xml.contains("modal_title"), "header should contain modal_title");
        assertTrue(xml.contains("sample_data_sensor.csv"), "should contain modal title value");
    }

    private static void testPdfExport() throws Exception {
        SpectrogramTableModel m = new SpectrogramTableModel();
        m.setSpectrogram(smallData());

        CsvModalParameters mp = new CsvModalParameters(
                "sample_data_sensor.csv",
                java.nio.charset.StandardCharsets.UTF_8,
                ',',
                false,
                4,
                20,
                java.util.List.of()
        );

        Path dir = Files.createTempDirectory("spec-pdf-");
        Path out = dir.resolve("export.pdf");
        SpectrogramPdfExport.writeRows(out.toFile(), m, new int[]{0, 1, 2}, mp);
        byte[] pdf = Files.readAllBytes(out);
        String header = new String(pdf, 0, Math.min(pdf.length, 20), java.nio.charset.StandardCharsets.US_ASCII);
        assertTrue(header.startsWith("%PDF"), "pdf header expected");
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
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
}

