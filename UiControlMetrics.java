import javax.swing.*;
import java.awt.*;

public final class UiControlMetrics {
    public static final Font CONTROL_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final int CONTROL_HEIGHT = 26;
    public static final int ROW_HEIGHT = 32;
    public static final int HGAP = 10;
    public static final int VGAP = 4;

    private UiControlMetrics() {}

    public static void applyControlFont(Component... components) {
        if (components == null) {
            return;
        }
        for (Component c : components) {
            if (c == null) {
                continue;
            }
            c.setFont(CONTROL_FONT);
            if (c instanceof JSpinner sp) {
                JComponent ed = sp.getEditor();
                if (ed != null) {
                    ed.setFont(CONTROL_FONT);
                }
            }
            if (c instanceof JComboBox<?> cb) {
                Component r = cb.getRenderer() instanceof Component rc ? rc : null;
                if (r != null) {
                    r.setFont(CONTROL_FONT);
                }
            }
        }
    }

    public static void setRowMaxHeight(JComponent row) {
        if (row == null) {
            return;
        }
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
    }

    public static void setPreferredHeight(JComponent c, int height) {
        if (c == null) {
            return;
        }
        Dimension d = c.getPreferredSize();
        int w = d == null ? 0 : d.width;
        c.setPreferredSize(new Dimension(w, height));
    }
}

