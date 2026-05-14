import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class SimplePdfWriter {
    private SimplePdfWriter() {}

    public static void writeTextPage(File file, String title, List<String> lines) throws IOException {
        if (file == null) {
            throw new IOException("No file selected.");
        }
        String t = title == null ? "" : title;
        List<String> ls = lines == null ? List.of() : lines;

        StringBuilder content = new StringBuilder();
        content.append("BT\n");
        content.append("/F1 10 Tf\n");
        content.append("72 760 Td\n");
        if (!t.isEmpty()) {
            content.append("(").append(escapePdf(t)).append(") Tj\n");
            content.append("T*\n");
            content.append("T*\n");
        }
        for (String line : ls) {
            String s = line == null ? "" : line;
            content.append("(").append(escapePdf(s)).append(") Tj\n");
            content.append("T*\n");
        }
        content.append("ET\n");

        byte[] streamBytes = content.toString().getBytes(StandardCharsets.US_ASCII);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeAscii(out, "%PDF-1.4\n");

        int[] offsets = new int[6];
        offsets[1] = out.size();
        writeAscii(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets[2] = out.size();
        writeAscii(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets[3] = out.size();
        writeAscii(out, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n");

        offsets[4] = out.size();
        writeAscii(out, "4 0 obj\n<< /Length ");
        writeAscii(out, Integer.toString(streamBytes.length));
        writeAscii(out, " >>\nstream\n");
        out.write(streamBytes);
        writeAscii(out, "endstream\nendobj\n");

        offsets[5] = out.size();
        writeAscii(out, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>\nendobj\n");

        int xrefPos = out.size();
        writeAscii(out, "xref\n0 6\n");
        writeAscii(out, "0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            writeAscii(out, pad10(offsets[i]) + " 00000 n \n");
        }

        writeAscii(out, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n");
        writeAscii(out, Integer.toString(xrefPos));
        writeAscii(out, "\n%%EOF\n");

        Files.write(file.toPath(), out.toByteArray());
    }

    private static void writeAscii(ByteArrayOutputStream out, String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.US_ASCII));
    }

    private static String pad10(int n) {
        String s = Integer.toString(n);
        StringBuilder sb = new StringBuilder();
        for (int i = s.length(); i < 10; i++) {
            sb.append('0');
        }
        sb.append(s);
        return sb.toString();
    }

    private static String escapePdf(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(' || ch == ')' || ch == '\\') {
                sb.append('\\').append(ch);
            } else if (ch == '\n' || ch == '\r') {
                sb.append(' ');
            } else if (ch < 32 || ch > 126) {
                sb.append('?');
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}

