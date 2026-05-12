import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class AppMonitoring {
    private static volatile boolean initialized = false;

    private AppMonitoring() {}

    public static void init() {
        if (initialized) {
            return;
        }
        synchronized (AppMonitoring.class) {
            if (initialized) {
                return;
            }
            try {
                Path logDir = Paths.get(System.getProperty("user.home"), ".aoma-heritage-monitor", "logs");
                Files.createDirectories(logDir);
                Path logFile = logDir.resolve("app.log");

                Logger root = Logger.getLogger("");
                FileHandler fh = new FileHandler(logFile.toString(), true);
                fh.setLevel(Level.INFO);
                fh.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return record.getLevel()
                                + " " + record.getLoggerName()
                                + " - " + record.getMessage()
                                + System.lineSeparator();
                    }
                });
                root.addHandler(fh);
                initialized = true;
            } catch (IOException e) {
                initialized = true;
            }
        }
    }
}

