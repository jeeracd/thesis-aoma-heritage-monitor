import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CsvFileValidator {
    public static final long MAX_BYTES = 50L * 1024L * 1024L;

    private CsvFileValidator() {}

    public static ValidationResult validate(File file) {
        if (file == null) {
            return ValidationResult.error("No file selected.");
        }
        Path p = file.toPath();
        if (!Files.exists(p)) {
            return ValidationResult.error("Selected file does not exist.");
        }
        if (!Files.isRegularFile(p)) {
            return ValidationResult.error("Selected path is not a file.");
        }
        if (!Files.isReadable(p)) {
            return ValidationResult.error("Permission denied: file is not readable.");
        }
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".csv")) {
            return ValidationResult.error("Please select a .csv file.");
        }
        try {
            long size = Files.size(p);
            if (size > MAX_BYTES) {
                return ValidationResult.error("File exceeds 50MB limit.");
            }
        } catch (IOException e) {
            return ValidationResult.error("Unable to read file size.");
        }
        return ValidationResult.success();
    }

    public record ValidationResult(boolean valid, String message) {
        public static ValidationResult success() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message == null ? "" : message);
        }
    }
}

