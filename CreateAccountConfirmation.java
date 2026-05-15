import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class CreateAccountConfirmation extends JFrame {
    private static final Color BG = new Color(248, 250, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(15, 23, 42);
    private static final Color MUTED = new Color(71, 85, 105);
    private static final Color BORDER = new Color(203, 213, 225);

    private static final Font H2 = new Font("SansSerif", Font.BOLD, 16);
    private static final Font BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);

    public CreateAccountConfirmation() {
        setTitle("Account Created");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 260));
        setPreferredSize(new Dimension(520, 300));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new RoundedBorder(14, BORDER, 1), new EmptyBorder(22, 22, 22, 22)));

        JLabel title = new JLabel("Account created successfully");
        title.setFont(H2);
        title.setForeground(TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel msg = new JLabel("<html><div style='text-align:center;width:420px'>You can now return to the login screen and sign in using your credentials.</div></html>");
        msg.setFont(BODY);
        msg.setForeground(MUTED);
        msg.setHorizontalAlignment(SwingConstants.CENTER);

        JButton proceed = new JButton("Back to login");
        proceed.setFont(BODY_BOLD);
        proceed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        proceed.setForeground(Color.WHITE);
        proceed.setBackground(new Color(37, 99, 235));
        proceed.setFocusPainted(false);
        proceed.setBorder(new CompoundBorder(new RoundedBorder(12, new Color(37, 99, 235), 1), new EmptyBorder(12, 14, 12, 14)));
        proceed.addActionListener(e -> {
            new UsersLoginOptions().setVisible(true);
            dispose();
        });

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(title, BorderLayout.NORTH);
        center.add(msg, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        bottom.add(proceed, BorderLayout.CENTER);

        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        pack();
        setVisible(true);
    }

    private static final class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private final int thickness;

        private RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = thickness;
            insets.right = thickness;
            insets.top = thickness;
            insets.bottom = thickness;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            int off = thickness / 2;
            g2.drawRoundRect(x + off, y + off, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateAccountConfirmation().setVisible(true));
    }
}
