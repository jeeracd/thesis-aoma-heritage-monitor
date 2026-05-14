import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class SpectrogramExcelExport {
    private SpectrogramExcelExport() {}

    public static void writeRows(File file, SpectrogramTableModel model, int[] modelRows, CsvModalParameters modalParameters) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        if (model == null) {
            throw new IOException("No data to export.");
        }

        String mpTitle = modalParameters == null ? "" : modalParameters.title();
        String mpEncoding = modalParameters == null ? "" : modalParameters.charset().name();
        String mpDelimiter = modalParameters == null ? "" : String.valueOf(modalParameters.delimiter());

        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<?mso-application progid=\"Excel.Sheet\"?>");
            out.println("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
            out.println("  <Worksheet ss:Name=\"SpectralData\">");
            out.println("    <Table>");

            writeRow(out,
                    cell("String", "modal_title"),
                    cell("String", "modal_encoding"),
                    cell("String", "modal_delimiter"),
                    cell("String", "time_sec"),
                    cell("String", "freq_hz"),
                    cell("String", "amplitude_db"),
                    cell("String", "flag")
            );

            if (modelRows != null) {
                for (int r : modelRows) {
                    double t = model.getTimeSecAtRow(r);
                    double f = model.getFreqHzAtRow(r);
                    double db = model.getDbAtRow(r);
                    String flag = model.getFlagAtRow(r);
                    if (!Double.isFinite(db)) {
                        continue;
                    }
                    writeRow(out,
                            cell("String", mpTitle),
                            cell("String", mpEncoding),
                            cell("String", mpDelimiter),
                            cell("Number", Double.toString(t)),
                            cell("Number", Double.toString(f)),
                            cell("Number", Double.toString(db)),
                            cell("String", flag == null ? "" : flag)
                    );
                }
            }

            out.println("    </Table>");
            out.println("  </Worksheet>");
            out.println("</Workbook>");
        }
    }

    private static void writeRow(PrintWriter out, String... cells) {
        out.println("      <Row>");
        for (String c : cells) {
            out.println(c);
        }
        out.println("      </Row>");
    }

    private static String cell(String type, String value) {
        String v = value == null ? "" : escapeXml(value);
        return "        <Cell><Data ss:Type=\"" + type + "\">" + v + "</Data></Cell>";
    }

    private static String escapeXml(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '&') {
                sb.append("&amp;");
            } else if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '"') {
                sb.append("&quot;");
            } else if (ch == '\'') {
                sb.append("&apos;");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}

