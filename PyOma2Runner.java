import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class PyOma2Runner {
    public record RunResult(boolean ok, String message, Properties summary) {}

    private PyOma2Runner() {}

    public static RunResult run(File csvFile, Path outDir, Double fsHz) {
        if (csvFile == null || !csvFile.exists() || !csvFile.isFile()) {
            return new RunResult(false, "CSV file not found.", new Properties());
        }
        try {
            Files.createDirectories(outDir);
        } catch (IOException e) {
            return new RunResult(false, "Failed to create output folder: " + e.getMessage(), new Properties());
        }

        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add(Path.of(System.getProperty("user.dir"), "pyoma2_oma_results.py").toAbsolutePath().toString());
        cmd.add("--csv");
        cmd.add(csvFile.getAbsolutePath());
        cmd.add("--out");
        cmd.add(outDir.toAbsolutePath().toString());
        if (fsHz != null) {
            cmd.add("--fs");
            cmd.add(String.valueOf(fsHz));
        }

        StringBuilder output = new StringBuilder();
        int code;
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            code = p.waitFor();
        } catch (Exception e) {
            return new RunResult(false, "Failed to run PyOMA2 pipeline: " + e.getMessage(), new Properties());
        }

        Path propsPath = outDir.resolve("summary.properties");
        Properties props = new Properties();
        if (Files.exists(propsPath)) {
            try (var in = Files.newInputStream(propsPath)) {
                props.load(in);
            } catch (IOException ignored) {
            }
        }

        String status = props.getProperty("status");
        if (code == 0 && "ok".equalsIgnoreCase(status)) {
            return new RunResult(true, "PyOMA2 results generated.", props);
        }

        String msg = props.getProperty("message");
        if (msg == null || msg.isBlank()) {
            msg = output.isEmpty() ? "PyOMA2 pipeline failed." : output.toString().trim();
        }
        return new RunResult(false, msg, props);
    }
}

