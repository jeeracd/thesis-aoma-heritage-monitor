import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

public final class ComplianceRules {

    public final double maxFrequencyShiftRatioWarn;
    public final double maxFrequencyShiftRatioFail;
    public final double matchFrequencyToleranceHz;
    public final double maxFnWarnHz;
    public final double maxFnFailHz;
    public final double minDampingWarn;
    public final double minDampingFail;

    public ComplianceRules(
            double maxFrequencyShiftRatioWarn,
            double maxFrequencyShiftRatioFail,
            double matchFrequencyToleranceHz,
            double maxFnWarnHz,
            double maxFnFailHz,
            double minDampingWarn,
            double minDampingFail) {
        this.maxFrequencyShiftRatioWarn = maxFrequencyShiftRatioWarn;
        this.maxFrequencyShiftRatioFail = maxFrequencyShiftRatioFail;
        this.matchFrequencyToleranceHz = matchFrequencyToleranceHz;
        this.maxFnWarnHz = maxFnWarnHz;
        this.maxFnFailHz = maxFnFailHz;
        this.minDampingWarn = minDampingWarn;
        this.minDampingFail = minDampingFail;
    }

    private static Path globalPath() {
        return Path.of(System.getProperty("user.home"),
                ".aoma-heritage-monitor", "compliance-rules.properties");
    }

    public static Path ensureGlobalDefault() throws IOException {
        Path p = globalPath();
        Files.createDirectories(p.getParent());
        if (!Files.exists(p)) {
            Files.writeString(p, defaultContent(), StandardCharsets.UTF_8);
        }
        return p;
    }

    public static Path ensurePerOutDir(Path outDir) throws IOException {
        Path global = ensureGlobalDefault();
        Path perDir = outDir.resolve("compliance-rules.properties");
        if (!Files.exists(perDir)) {
            Files.copy(global, perDir, StandardCopyOption.REPLACE_EXISTING);
        }
        return perDir;
    }

    public static ComplianceRules load(Path outDir) throws IOException {
        Path path = outDir.resolve("compliance-rules.properties");
        if (!Files.exists(path)) {
            path = ensureGlobalDefault();
        }
        Properties props = new Properties();
        try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            props.load(r);
        }
        return new ComplianceRules(
                parseDouble(props, "maxFrequencyShiftRatioWarn", 0.05),
                parseDouble(props, "maxFrequencyShiftRatioFail", 0.10),
                parseDouble(props, "matchFrequencyToleranceHz", 0.5),
                parseDouble(props, "maxFnWarnHz", 10.0),
                parseDouble(props, "maxFnFailHz", 20.0),
                parseDouble(props, "minDampingWarn", 0.01),
                parseDouble(props, "minDampingFail", 0.005)
        );
    }

    private static double parseDouble(Properties p, String key, double def) {
        try {
            return Double.parseDouble(p.getProperty(key, String.valueOf(def)));
        } catch (Exception e) {
            return def;
        }
    }

    private static String defaultContent() {
        return "# NSCP/NBC-based compliance thresholds for heritage building OMA\n" +
               "maxFrequencyShiftRatioWarn=0.05\n" +
               "maxFrequencyShiftRatioFail=0.10\n" +
               "matchFrequencyToleranceHz=0.5\n" +
               "maxFnWarnHz=10.0\n" +
               "maxFnFailHz=20.0\n" +
               "minDampingWarn=0.01\n" +
               "minDampingFail=0.005\n";
    }
}
