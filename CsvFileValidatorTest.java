import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CsvFileValidatorTest {
    public static void main(String[] args) throws Exception {
        testCancelNull();
        testValidCsv();
        testWrongExtension();
        testMissingFile();
        testTooLargeFile();
        testNotAFile();
        testPermissionDeniedIfSupported();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testCancelNull() {
        CsvFileValidator.ValidationResult r = CsvFileValidator.validate(null);
        assertFalse(r.valid(), "null selection should fail");
    }

    private static void testValidCsv() throws Exception {
        Path dir = Files.createTempDirectory("csv-validate-");
        Path f = dir.resolve("ok.csv");
        Files.writeString(f, "a,b\n1,2\n");
        CsvFileValidator.ValidationResult r = CsvFileValidator.validate(f.toFile());
        assertTrue(r.valid(), "valid csv should pass");
    }

    private static void testWrongExtension() throws Exception {
        Path dir = Files.createTempDirectory("csv-validate-");
        Path f = dir.resolve("bad.txt");
        Files.writeString(f, "a,b\n1,2\n");
        CsvFileValidator.ValidationResult r = CsvFileValidator.validate(f.toFile());
        assertFalse(r.valid(), "wrong extension should fail");
    }

    private static void testMissingFile() {
        File f = new File("Z:\\this-path-should-not-exist\\missing.csv");
        CsvFileValidator.ValidationResult r = CsvFileValidator.validate(f);
        assertFalse(r.valid(), "missing file should fail");
    }

    private static void testTooLargeFile() throws Exception {
        Path dir = Files.createTempDirectory("csv-validate-");
        Path f = dir.resolve("big.csv");
        try (RandomAccessFile raf = new RandomAccessFile(f.toFile(), "rw")) {
            raf.setLength(CsvFileValidator.MAX_BYTES + 1);
        }
        CsvFileValidator.ValidationResult r = CsvFileValidator.validate(f.toFile());
        assertFalse(r.valid(), "too large file should fail");
    }

    private static void testNotAFile() throws Exception {
        Path dir = Files.createTempDirectory("csv-validate-");
        CsvFileValidator.ValidationResult r = CsvFileValidator.validate(dir.toFile());
        assertFalse(r.valid(), "directory selection should fail");
    }

    private static void testPermissionDeniedIfSupported() throws Exception {
        Path dir = Files.createTempDirectory("csv-validate-");
        Path f = dir.resolve("deny.csv");
        Files.writeString(f, "a,b\n1,2\n");

        File file = f.toFile();
        file.setReadable(false, false);

        if (!Files.isReadable(f)) {
            CsvFileValidator.ValidationResult r = CsvFileValidator.validate(file);
            assertFalse(r.valid(), "unreadable file should fail");
        }
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }

    private static void assertFalse(boolean ok, String msg) {
        if (ok) {
            throw new AssertionError(msg);
        }
    }
}

