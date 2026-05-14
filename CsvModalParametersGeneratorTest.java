import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
 
public final class CsvModalParametersGeneratorTest {
    public static void main(String[] args) throws Exception {
        testDelimiterAndTypeInference();
        testUtf8Bom();
        testWindows1252Fallback();
        testBlockFormatParsing();
        testMalformedCsvUnclosedQuote();
        System.out.println("ALL TESTS PASSED");
    }
 
    private static void testDelimiterAndTypeInference() throws Exception {
        Path dir = Files.createTempDirectory("csv-params-");
        Path p = dir.resolve("data.csv");
        String csv = ""
                + "name;count;value;date\n"
                + "alpha;10;3.14;2026-05-12\n"
                + "beta;11;2.0;2026-05-13\n";
        Files.writeString(p, csv, StandardCharsets.UTF_8);
 
        CsvModalParameters mp = CsvModalParametersGenerator.generate(p.toFile(), 100);
        assertTrue(mp.delimiter() == ';', "should detect semicolon delimiter");
        assertTrue(mp.columnCount() == 4, "should infer 4 columns");
        assertTrue(mp.rowCount() == 2, "should count 2 data rows");
 
        assertTrue(mp.fields().get(0).type() == CsvModalParameters.DataType.TEXT, "name should be text");
        assertTrue(mp.fields().get(1).type() == CsvModalParameters.DataType.INTEGER, "count should be integer");
        assertTrue(mp.fields().get(2).type() == CsvModalParameters.DataType.DECIMAL, "value should be decimal");
        assertTrue(mp.fields().get(3).type() == CsvModalParameters.DataType.DATE, "date should be date");
    }
 
    private static void testUtf8Bom() throws Exception {
        Path dir = Files.createTempDirectory("csv-bom-");
        Path p = dir.resolve("bom.csv");
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = "a,b\n1,2\n".getBytes(StandardCharsets.UTF_8);
        byte[] all = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, all, 0, bom.length);
        System.arraycopy(body, 0, all, bom.length, body.length);
        Files.write(p, all);
 
        CsvModalParameters mp = CsvModalParametersGenerator.generate(p.toFile(), 10);
        assertTrue(mp.charset().equals(StandardCharsets.UTF_8), "should detect UTF-8 BOM");
        assertTrue(mp.rowCount() == 1, "should count 1 data row");
    }
 
    private static void testWindows1252Fallback() throws Exception {
        Path dir = Files.createTempDirectory("csv-1252-");
        Path p = dir.resolve("win1252.csv");
 
        byte[] bytes = new byte[]{
                'n', 'a', 'm', 'e', ',', 'n', 'o', 't', 'e', '\n',
                'a', ',', (byte) 0xE9, '\n'
        };
        Files.write(p, bytes);
 
        CsvModalParameters mp = CsvModalParametersGenerator.generate(p.toFile(), 10);
        Charset cs = mp.charset();
        assertTrue(cs.name().toLowerCase().contains("1252") || cs.equals(StandardCharsets.ISO_8859_1), "should fall back from UTF-8");
        assertTrue(mp.fields().get(1).type() == CsvModalParameters.DataType.TEXT, "note should be text");
    }
 
    private static void testBlockFormatParsing() throws Exception {
        Path dir = Files.createTempDirectory("csv-block-");
        Path p = dir.resolve("block.csv");
        String txt = ""
                + "timestamp\n"
                + "accelX\n"
                + "accelY\n"
                + "accelZ\n"
                + "########\n"
                + "0.1\n"
                + "0.2\n"
                + "9.8\n"
                + "########\n"
                + "0.2\n"
                + "0.0\n"
                + "9.7\n";
        Files.writeString(p, txt, StandardCharsets.UTF_8);
 
        CsvModalParameters mp = CsvModalParametersGenerator.generate(p.toFile(), 10);
        assertTrue(mp.blockFormat(), "should detect block format");
        assertTrue(mp.columnCount() == 3, "block should reconcile headers to data width");
        assertTrue(mp.rowCount() == 2, "should parse two records");
        assertTrue(mp.fields().get(0).csvHeader().equals("accelX"), "should drop unused timestamp header");
        assertTrue(mp.fields().get(0).type() == CsvModalParameters.DataType.DECIMAL, "accelX numeric should be decimal");
    }
 
    private static void testMalformedCsvUnclosedQuote() throws Exception {
        Path dir = Files.createTempDirectory("csv-bad-");
        Path p = dir.resolve("bad.csv");
        String txt = "a,b\n\"x,y\n";
        Files.writeString(p, txt, StandardCharsets.UTF_8);
 
        boolean threw = false;
        try {
            CsvModalParametersGenerator.generate(p.toFile(), 10);
        } catch (Exception e) {
            threw = true;
            assertTrue(e.getMessage() != null && e.getMessage().toLowerCase().contains("unclosed"), "should report unclosed quote");
        }
        assertTrue(threw, "malformed CSV should throw");
    }
 
    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}
