import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class UsersLoginOptions extends JFrame {
    private static final Color BG = new Color(248, 250, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(15, 23, 42);
    private static final Color MUTED = new Color(71, 85, 105);
    private static final Color BRAND = new Color(15, 23, 42);
    private static final Color BRAND_ACCENT = new Color(56, 189, 248);
    private static final Color BORDER = new Color(203, 213, 225);
    private static final Color FOCUS = new Color(37, 99, 235);
    private static final Color ERROR = new Color(220, 38, 38);

    private static final Font H1 = new Font("SansSerif", Font.BOLD, 24);
    private static final Font H2 = new Font("SansSerif", Font.BOLD, 16);
    private static final Font BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);

    private static final String PREF_REMEMBER = "login_remember";
    private static final String PREF_EMAIL = "login_email";

    private final Preferences prefs = Preferences.userNodeForPackage(UsersLoginOptions.class);

    private final JPanel layoutRoot = new JPanel(new BorderLayout());
    private final JPanel brandPanel = new JPanel();
    private final JPanel formCard = new JPanel(new BorderLayout());

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton togglePassword = new JButton("Show");
    private final JCheckBox rememberMe = new JCheckBox("Remember me");
    private final JButton loginButton = new JButton("Log in");
    private final JProgressBar loading = new JProgressBar();
    private final FadeLabel emailError = new FadeLabel();
    private final FadeLabel passwordError = new FadeLabel();
    private final FadeLabel formError = new FadeLabel();

    private JPanel emailWrap;
    private JPanel passwordWrap;
    private boolean loadingState;
    private boolean wideLayout;

    public UsersLoginOptions() {
        setTitle("AOMA-Heritage Monitor - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 560));
        setPreferredSize(new Dimension(920, 620));
        setLocationRelativeTo(null);

        layoutRoot.setBackground(BG);
        layoutRoot.setBorder(new EmptyBorder(16, 16, 16, 16));

        buildBrandPanel();
        buildFormPanel();

        setContentPane(layoutRoot);
        applyResponsiveLayout(getPreferredSize().width);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveLayout(getWidth());
            }
        });

        getRootPane().setDefaultButton(loginButton);
        loadRememberedState();
        pack();
        setVisible(true);
    }

    private void buildBrandPanel() {
        brandPanel.setBackground(BRAND);
        brandPanel.setBorder(new EmptyBorder(28, 28, 28, 28));
        brandPanel.setLayout(new GridBagLayout());

        JLabel title = new JLabel("AOMA-Heritage Monitor");
        title.setFont(H1);
        title.setForeground(Color.WHITE);

        JLabel by = new JLabel("By RJ13 Connectors");
        by.setFont(BODY);
        by.setForeground(new Color(203, 213, 225));

        JLabel tagline = new JLabel("<html><div style='width:260px'>Operational Modal Analysis results and heritage structural health insights in one workspace.</div></html>");
        tagline.setFont(BODY);
        tagline.setForeground(new Color(226, 232, 240));

        JPanel accent = new JPanel();
        accent.setBackground(BRAND_ACCENT);
        accent.setPreferredSize(new Dimension(56, 6));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 0, 0);

        gc.gridy = 0;
        brandPanel.add(title, gc);
        gc.gridy = 1;
        gc.insets = new Insets(6, 0, 0, 0);
        brandPanel.add(by, gc);
        gc.gridy = 2;
        gc.insets = new Insets(14, 0, 0, 0);
        brandPanel.add(accent, gc);
        gc.gridy = 3;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(18, 0, 0, 0);
        brandPanel.add(tagline, gc);
    }

    private void buildFormPanel() {
        formCard.setBackground(CARD);
        formCard.setBorder(new CompoundBorder(new RoundedBorder(14, BORDER, 1), new EmptyBorder(22, 22, 22, 22)));

        JLabel header = new JLabel("Account Login");
        header.setFont(H2);
        header.setForeground(TEXT);

        JLabel hint = new JLabel("Use your email address and password to continue.");
        hint.setFont(BODY);
        hint.setForeground(MUTED);

        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.gridy = 0;
        top.add(header, gc);
        gc.gridy = 1;
        gc.insets = new Insets(6, 0, 0, 0);
        top.add(hint, gc);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(BODY_BOLD);
        emailLabel.setForeground(TEXT);
        emailLabel.setDisplayedMnemonic(KeyEvent.VK_E);
        emailLabel.setLabelFor(emailField);

        emailField.setFont(BODY);
        emailField.getAccessibleContext().setAccessibleName("Email");
        emailField.setToolTipText("Enter your email address");
        emailWrap = fieldWrap(emailField);

        emailError.setFont(BODY);
        emailError.setForeground(ERROR);
        emailError.setVisible(false);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(BODY_BOLD);
        passLabel.setForeground(TEXT);
        passLabel.setDisplayedMnemonic(KeyEvent.VK_P);
        passLabel.setLabelFor(passwordField);

        passwordField.setFont(BODY);
        passwordField.getAccessibleContext().setAccessibleName("Password");
        passwordField.setToolTipText("Enter your password");

        togglePassword.setFont(BODY_BOLD);
        togglePassword.setFocusable(false);
        togglePassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        togglePassword.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        togglePassword.setContentAreaFilled(false);
        togglePassword.setForeground(FOCUS);
        togglePassword.addActionListener(e -> togglePasswordVisibility());

        passwordWrap = fieldWrapWithTrailing(passwordField, togglePassword);
        passwordError.setFont(BODY);
        passwordError.setForeground(ERROR);
        passwordError.setVisible(false);

        rememberMe.setFont(BODY);
        rememberMe.setForeground(TEXT);
        rememberMe.setMnemonic(KeyEvent.VK_R);
        rememberMe.setOpaque(false);

        JLabel forgot = linkLabel("Forgot password?");
        forgot.getAccessibleContext().setAccessibleName("Forgot password");
        forgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openForgotPasswordDialog();
            }
        });

        JPanel rowRemember = new JPanel(new BorderLayout());
        rowRemember.setOpaque(false);
        rowRemember.add(rememberMe, BorderLayout.WEST);
        rowRemember.add(forgot, BorderLayout.EAST);

        loginButton.setFont(BODY_BOLD);
        loginButton.setMnemonic(KeyEvent.VK_L);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(37, 99, 235));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new CompoundBorder(new RoundedBorder(12, new Color(37, 99, 235), 1), new EmptyBorder(12, 14, 12, 14)));
        loginButton.addActionListener(e -> attemptLogin());

        loading.setIndeterminate(true);
        loading.setVisible(false);
        loading.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        formError.setFont(BODY);
        formError.setForeground(ERROR);
        formError.setVisible(false);

        JLabel create = linkLabel("Create an account");
        create.getAccessibleContext().setAccessibleName("Create an account");
        create.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new CreateAccount().setVisible(true);
                dispose();
            }
        });

        JLabel createHint = new JLabel("Don’t have an account?");
        createHint.setFont(BODY);
        createHint.setForeground(MUTED);

        JPanel bottomLinks = new JPanel(new BorderLayout(8, 0));
        bottomLinks.setOpaque(false);
        bottomLinks.add(createHint, BorderLayout.WEST);
        bottomLinks.add(create, BorderLayout.EAST);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.weightx = 1;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.anchor = GridBagConstraints.WEST;

        bc.gridy = 0;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(emailLabel, bc);
        bc.gridy = 1;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(emailWrap, bc);
        bc.gridy = 2;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(emailError, bc);

        bc.gridy = 3;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(passLabel, bc);
        bc.gridy = 4;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(passwordWrap, bc);
        bc.gridy = 5;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(passwordError, bc);

        bc.gridy = 6;
        bc.insets = new Insets(4, 0, 14, 0);
        body.add(rowRemember, bc);

        bc.gridy = 7;
        bc.insets = new Insets(0, 0, 8, 0);
        body.add(loginButton, bc);
        bc.gridy = 8;
        bc.insets = new Insets(0, 0, 10, 0);
        body.add(loading, bc);
        bc.gridy = 9;
        bc.insets = new Insets(0, 2, 14, 0);
        body.add(formError, bc);

        bc.gridy = 10;
        bc.insets = new Insets(8, 0, 0, 0);
        body.add(bottomLinks, bc);

        formCard.add(top, BorderLayout.NORTH);
        formCard.add(body, BorderLayout.CENTER);

        installFocusBehavior(emailField, emailWrap);
        installFocusBehavior(passwordField, passwordWrap);
        emailField.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void applyResponsiveLayout(int width) {
        boolean shouldWide = width >= 820;
        if (shouldWide == wideLayout && layoutRoot.getComponentCount() > 0) {
            return;
        }
        wideLayout = shouldWide;
        layoutRoot.removeAll();
        if (wideLayout) {
            JPanel center = new JPanel(new BorderLayout(16, 0));
            center.setOpaque(false);
            center.add(brandPanel, BorderLayout.WEST);
            center.add(formCard, BorderLayout.CENTER);
            layoutRoot.add(center, BorderLayout.CENTER);
            brandPanel.setPreferredSize(new Dimension(360, 0));
        } else {
            JPanel center = new JPanel(new BorderLayout(0, 16));
            center.setOpaque(false);
            center.add(brandPanel, BorderLayout.NORTH);
            center.add(formCard, BorderLayout.CENTER);
            layoutRoot.add(center, BorderLayout.CENTER);
            brandPanel.setPreferredSize(null);
        }
        layoutRoot.revalidate();
        layoutRoot.repaint();
    }

    private JPanel fieldWrap(JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(fieldBorder(BORDER, 1));
        wrap.add(field, BorderLayout.CENTER);
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
        return wrap;
    }

    private JPanel fieldWrapWithTrailing(JComponent field, JButton trailing) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(fieldBorder(BORDER, 1));
        wrap.add(field, BorderLayout.CENTER);
        wrap.add(trailing, BorderLayout.EAST);
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
        return wrap;
    }

    private static CompoundBorder fieldBorder(Color c, int thickness) {
        return new CompoundBorder(new RoundedBorder(12, c, thickness), new EmptyBorder(0, 0, 0, 0));
    }

    private static JLabel linkLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BODY_BOLD);
        l.setForeground(FOCUS);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                l.setForeground(new Color(29, 78, 216));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                l.setForeground(FOCUS);
            }
        });
        return l;
    }

    private void installFocusBehavior(JComponent field, JPanel wrap) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                animateWrapBorder(wrap, currentWrapColor(wrap), FOCUS, 140);
            }

            @Override
            public void focusLost(FocusEvent e) {
                boolean err = (wrap == emailWrap && !emailError.isBlank()) || (wrap == passwordWrap && !passwordError.isBlank());
                Color target = err ? ERROR : BORDER;
                animateWrapBorder(wrap, currentWrapColor(wrap), target, 140);
                if (wrap == emailWrap) {
                    validateEmail(true);
                } else if (wrap == passwordWrap) {
                    validatePassword(true);
                }
            }
        });
    }

    private static Color currentWrapColor(JPanel wrap) {
        if (wrap.getBorder() instanceof CompoundBorder cb && cb.getOutsideBorder() instanceof RoundedBorder rb) {
            return rb.color;
        }
        return BORDER;
    }

    private static void animateWrapBorder(JPanel wrap, Color from, Color to, int durationMs) {
        if (Objects.equals(from, to)) {
            wrap.setBorder(fieldBorder(to, Objects.equals(to, FOCUS) ? 2 : 1));
            wrap.revalidate();
            wrap.repaint();
            return;
        }
        Timer existing = (Timer) wrap.getClientProperty("border_anim");
        if (existing != null) {
            existing.stop();
        }
        long start = System.currentTimeMillis();
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            float p = (System.currentTimeMillis() - start) / (float) durationMs;
            if (p >= 1f) {
                t.stop();
                wrap.putClientProperty("border_anim", null);
                wrap.setBorder(fieldBorder(to, Objects.equals(to, FOCUS) ? 2 : 1));
                wrap.revalidate();
                wrap.repaint();
                return;
            }
            Color c = lerp(from, to, p);
            int thick = Objects.equals(to, FOCUS) ? 2 : 1;
            wrap.setBorder(fieldBorder(c, thick));
            wrap.revalidate();
            wrap.repaint();
        });
        wrap.putClientProperty("border_anim", t);
        t.start();
    }

    private static Color lerp(Color a, Color b, float t) {
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        int al = Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(r, g, bl, al);
    }

    private void togglePasswordVisibility() {
        boolean showing = Objects.equals(togglePassword.getText(), "Hide");
        if (showing) {
            Object v = UIManager.get("PasswordField.echoChar");
            char ch = v instanceof Character ? (Character) v : '\u2022';
            passwordField.setEchoChar(ch);
            togglePassword.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            togglePassword.setText("Hide");
        }
        passwordField.requestFocusInWindow();
    }

    private void loadRememberedState() {
        boolean remember = prefs.getBoolean(PREF_REMEMBER, false);
        String email = prefs.get(PREF_EMAIL, "");
        rememberMe.setSelected(remember);
        if (remember && email != null && !email.isBlank()) {
            emailField.setText(email);
        } else if (emailField.getText() == null || emailField.getText().isBlank()) {
            emailField.setText(EngineerCredentialStore.getEmail());
        }
    }

    private void persistRememberedStateOnSuccess() {
        if (rememberMe.isSelected()) {
            prefs.putBoolean(PREF_REMEMBER, true);
            prefs.put(PREF_EMAIL, emailField.getText().trim());
        } else {
            prefs.putBoolean(PREF_REMEMBER, false);
            prefs.remove(PREF_EMAIL);
        }
    }

    private void attemptLogin() {
        if (loadingState) {
            return;
        }
        clearFormError();
        boolean ok = validateEmail(true) & validatePassword(true);
        if (!ok) {
            return;
        }

        setLoading(true);
        Timer t = new Timer(350, null);
        t.setRepeats(false);
        t.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (EngineerCredentialStore.verifyCredentials(email, passwordField.getPassword())) {
                persistRememberedStateOnSuccess();
                setLoading(false);
                JOptionPane.showMessageDialog(this, "Engineer Login Successful!");
                new EngineerBldgStatusOverview();
                dispose();
            } else if (email.equals("juandelacruz2@officer.com") && password.equals("dummy123")) {
                persistRememberedStateOnSuccess();
                setLoading(false);
                JOptionPane.showMessageDialog(this, "Officer Login Successful!");
                new OfficerBldgStatusOverview();
                dispose();
            } else if (email.equals("juandelacruz3@head.com") && password.equals("dummy123")) {
                persistRememberedStateOnSuccess();
                setLoading(false);
                JOptionPane.showMessageDialog(this, "Head Login Successful!");
                new HeadBldgStatusOverview();
                dispose();
            } else {
                setLoading(false);
                showFormError("Invalid email or password.");
                animateWrapBorder(emailWrap, currentWrapColor(emailWrap), ERROR, 120);
                animateWrapBorder(passwordWrap, currentWrapColor(passwordWrap), ERROR, 120);
            }
        });
        t.start();
    }

    private boolean validateEmail(boolean show) {
        String v = emailField.getText() == null ? "" : emailField.getText().trim();
        String msg = null;
        if (v.isEmpty()) {
            msg = "Email is required.";
        } else if (!v.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            msg = "Enter a valid email address.";
        }
        if (msg == null) {
            clearEmailError();
            return true;
        }
        if (show) {
            showEmailError(msg);
        }
        return false;
    }

    private boolean validatePassword(boolean show) {
        String v = new String(passwordField.getPassword()).trim();
        String msg = null;
        if (v.isEmpty()) {
            msg = "Password is required.";
        }
        if (msg == null) {
            clearPasswordError();
            return true;
        }
        if (show) {
            showPasswordError(msg);
        }
        return false;
    }

    private void showEmailError(String msg) {
        emailError.fadeIn(msg);
        animateWrapBorder(emailWrap, currentWrapColor(emailWrap), ERROR, 120);
    }

    private void clearEmailError() {
        emailError.fadeOut();
        boolean focused = emailField.isFocusOwner();
        animateWrapBorder(emailWrap, currentWrapColor(emailWrap), focused ? FOCUS : BORDER, 120);
    }

    private void showPasswordError(String msg) {
        passwordError.fadeIn(msg);
        animateWrapBorder(passwordWrap, currentWrapColor(passwordWrap), ERROR, 120);
    }

    private void clearPasswordError() {
        passwordError.fadeOut();
        boolean focused = passwordField.isFocusOwner();
        animateWrapBorder(passwordWrap, currentWrapColor(passwordWrap), focused ? FOCUS : BORDER, 120);
    }

    private void showFormError(String msg) {
        formError.fadeIn(msg);
    }

    private void clearFormError() {
        formError.fadeOut();
    }

    private void setLoading(boolean loadingOn) {
        loadingState = loadingOn;
        emailField.setEnabled(!loadingOn);
        passwordField.setEnabled(!loadingOn);
        togglePassword.setEnabled(!loadingOn);
        rememberMe.setEnabled(!loadingOn);
        loginButton.setEnabled(!loadingOn);
        loading.setVisible(loadingOn);
        loginButton.setText(loadingOn ? "Logging in…" : "Log in");
        JRootPane rp = getRootPane();
        if (rp != null) {
            rp.setDefaultButton(loadingOn ? null : loginButton);
        }
        SwingUtilities.invokeLater(() -> {
            layoutRoot.revalidate();
            layoutRoot.repaint();
        });
    }

    private void openForgotPasswordDialog() {
        JDialog d = new JDialog(this, "Forgot password", true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel t = new JLabel("Password reset");
        t.setFont(H2);
        t.setForeground(TEXT);

        JTextField email = new JTextField(EngineerCredentialStore.getEmail());
        email.setFont(BODY);
        email.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPasswordField newPass = new JPasswordField();
        newPass.setFont(BODY);
        newPass.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPasswordField confirm = new JPasswordField();
        confirm.setFont(BODY);
        confirm.setBorder(new EmptyBorder(8, 10, 8, 10));

        FadeLabel err = new FadeLabel();
        err.setFont(BODY);
        err.setForeground(ERROR);
        err.setVisible(false);

        long expectedVersion = EngineerCredentialStore.getVersion();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 6, 0);
        form.add(new JLabel("Email"), gc);
        gc.gridy++;
        form.add(fieldWrap(email), gc);
        gc.gridy++;
        gc.insets = new Insets(12, 0, 6, 0);
        form.add(new JLabel("New password"), gc);
        gc.gridy++;
        gc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldWrap(newPass), gc);
        gc.gridy++;
        gc.insets = new Insets(12, 0, 6, 0);
        form.add(new JLabel("Confirm new password"), gc);
        gc.gridy++;
        gc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldWrap(confirm), gc);
        gc.gridy++;
        gc.insets = new Insets(8, 0, 0, 0);
        form.add(err, gc);

        JButton reset = new JButton("Reset password");
        reset.setFont(BODY_BOLD);
        reset.addActionListener(ev -> {
            String em = email.getText() == null ? "" : email.getText().trim();
            if (!EngineerCredentialStore.getEmail().equalsIgnoreCase(em)) {
                err.fadeIn("Email does not match the current engineer account.");
                return;
            }
            char[] p1 = newPass.getPassword();
            char[] p2 = confirm.getPassword();
            if (!java.util.Arrays.equals(p1, p2)) {
                err.fadeIn("Passwords do not match.");
                return;
            }
            if (!EngineerCredentialStore.isValidPassword(p1)) {
                err.fadeIn("Password must be at least 6 characters and include letters and digits.");
                return;
            }
            boolean updated = EngineerCredentialStore.updatePassword(p1, expectedVersion);
            if (!updated) {
                err.fadeIn("Password reset failed. Please retry.");
                return;
            }
            JOptionPane.showMessageDialog(d, "Password updated.", "Password reset", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });

        JButton cancel = new JButton("Cancel");
        cancel.setFont(BODY_BOLD);
        cancel.addActionListener(ev -> d.dispose());

        JPanel buttons = new JPanel(new BorderLayout(8, 0));
        buttons.setOpaque(false);
        buttons.add(cancel, BorderLayout.WEST);
        buttons.add(reset, BorderLayout.EAST);

        p.add(t, BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);

        d.setContentPane(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
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

    private static final class FadeLabel extends JLabel {
        private float alpha = 1f;
        private Timer anim;

        private FadeLabel() {
            setOpaque(false);
        }

        private boolean isBlank() {
            String t = getText();
            return t == null || t.isBlank();
        }

        private void fadeIn(String text) {
            setText(text);
            setVisible(true);
            animateTo(1f, 160, true);
        }

        private void fadeOut() {
            if (!isVisible() || isBlank()) {
                setText("");
                setVisible(false);
                return;
            }
            animateTo(0f, 160, false);
        }

        private void animateTo(float target, int durationMs, boolean keepVisible) {
            if (anim != null) {
                anim.stop();
            }
            float startAlpha = alpha;
            long start = System.currentTimeMillis();
            anim = new Timer(15, null);
            anim.addActionListener(e -> {
                float p = (System.currentTimeMillis() - start) / (float) durationMs;
                if (p >= 1f) {
                    anim.stop();
                    alpha = target;
                    if (!keepVisible && target <= 0f) {
                        setText("");
                        setVisible(false);
                    }
                    repaint();
                    return;
                }
                alpha = startAlpha + (target - startAlpha) * p;
                repaint();
            });
            anim.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new UsersLoginOptions();
    }
}
