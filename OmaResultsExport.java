import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class OmaResultsExport {
    private OmaResultsExport() {}

    public static void writeCsv(File file, List<OmaResultsModel.ModeRow> rows) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        List<OmaResultsModel.ModeRow> rs = rows == null ? List.of() : rows;
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            out.println("mode_index,frequency_hz,damping_ratio,mpc,mpd,phi_accelX,phi_accelY,phi_accelZ,severity");
            for (OmaResultsModel.ModeRow r : rs) {
                out.print(r.modeIndex());
                out.print(",");
                out.print(r.frequencyHz());
                out.print(",");
                out.print(r.dampingRatio());
                out.print(",");
                out.print(r.mpc());
                out.print(",");
                out.print(r.mpd());
                out.print(",");
                out.print(r.phiAccelX());
                out.print(",");
                out.print(r.phiAccelY());
                out.print(",");
                out.print(r.phiAccelZ());
                out.print(",");
                out.println(r.severity().name());
            }
        }
    }

    public static void writeExcelXml(File file, List<OmaResultsModel.ModeRow> rows, String sheetName) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        List<OmaResultsModel.ModeRow> rs = rows == null ? List.of() : rows;
        String sn = sheetName == null || sheetName.isBlank() ? "OmaResults" : sheetName.trim();
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<?mso-application progid=\"Excel.Sheet\"?>");
            out.println("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
            out.println("  <Worksheet ss:Name=\"" + escapeXml(sn) + "\">");
            out.println("    <Table>");
            writeRow(out,
                    cell("String", "mode_index"),
                    cell("String", "frequency_hz"),
                    cell("String", "damping_ratio"),
                    cell("String", "mpc"),
                    cell("String", "mpd"),
                    cell("String", "phi_accelX"),
                    cell("String", "phi_accelY"),
                    cell("String", "phi_accelZ"),
                    cell("String", "severity")
            );
            for (OmaResultsModel.ModeRow r : rs) {
                writeRow(out,
                        cell("Number", Integer.toString(r.modeIndex())),
                        cell("Number", Double.toString(r.frequencyHz())),
                        cell("Number", Double.toString(r.dampingRatio())),
                        cell("Number", Double.toString(r.mpc())),
                        cell("Number", Double.toString(r.mpd())),
                        cell("Number", Double.toString(r.phiAccelX())),
                        cell("Number", Double.toString(r.phiAccelY())),
                        cell("Number", Double.toString(r.phiAccelZ())),
                        cell("String", r.severity().name())
                );
            }
            out.println("    </Table>");
            out.println("  </Worksheet>");
            out.println("</Workbook>");
        }
    }

    public static void writePdfSummary(File file, OmaResultsModel model, List<OmaResultsModel.ModeRow> rows) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        OmaResultsModel m = model == null ? OmaResultsModel.loadFromDirOrEmpty(null) : model;
        List<OmaResultsModel.ModeRow> rs = rows == null ? List.of() : rows;

        List<String> lines = new ArrayList<>();
        if (m.outDir() != null) {
            lines.add("Source: " + m.outDir());
        }
        lines.add("Modes: " + rs.size());
        lines.add("Issues: " + m.issuesCount());
        lines.add("");
        lines.add("mode | frequency_hz | damping_ratio | mpc | mpd | severity");
        for (OmaResultsModel.ModeRow r : rs) {
            lines.add(r.modeIndex()
                    + " | "
                    + r.frequencyHz()
                    + " | "
                    + r.dampingRatio()
                    + " | "
                    + r.mpc()
                    + " | "
                    + r.mpd()
                    + " | "
                    + r.severity().name());
        }

        SimplePdfWriter.writeTextPage(file, "OMA Analysis Results (CAD UI)", lines);
    }

    public static void writeDxfFrequencyPlot(File file, CadViewportPanel viewport) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        if (viewport == null) {
            throw new IOException("No viewport to export.");
        }
        writeDxfPlot(file, viewport.getModel(), viewport.getViewType(), viewport.getAnnotations());
    }

    public static void writeDxfPlot(File file, OmaResultsModel model, CadViewportPanel.ViewType viewType, List<CadViewportPanel.Annotation> annotations) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        OmaResultsModel m = model == null ? OmaResultsModel.loadFromDirOrEmpty(null) : model;
        List<OmaResultsModel.ModeRow> rows = m.modes();
        if (rows.isEmpty()) {
            throw new IOException("No results to export.");
        }

        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.US_ASCII))) {
            out.println("0");
            out.println("SECTION");
            out.println("2");
            out.println("HEADER");
            out.println("0");
            out.println("ENDSEC");
            out.println("0");
            out.println("SECTION");
            out.println("2");
            out.println("ENTITIES");

            CadViewportPanel.ViewType vt = viewType == null ? CadViewportPanel.ViewType.FREQUENCY : viewType;
            String layer = vt == CadViewportPanel.ViewType.FREQUENCY ? "FREQUENCY_HZ" : "DAMPING_RATIO";
            double yScale = vt == CadViewportPanel.ViewType.FREQUENCY ? 1.0 : 1000.0;

            for (int i = 0; i < rows.size(); i++) {
                OmaResultsModel.ModeRow r = rows.get(i);
                double x = r.modeIndex();
                double y = (vt == CadViewportPanel.ViewType.FREQUENCY ? r.frequencyHz() : r.dampingRatio() * yScale);
                writeCircle(out, layer, x, y, 0.15);
                writeText(out, layer, x + 0.25, y + 0.25, 0.25,
                        vt == CadViewportPanel.ViewType.FREQUENCY ? String.format("%.4fHz", r.frequencyHz()) : String.format("%.4f", r.dampingRatio()));
            }

            if (annotations != null) {
                for (CadViewportPanel.Annotation a : annotations) {
                    double ax = a.x();
                    double ay = a.y();
                    if (vt == CadViewportPanel.ViewType.DAMPING) {
                        ay = ay * yScale;
                    }
                    writeText(out, "ANNOTATIONS", ax, ay, 0.30, a.text());
                }
            }

            out.println("0");
            out.println("ENDSEC");
            out.println("0");
            out.println("EOF");
        }
    }

    private static void writeCircle(PrintWriter out, String layer, double x, double y, double r) {
        out.println("0");
        out.println("CIRCLE");
        out.println("8");
        out.println(layer);
        out.println("10");
        out.println(x);
        out.println("20");
        out.println(y);
        out.println("30");
        out.println(0);
        out.println("40");
        out.println(r);
    }

    private static void writeText(PrintWriter out, String layer, double x, double y, double h, String text) {
        out.println("0");
        out.println("TEXT");
        out.println("8");
        out.println(layer);
        out.println("10");
        out.println(x);
        out.println("20");
        out.println(y);
        out.println("30");
        out.println(0);
        out.println("40");
        out.println(h);
        out.println("1");
        out.println(text == null ? "" : text.replace('\n', ' '));
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
