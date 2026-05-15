import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

public final class UiErrorBoundary {
    private UiErrorBoundary() {}

    public static JComponent wrap(String label, Supplier<JComponent> supplier) {
        try {
            JComponent c = supplier == null ? null : supplier.get();
            return c == null ? new JPanel() : c;
        } catch (Throwable t) {
            return fallback(label, t);
        }
    }

    private static JComponent fallback(String label, Throwable t) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        String title = (label == null || label.isBlank()) ? "Module Error" : label.trim() + " Error";
        JLabel header = new JLabel(title);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        String msg = t == null ? "Unknown error" : (t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage());
        JLabel body = new JLabel(msg);

        JTextArea details = new JTextArea(stackTrace(t));
        details.setEditable(false);
        details.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JButton copy = new JButton("Copy Details");
        copy.addActionListener(e -> {
            StringSelection s = new StringSelection(details.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
        });

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(header, BorderLayout.NORTH);
        top.add(body, BorderLayout.CENTER);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(details), BorderLayout.CENTER);
        p.add(copy, BorderLayout.SOUTH);

        return p;
    }

    private static String stackTrace(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
