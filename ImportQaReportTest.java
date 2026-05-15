import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ImportQaReportTest {
    public static void main(String[] args) throws Exception {
        testKpiReportArtifacts();
        System.out.println("ImportQaReportTest OK");
    }

    private static void testKpiReportArtifacts() throws Exception {
        Path dir = Files.createTempDirectory("qa_out_");
        Path csv = Files.createTempFile("qa_in_", ".csv");
        String content = String.join(
                "\n",
                "timestamp,mode_active,mode_label,anomaly_flag,event_marker,sensor_health,dominant_freq_hz,damping_ratio",
                "2026-01-01 00:00:00,1,Idle / Ambient,0,NONE,OK,1.80,0.010",
                "2026-01-01 00:01:00,1,Idle / Ambient,0,NONE,OK,1.81,0.010",
                "2026-01-01 00:03:00,2,Low Occupancy,1,SENSOR_GLITCH,FAIL,2.40,0.012",
                ""
        );
        Files.writeString(csv, content, StandardCharsets.UTF_8);

        ImportQaRules rules = new ImportQaRules(0.0, 50.0, java.util.List.of("SENSOR_GLITCH"), true, 10000, 60000.0, 0.5);
        CsvFileValidator.QaArtifacts a = CsvFileValidator.writeImportQaReport(csv.toFile(), dir, rules);

        assertTrue(Files.isRegularFile(a.jsonPath()), "json report should exist");
        assertTrue(Files.isRegularFile(a.csvPath()), "csv report should exist");

        String json = Files.readString(a.jsonPath(), StandardCharsets.UTF_8);
        assertTrue(json.contains("\"profile\":\"KPI_LOG\""), "profile should be KPI_LOG");
        assertTrue(json.contains("TIME_GAP_PERCENT"), "should include gap check");
        assertTrue(json.contains("MODE8_PRESENT"), "should include mode8 check");
        assertTrue(json.contains("EVENT_SENSOR_GLITCH"), "should include required event check");
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

