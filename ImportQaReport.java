import java.util.List;
import java.util.Map;

public record ImportQaReport(
        CsvFileValidator.CsvProfile profile,
        String sourceCsv,
        int rowsScanned,
        int columns,
        String startTimestamp,
        String endTimestamp,
        int timestampGaps,
        double gapPercent,
        List<Integer> modesPresent,
        int anomalyRows,
        int sensorWarnRows,
        int sensorFailRows,
        Map<String, Integer> eventCounts,
        List<Check> checks,
        double qualityScore
) {
    public record Check(
            String id,
            String description,
            String threshold,
            String actual,
            boolean passed,
            String details
    ) {
    }
}

