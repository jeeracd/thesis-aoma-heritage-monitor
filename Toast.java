import javax.swing.*;
import java.awt.*;

public final class Toast {
    private Toast() {}

    public static void show(Window owner, String message, Color background, int durationMs) {
        if (owner == null) {
            return;
        }
        JWindow window = new JWindow(owner);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        panel.setBackground(background == null ? new Color(60, 60, 60) : background);

        JLabel label = new JLabel(message == null ? "" : message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(label, BorderLayout.CENTER);

        window.setAlwaysOnTop(true);
        window.setContentPane(panel);
        window.pack();

        int x = owner.getX() + owner.getWidth() - window.getWidth() - 20;
        int y = owner.getY() + owner.getHeight() - window.getHeight() - 40;
        window.setLocation(Math.max(x, 0), Math.max(y, 0));

        window.setVisible(true);

        Timer t = new Timer(Math.max(durationMs, 800), e -> window.dispose());
        t.setRepeats(false);
        t.start();
    }
}

