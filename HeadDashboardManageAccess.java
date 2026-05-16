import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class HeadDashboardManageAccess extends JFrame {

    private static final Color BG = new Color(248, 250, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(15, 23, 42);
    private static final Color MUTED = new Color(71, 85, 105);
    private static final Color BORDER_CLR = new Color(203, 213, 225);
    private static final Color FOCUS = new Color(37, 99, 235);
    private static final Color ERROR = new Color(220, 38, 38);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color BADGE_ENG = new Color(219, 234, 254);
    private static final Color BADGE_OFF = new Color(220, 252, 231);
    private static final Color BADGE_HEAD = new Color(237, 233, 254);

    private static final Font H2 = new Font("SansSerif", Font.BOLD, 16);
    private static final Font SECTION = new Font("SansSerif", Font.BOLD, 12);
    private static final Font BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    private static final Font SMALL = new Font("SansSerif", Font.PLAIN, 11);

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy").withZone(ZoneId.systemDefault());

    private JPanel userListPanel;
    private final JTextField addEmailField = new JTextField();
    private final JPasswordField addPasswordField = new JPasswordField();
    private final JButton addTogglePassword = new JButton("Show");
    private final JComboBox<String> addRoleCombo = new JComboBox<>(
            new String[]{"Structural Engineer", "LGU Officer"});
    private final JButton addButton = new JButton("Add Account");
    private final JLabel addStatus = new JLabel(" ");

    public HeadDashboardManageAccess() {
        setTitle("AOMA-Heritage Monitor — Manage Accounts");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(560, 540));
        setPreferredSize(new Dimension(640, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setVisible(true);
        refreshUserList();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Manage Accounts");
        title.setFont(H2);
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Add Structural Engineer or LGU Officer accounts to the system.");
        sub.setFont(BODY);
        sub.setForeground(MUTED);

        p.add(title, BorderLayout.NORTH);
        p.add(sub, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);

        p.add(buildUserListSection(), BorderLayout.CENTER);
        p.add(buildAddSection(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildUserListSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        JLabel sectionLabel = sectionHeader("REGISTERED ACCOUNTS");
        wrapper.add(sectionLabel, BorderLayout.NORTH);

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setBackground(CARD);

        JScrollPane scroll = new JScrollPane(userListPanel);
        scroll.setBorder(new RoundedBorder(10, BORDER_CLR, 1));
        scroll.setBackground(CARD);
        scroll.getViewport().setBackground(CARD);
        scroll.setPreferredSize(new Dimension(0, 220));

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildAddSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        wrapper.add(sectionHeader("ADD ACCOUNT"), BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new RoundedBorder(10, BORDER_CLR, 1), new EmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(4, 0, 4, 8);

        // Email
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        card.add(fieldLabel("Email"), gc);
        gc.gridx = 1; gc.weightx = 1;
        addEmailField.setFont(BODY);
        card.add(fieldWrap(addEmailField), gc);

        // Password
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        card.add(fieldLabel("Password"), gc);
        gc.gridx = 1; gc.weightx = 1;
        addPasswordField.setFont(BODY);
        addTogglePassword.setFont(BODY_BOLD);
        addTogglePassword.setFocusable(false);
        addTogglePassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addTogglePassword.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        addTogglePassword.setContentAreaFilled(false);
        addTogglePassword.setForeground(FOCUS);
        addTogglePassword.addActionListener(e -> togglePwVisibility());
        card.add(pwWrap(addPasswordField, addTogglePassword), gc);

        // Role
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        card.add(fieldLabel("Role"), gc);
        gc.gridx = 1; gc.weightx = 1;
        addRoleCombo.setFont(BODY);
        card.add(addRoleCombo, gc);

        // Button row
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; gc.weightx = 1;
        gc.insets = new Insets(10, 0, 0, 0);
        JPanel btnRow = new JPanel(new BorderLayout(12, 0));
        btnRow.setOpaque(false);
        addButton.setFont(BODY_BOLD);
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(FOCUS);
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setBorder(new CompoundBorder(new RoundedBorder(10, FOCUS, 1), new EmptyBorder(9, 18, 9, 18)));
        addButton.addActionListener(e -> attemptAdd());
        addStatus.setFont(BODY);
        addStatus.setForeground(MUTED);
        btnRow.add(addButton, BorderLayout.WEST);
        btnRow.add(addStatus, BorderLayout.CENTER);
        card.add(btnRow, gc);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 0, 0, 0));

        JButton done = new JButton("Done");
        done.setFont(BODY_BOLD);
        done.setForeground(TEXT);
        done.setFocusPainted(false);
        done.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        done.setBorder(new CompoundBorder(new RoundedBorder(10, BORDER_CLR, 1), new EmptyBorder(9, 24, 9, 24)));
        done.addActionListener(e -> dispose());
        p.add(done, BorderLayout.EAST);
        return p;
    }

    private void refreshUserList() {
        new SwingWorker<List<UserStore.UserRecord>, Void>() {
            @Override
            protected List<UserStore.UserRecord> doInBackground() {
                return UserStore.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<UserStore.UserRecord> users = get();
                    userListPanel.removeAll();
                    if (users.isEmpty()) {
                        JLabel empty = new JLabel("No accounts registered yet.");
                        empty.setFont(BODY);
                        empty.setForeground(MUTED);
                        empty.setBorder(new EmptyBorder(16, 16, 16, 16));
                        userListPanel.add(empty);
                    } else {
                        for (int i = 0; i < users.size(); i++) {
                            userListPanel.add(buildUserRow(users.get(i)));
                            if (i < users.size() - 1) {
                                JPanel sep = new JPanel();
                                sep.setBackground(BORDER_CLR);
                                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                                userListPanel.add(sep);
                            }
                        }
                    }
                    userListPanel.revalidate();
                    userListPanel.repaint();
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private JPanel buildUserRow(UserStore.UserRecord user) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(CARD);
        row.setBorder(new EmptyBorder(10, 14, 10, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        JLabel emailLabel = new JLabel(user.email());
        emailLabel.setFont(BODY_BOLD);
        emailLabel.setForeground(TEXT);
        gc.gridx = 0; gc.weightx = 1; gc.insets = new Insets(0, 0, 0, 8);
        row.add(emailLabel, gc);

        JLabel badge = roleBadge(user.role());
        gc.gridx = 1; gc.weightx = 0; gc.insets = new Insets(0, 4, 0, 8);
        row.add(badge, gc);

        String dateStr = user.createdAtMs() > 0
                ? DATE_FMT.format(Instant.ofEpochMilli(user.createdAtMs()))
                : "";
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(SMALL);
        dateLabel.setForeground(MUTED);
        gc.gridx = 2; gc.insets = new Insets(0, 4, 0, 0);
        row.add(dateLabel, gc);

        return row;
    }

    private JLabel roleBadge(RoleMenuBar.Role role) {
        String text;
        Color bg;
        Color fg;
        switch (role) {
            case OFFICER -> { text = "Officer";  bg = BADGE_OFF;  fg = new Color(22, 101, 52);  }
            case HEAD    -> { text = "Head";     bg = BADGE_HEAD; fg = new Color(91, 33, 182);  }
            default      -> { text = "Engineer"; bg = BADGE_ENG;  fg = new Color(29, 78, 216);  }
        }
        JLabel l = new JLabel(text);
        l.setFont(SMALL);
        l.setForeground(fg);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setBorder(new EmptyBorder(2, 8, 2, 8));
        return l;
    }

    private void attemptAdd() {
        String email = addEmailField.getText().trim();
        char[] password = addPasswordField.getPassword();
        String roleStr = Objects.toString(addRoleCombo.getSelectedItem(), "");

        if (email.isEmpty()) {
            setStatus("Email is required.", ERROR);
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            setStatus("Enter a valid email address.", ERROR);
            return;
        }
        if (password.length < 6) {
            setStatus("Password must be at least 6 characters.", ERROR);
            return;
        }

        RoleMenuBar.Role role = roleStr.contains("Officer")
                ? RoleMenuBar.Role.OFFICER
                : RoleMenuBar.Role.ENGINEER;

        addButton.setEnabled(false);
        setStatus("Adding…", MUTED);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return UserStore.createUser(email, role, password);
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    addButton.setEnabled(true);
                    if (ok) {
                        addEmailField.setText("");
                        addPasswordField.setText("");
                        addRoleCombo.setSelectedIndex(0);
                        setStatus("Account created for " + email, SUCCESS);
                        refreshUserList();
                    } else {
                        setStatus("An account with this email already exists.", ERROR);
                    }
                } catch (Exception ex) {
                    addButton.setEnabled(true);
                    setStatus("Failed to create account. Try again.", ERROR);
                }
            }
        }.execute();
    }

    private void setStatus(String msg, Color color) {
        addStatus.setText(msg);
        addStatus.setForeground(color);
    }

    private void togglePwVisibility() {
        boolean showing = "Hide".equals(addTogglePassword.getText());
        if (showing) {
            addPasswordField.setEchoChar('•');
            addTogglePassword.setText("Show");
        } else {
            addPasswordField.setEchoChar((char) 0);
            addTogglePassword.setText("Hide");
        }
        addPasswordField.requestFocusInWindow();
    }

    private static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BODY_BOLD);
        l.setForeground(TEXT);
        l.setBorder(new EmptyBorder(0, 0, 0, 8));
        return l;
    }

    private static JPanel fieldWrap(JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new CompoundBorder(new RoundedBorder(10, BORDER_CLR, 1), new EmptyBorder(0, 0, 0, 0)));
        field.setBorder(new EmptyBorder(8, 10, 8, 10));
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private static JPanel pwWrap(JPasswordField field, JButton toggle) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new CompoundBorder(new RoundedBorder(10, BORDER_CLR, 1), new EmptyBorder(0, 0, 0, 0)));
        field.setBorder(new EmptyBorder(8, 10, 8, 10));
        wrap.add(field, BorderLayout.CENTER);
        wrap.add(toggle, BorderLayout.EAST);
        return wrap;
    }

    private static JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SECTION);
        l.setForeground(MUTED);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        return l;
    }

    private static final class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private final int thickness;

        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c, Insets insets) {
            insets.set(thickness, thickness, thickness, thickness);
            return insets;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            int off = thickness / 2;
            g2.drawRoundRect(x + off, y + off, w - thickness, h - thickness, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HeadDashboardManageAccess::new);
    }
}
