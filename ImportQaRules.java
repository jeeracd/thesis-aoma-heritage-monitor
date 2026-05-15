import java.util.List;
import java.util.prefs.Preferences;

public record ImportQaRules(
        double maxGapPercent,
        double maxSensorFailPercent,
        List<String> requiredEventMarkers,
        boolean requireMode8,
        int maxRowsToScan,
        Double expectedIntervalMs,
        double intervalTolerancePercent
) {
    public static ImportQaRules defaults() {
        return new ImportQaRules(
                0.0,
                0.2,
                List.of(),
                false,
                200_000,
                null,
                0.5
        );
    }

    public static ImportQaRules loadFromPreferences() {
        Preferences p = Preferences.userNodeForPackage(ImportQaRules.class).node("import_qa_rules");
        double maxGap = p.getDouble("maxGapPercent", defaults().maxGapPercent());
        double maxFail = p.getDouble("maxSensorFailPercent", defaults().maxSensorFailPercent());
        boolean requireMode8 = p.getBoolean("requireMode8", defaults().requireMode8());
        int maxRows = p.getInt("maxRowsToScan", defaults().maxRowsToScan());
        double tol = p.getDouble("intervalTolerancePercent", defaults().intervalTolerancePercent());
        String req = p.get("requiredEventMarkers", "");
        List<String> markers = req.isBlank() ? List.of() : List.of(req.split("\\s*,\\s*"));
        String interval = p.get("expectedIntervalMs", "");
        Double expectedInterval = null;
        if (!interval.isBlank()) {
            try {
                expectedInterval = Double.parseDouble(interval.trim());
            } catch (Exception ignored) {
            }
        }
        return new ImportQaRules(maxGap, maxFail, markers, requireMode8, maxRows, expectedInterval, tol);
    }
}

