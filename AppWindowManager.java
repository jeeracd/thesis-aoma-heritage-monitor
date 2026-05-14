import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AppWindowManager {
    public record Entry(String label, WeakReference<Window> windowRef, long createdAtMillis) {}

    private static final Object LOCK = new Object();
    private static final List<Entry> WINDOWS = new ArrayList<>();
    private static volatile boolean recoveryDialogOpen = false;

    private AppWindowManager() {}

    public static void register(JFrame frame, RoleMenuBar.Role role) {
        if (frame == null) {
            return;
        }
        String base = frame.getTitle();
        String label = (base == null || base.isBlank()) ? frame.getClass().getSimpleName() : base;
        if (role != null) {
            label = label + " [" + role.name() + "]";
        }
        register(frame, label);
    }

    public static void register(Window window, String label) {
        if (window == null) {
            return;
        }
        String l = label == null ? "" : label.trim();
        if (l.isEmpty()) {
            l = window.getClass().getSimpleName();
        }
        synchronized (LOCK) {
            cleanupLocked();
            WINDOWS.add(new Entry(l, new WeakReference<>(window), System.currentTimeMillis()));
        }

        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                cleanup();
                ensureRecoveryDialogIfAllHidden();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                cleanup();
            }
        });
    }

    public static void showHiddenWindowsDialog(Window owner) {
        List<Entry> hidden = getHiddenWindows();

        DefaultListModel<Entry> model = new DefaultListModel<>();
        for (Entry e : hidden) {
            model.addElement(e);
        }

        JList<Entry> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Entry e) {
                    setText(e.label());
                }
                return c;
            }
        });

        JButton restore = new JButton("Restore");
        restore.setMnemonic('R');
        restore.addActionListener(e -> {
            Entry sel = list.getSelectedValue();
            if (sel == null) {
                return;
            }
            if (!restoreEntry(owner, sel)) {
                model.removeElement(sel);
            }
        });

        JButton restoreAll = new JButton("Restore All");
        restoreAll.setMnemonic('A');
        restoreAll.addActionListener(e -> {
            List<Entry> toRestore = new ArrayList<>();
            for (int i = 0; i < model.size(); i++) {
                toRestore.add(model.get(i));
            }
            for (Entry ent : toRestore) {
                restoreEntry(owner, ent);
            }
            model.clear();
        });

        JButton refresh = new JButton("Refresh");
        refresh.setMnemonic('F');
        refresh.addActionListener(e -> {
            model.clear();
            for (Entry ent : getHiddenWindows()) {
                model.addElement(ent);
            }
        });

        JButton close = new JButton("Close");
        close.setMnemonic('C');

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(refresh);
        buttons.add(restore);
        buttons.add(restoreAll);
        buttons.add(close);

        JLabel header = new JLabel("Hidden Windows");
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        top.add(header, BorderLayout.WEST);

        JPanel body = new JPanel(new BorderLayout());
        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        body.add(new JScrollPane(list), BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(owner, "Hidden Windows", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(top, BorderLayout.NORTH);
        dialog.add(body, BorderLayout.CENTER);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(owner);
        close.addActionListener(e -> dialog.dispose());

        dialog.getRootPane().setDefaultButton(restore);
        dialog.setVisible(true);
    }

    public static List<Entry> getHiddenWindows() {
        synchronized (LOCK) {
            cleanupLocked();
            List<Entry> out = new ArrayList<>();
            for (Entry e : WINDOWS) {
                Window w = e.windowRef().get();
                if (w == null) {
                    continue;
                }
                if (!w.isDisplayable()) {
                    continue;
                }
                if (!w.isVisible()) {
                    out.add(e);
                }
            }
            return out;
        }
    }

    private static void ensureRecoveryDialogIfAllHidden() {
        if (recoveryDialogOpen) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (recoveryDialogOpen) {
                return;
            }
            int visible = 0;
            int hidden = 0;
            List<Entry> hiddenEntries = new ArrayList<>();
            synchronized (LOCK) {
                cleanupLocked();
                for (Entry e : WINDOWS) {
                    Window w = e.windowRef().get();
                    if (w == null || !w.isDisplayable()) {
                        continue;
                    }
                    if (w.isVisible()) {
                        visible++;
                    } else {
                        hidden++;
                        hiddenEntries.add(e);
                    }
                }
            }
            if (visible > 0 || hidden == 0) {
                return;
            }
            recoveryDialogOpen = true;

            DefaultListModel<Entry> model = new DefaultListModel<>();
            for (Entry e : hiddenEntries) {
                model.addElement(e);
            }

            JList<Entry> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Entry e) {
                        setText(e.label());
                    }
                    return c;
                }
            });
            if (!model.isEmpty()) {
                list.setSelectedIndex(0);
            }

            JButton restore = new JButton("Restore Selected");
            restore.setMnemonic('R');
            restore.addActionListener(ev -> {
                Entry sel = list.getSelectedValue();
                if (sel == null) {
                    return;
                }
                restoreEntry(null, sel);
            });

            JButton restoreAll = new JButton("Restore All");
            restoreAll.setMnemonic('A');
            restoreAll.addActionListener(ev -> {
                for (int i = 0; i < model.size(); i++) {
                    restoreEntry(null, model.get(i));
                }
            });

            JButton close = new JButton("Close");
            close.setMnemonic('C');

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            buttons.add(restore);
            buttons.add(restoreAll);
            buttons.add(close);

            JLabel header = new JLabel("All windows are hidden. Restore one to continue.");
            header.setFont(new Font("Arial", Font.BOLD, 13));
            JPanel top = new JPanel(new BorderLayout());
            top.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
            top.add(header, BorderLayout.CENTER);

            JPanel body = new JPanel(new BorderLayout());
            body.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            body.add(new JScrollPane(list), BorderLayout.CENTER);
            body.add(buttons, BorderLayout.SOUTH);

            JDialog dialog = new JDialog((Window) null, "Recovery", Dialog.ModalityType.MODELESS);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setLayout(new BorderLayout());
            dialog.add(top, BorderLayout.NORTH);
            dialog.add(body, BorderLayout.CENTER);
            dialog.setSize(520, 420);
            dialog.setLocationRelativeTo(null);
            dialog.getRootPane().setDefaultButton(restore);
            close.addActionListener(ev -> dialog.dispose());
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    recoveryDialogOpen = false;
                }
            });
            dialog.setVisible(true);
        });
    }

    private static boolean restoreEntry(Window owner, Entry entry) {
        Window w = entry.windowRef().get();
        if (w == null || !w.isDisplayable()) {
            JOptionPane.showMessageDialog(owner, "This window can no longer be restored.", "Restore", JOptionPane.WARNING_MESSAGE);
            cleanup();
            return false;
        }
        try {
            w.setVisible(true);
            w.toFront();
            if (w instanceof Frame f) {
                f.setState(Frame.NORMAL);
            }
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage() == null ? "Failed to restore window." : ex.getMessage(), "Restore Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void cleanup() {
        synchronized (LOCK) {
            cleanupLocked();
        }
    }

    private static void cleanupLocked() {
        for (Iterator<Entry> it = WINDOWS.iterator(); it.hasNext();) {
            Entry e = it.next();
            Window w = e.windowRef().get();
            if (w == null) {
                it.remove();
                continue;
            }
            if (!w.isDisplayable()) {
                it.remove();
                continue;
            }
            String l = e.label();
            if (l == null || l.isBlank()) {
                it.remove();
            }
        }
    }
}
