import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public final class AppScrollBarUI extends BasicScrollBarUI {
    private final Color thumb = new Color(150, 190, 240);
    private final Color thumbHover = new Color(120, 170, 235);
    private final Color track = new Color(240, 240, 240);
    private final int thickness;
    private boolean hovering;

    public AppScrollBarUI() {
        this(10);
    }

    public AppScrollBarUI(int thickness) {
        this.thickness = Math.max(8, thickness);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        scrollbar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = thumbRect.contains(e.getPoint());
                scrollbar.repaint();
            }
        });
        scrollbar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovering = false;
                scrollbar.repaint();
            }
        });
    }

    @Override
    protected void configureScrollBarColors() {
        scrollbar.setBackground(track);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(thickness, 28);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(thickness, super.getPreferredSize(c).height);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(track);
        g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovering ? thumbHover : thumb);

        int arc = 10;
        int pad = 2;
        g2.fillRoundRect(
                thumbBounds.x + pad,
                thumbBounds.y + pad,
                thumbBounds.width - pad * 2,
                thumbBounds.height - pad * 2,
                arc,
                arc
        );
        g2.dispose();
    }
}

