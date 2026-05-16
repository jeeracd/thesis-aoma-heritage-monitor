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
import java.awt.GridLayout;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class CreateAccount extends JFrame {
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

    private static final String ACCOUNT_TYPE_ENGINEER = "Structural Engineer";

    private final JPanel layoutRoot = new JPanel(new BorderLayout());
    private final JPanel brandPanel = new JPanel();
    private final JPanel formCard = new JPanel(new BorderLayout());

    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JComboBox<String> accountType = new JComboBox<>(new String[] { ACCOUNT_TYPE_ENGINEER });
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField locationField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton togglePassword = new JButton("Show");
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private final JButton toggleConfirmPassword = new JButton("Show");
    private final JProgressBar strengthBar = new JProgressBar(0, 100);
    private final JLabel strengthLabel = new JLabel("Password strength");
    private final JCheckBox consent = new JCheckBox("I agree to the Terms of Service and Privacy Policy");
    private final JButton createButton = new JButton("Create account");
    private final JProgressBar loading = new JProgressBar();

    private final FadeLabel firstNameError = new FadeLabel();
    private final FadeLabel lastNameError = new FadeLabel();
    private final FadeLabel accountTypeError = new FadeLabel();
    private final FadeLabel emailError = new FadeLabel();
    private final FadeLabel phoneError = new FadeLabel();
    private final FadeLabel locationError = new FadeLabel();
    private final FadeLabel passwordError = new FadeLabel();
    private final FadeLabel confirmError = new FadeLabel();
    private final FadeLabel consentError = new FadeLabel();
    private final FadeLabel formError = new FadeLabel();

    private JPanel firstNameWrap;
    private JPanel lastNameWrap;
    private JPanel accountTypeWrap;
    private JPanel emailWrap;
    private JPanel phoneWrap;
    private JPanel locationWrap;
    private JPanel passwordWrap;
    private JPanel confirmWrap;

    private boolean wideLayout;
    private boolean loadingState;

    public CreateAccount() {
        setTitle("AOMA-Heritage Monitor - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 700));
        setPreferredSize(new Dimension(980, 720));
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

        getRootPane().setDefaultButton(createButton);
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

        JLabel page = new JLabel("Create your account");
        page.setFont(H2);
        page.setForeground(new Color(226, 232, 240));

        JLabel help = new JLabel("<html><div style='width:260px'>Create an account to access dashboards, monitoring sessions, and OMA analysis results. Fields marked required must be completed to proceed.</div></html>");
        help.setFont(BODY);
        help.setForeground(new Color(226, 232, 240));

        JPanel accent = new JPanel();
        accent.setBackground(BRAND_ACCENT);
        accent.setPreferredSize(new Dimension(56, 6));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTHWEST;

        gc.gridy = 0;
        brandPanel.add(title, gc);
        gc.gridy = 1;
        gc.insets = new Insets(6, 0, 0, 0);
        brandPanel.add(by, gc);
        gc.gridy = 2;
        gc.insets = new Insets(14, 0, 0, 0);
        brandPanel.add(accent, gc);
        gc.gridy = 3;
        gc.insets = new Insets(18, 0, 0, 0);
        brandPanel.add(page, gc);
        gc.gridy = 4;
        gc.weighty = 1;
        gc.insets = new Insets(12, 0, 0, 0);
        brandPanel.add(help, gc);
    }

    private void buildFormPanel() {
        formCard.setBackground(CARD);
        formCard.setBorder(new CompoundBorder(new RoundedBorder(14, BORDER, 1), new EmptyBorder(22, 22, 22, 22)));

        JLabel header = new JLabel("Create account");
        header.setFont(H2);
        header.setForeground(TEXT);

        JLabel hint = new JLabel("All fields are required unless marked optional.");
        hint.setFont(BODY);
        hint.setForeground(MUTED);

        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);
        GridBagConstraints tc = new GridBagConstraints();
        tc.gridx = 0;
        tc.weightx = 1;
        tc.fill = GridBagConstraints.HORIZONTAL;
        tc.anchor = GridBagConstraints.WEST;
        tc.gridy = 0;
        top.add(header, tc);
        tc.gridy = 1;
        tc.insets = new Insets(6, 0, 0, 0);
        top.add(hint, tc);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        firstNameField.setFont(BODY);
        firstNameField.getAccessibleContext().setAccessibleName("First name");
        firstNameField.setToolTipText("Enter your first name");
        lastNameField.setFont(BODY);
        lastNameField.getAccessibleContext().setAccessibleName("Last name");
        lastNameField.setToolTipText("Enter your last name");

        firstNameWrap = fieldWrap(firstNameField);
        lastNameWrap = fieldWrap(lastNameField);

        JLabel firstNameLabel = fieldLabel("First name", KeyEvent.VK_F, firstNameField);
        JLabel lastNameLabel = fieldLabel("Last name", KeyEvent.VK_A, lastNameField);

        configureError(firstNameError);
        configureError(lastNameError);

        accountType.setFont(BODY);
        accountType.getAccessibleContext().setAccessibleName("Account type");
        accountType.setToolTipText("Public registration creates Structural Engineer accounts. Contact your administrator for Officer or Head accounts.");
        accountType.setEnabled(false);
        accountType.setPrototypeDisplayValue(ACCOUNT_TYPE_ENGINEER);
        accountTypeWrap = fieldWrap(accountType);
        JLabel accountTypeLabel = fieldLabel("Account type", KeyEvent.VK_T, accountType);
        configureError(accountTypeError);

        emailField.setFont(BODY);
        emailField.getAccessibleContext().setAccessibleName("Email");
        emailField.setToolTipText("Enter a valid email address");
        emailWrap = fieldWrap(emailField);
        JLabel emailLabel = fieldLabel("Email", KeyEvent.VK_E, emailField);
        configureError(emailError);

        phoneField.setFont(BODY);
        phoneField.getAccessibleContext().setAccessibleName("Phone number");
        phoneField.setToolTipText("Enter your phone number (e.g., +63...)");
        phoneWrap = fieldWrap(phoneField);
        JLabel phoneLabel = fieldLabel("Phone number", KeyEvent.VK_P, phoneField);
        configureError(phoneError);

        locationField.setFont(BODY);
        locationField.getAccessibleContext().setAccessibleName("City, Province");
        locationField.setToolTipText("Enter your city and province");
        locationWrap = fieldWrap(locationField);
        JLabel locationLabel = fieldLabel("City, Province", KeyEvent.VK_C, locationField);
        configureError(locationError);

        passwordField.setFont(BODY);
        passwordField.getAccessibleContext().setAccessibleName("Password");
        passwordField.setToolTipText("Create a password");
        togglePassword.setFont(BODY_BOLD);
        togglePassword.setFocusable(false);
        togglePassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        togglePassword.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        togglePassword.setContentAreaFilled(false);
        togglePassword.setForeground(FOCUS);
        togglePassword.addActionListener(e -> togglePasswordVisibility(passwordField, togglePassword));
        passwordWrap = fieldWrapWithTrailing(passwordField, togglePassword);

        JLabel passwordLabel = fieldLabel("Password", KeyEvent.VK_S, passwordField);
        configureError(passwordError);

        confirmPasswordField.setFont(BODY);
        confirmPasswordField.getAccessibleContext().setAccessibleName("Confirm password");
        confirmPasswordField.setToolTipText("Re-enter your password");
        toggleConfirmPassword.setFont(BODY_BOLD);
        toggleConfirmPassword.setFocusable(false);
        toggleConfirmPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleConfirmPassword.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        toggleConfirmPassword.setContentAreaFilled(false);
        toggleConfirmPassword.setForeground(FOCUS);
        toggleConfirmPassword.addActionListener(e -> togglePasswordVisibility(confirmPasswordField, toggleConfirmPassword));
        confirmWrap = fieldWrapWithTrailing(confirmPasswordField, toggleConfirmPassword);

        JLabel confirmLabel = fieldLabel("Confirm password", KeyEvent.VK_N, confirmPasswordField);
        configureError(confirmError);

        strengthBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        strengthBar.setStringPainted(false);
        strengthBar.setValue(0);
        strengthBar.setPreferredSize(new Dimension(0, 10));
        strengthBar.setForeground(new Color(148, 163, 184));
        strengthLabel.setFont(BODY);
        strengthLabel.setForeground(MUTED);

        consent.setFont(BODY);
        consent.setForeground(TEXT);
        consent.setOpaque(false);
        consent.setMnemonic(KeyEvent.VK_I);
        consent.getAccessibleContext().setAccessibleName("Consent");
        consent.addActionListener(e -> {
            clearConsentError();
            updateCreateEnabled();
        });
        configureError(consentError);

        JLabel termsLink = linkLabel("Terms of Service");
        termsLink.setHorizontalAlignment(SwingConstants.LEFT);
        termsLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openPolicyDialog("Terms of Service", "These Terms of Service are provided for prototype demonstration. In production, this dialog should be replaced with the full terms and a persistent link.");
            }
        });

        JLabel privacyLink = linkLabel("Privacy Policy");
        privacyLink.setHorizontalAlignment(SwingConstants.LEFT);
        privacyLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openPolicyDialog("Privacy Policy", "This Privacy Policy is provided for prototype demonstration. In production, this dialog should be replaced with the full policy and a persistent link.");
            }
        });

        JPanel policyLinks = new JPanel(new GridLayout(1, 2, 12, 0));
        policyLinks.setOpaque(false);
        policyLinks.add(termsLink);
        policyLinks.add(privacyLink);

        createButton.setFont(BODY_BOLD);
        createButton.setMnemonic(KeyEvent.VK_R);
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(37, 99, 235));
        createButton.setFocusPainted(false);
        createButton.setBorder(new CompoundBorder(new RoundedBorder(12, new Color(37, 99, 235), 1), new EmptyBorder(12, 14, 12, 14)));
        createButton.addActionListener(e -> attemptCreate());

        loading.setIndeterminate(true);
        loading.setVisible(false);
        loading.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        formError.setFont(BODY);
        formError.setForeground(ERROR);
        formError.setVisible(false);

        JLabel backHint = new JLabel("Already have an account?");
        backHint.setFont(BODY);
        backHint.setForeground(MUTED);

        JLabel back = linkLabel("Back to login");
        back.setHorizontalAlignment(SwingConstants.RIGHT);
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new UsersLoginOptions().setVisible(true);
                dispose();
            }
        });

        JPanel bottomLinks = new JPanel(new BorderLayout(8, 0));
        bottomLinks.setOpaque(false);
        bottomLinks.add(backHint, BorderLayout.WEST);
        bottomLinks.add(back, BorderLayout.EAST);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.weightx = 1;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.anchor = GridBagConstraints.WEST;
        int y = 0;

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(firstNameLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(firstNameWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(firstNameError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(lastNameLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(lastNameWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(lastNameError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(accountTypeLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(accountTypeWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(accountTypeError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(emailLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(emailWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(emailError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(phoneLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(phoneWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(phoneError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(locationLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(locationWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(locationError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(passwordLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(passwordWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 8, 0);
        body.add(passwordError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(strengthLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 12, 0);
        body.add(strengthBar, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 6, 0);
        body.add(confirmLabel, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 4, 0);
        body.add(confirmWrap, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 12, 0);
        body.add(confirmError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(6, 0, 0, 0);
        body.add(consent, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 10, 0);
        body.add(consentError, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 14, 0);
        body.add(policyLinks, bc);

        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 8, 0);
        body.add(createButton, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 0, 10, 0);
        body.add(loading, bc);
        bc.gridy = y++;
        bc.insets = new Insets(0, 2, 14, 0);
        body.add(formError, bc);

        bc.gridy = y++;
        bc.insets = new Insets(8, 0, 0, 0);
        body.add(bottomLinks, bc);

        formCard.add(top, BorderLayout.NORTH);
        formCard.add(body, BorderLayout.CENTER);

        installFocusBehavior(firstNameField, firstNameWrap, firstNameError);
        installFocusBehavior(lastNameField, lastNameWrap, lastNameError);
        installFocusBehavior(emailField, emailWrap, emailError);
        installFocusBehavior(phoneField, phoneWrap, phoneError);
        installFocusBehavior(locationField, locationWrap, locationError);
        installFocusBehavior(passwordField, passwordWrap, passwordError);
        installFocusBehavior(confirmPasswordField, confirmWrap, confirmError);

        passwordField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updatePasswordStrength));
        confirmPasswordField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
            validateConfirmPassword(true);
            updateCreateEnabled();
        }));
        firstNameField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
            validateFirstName(true);
            updateCreateEnabled();
        }));
        lastNameField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
            validateLastName(true);
            updateCreateEnabled();
        }));
        emailField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
            detectAccountTypeFromEmail();
            validateEmail(true);
            updateCreateEnabled();
        }));
        phoneField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
            validatePhone(true);
            updateCreateEnabled();
        }));
        locationField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
            validateLocation(true);
            updateCreateEnabled();
        }));

        updatePasswordStrength();
        detectAccountTypeFromEmail();
        updateCreateEnabled();
    }

    private void applyResponsiveLayout(int width) {
        boolean shouldWide = width >= 880;
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

    private static void configureError(FadeLabel l) {
        l.setFont(BODY);
        l.setForeground(ERROR);
        l.setVisible(false);
    }

    private static JLabel fieldLabel(String text, int mnemonic, JComponent forComp) {
        JLabel l = new JLabel(text);
        l.setFont(BODY_BOLD);
        l.setForeground(TEXT);
        l.setDisplayedMnemonic(mnemonic);
        l.setLabelFor(forComp);
        return l;
    }

    private JPanel fieldWrap(JComponent field) {
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

    private void installFocusBehavior(JComponent field, JPanel wrap, FadeLabel err) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                animateWrapBorder(wrap, currentWrapColor(wrap), FOCUS, 140);
            }

            @Override
            public void focusLost(FocusEvent e) {
                boolean hasErr = err != null && !err.isBlank();
                Color target = hasErr ? ERROR : BORDER;
                animateWrapBorder(wrap, currentWrapColor(wrap), target, 140);
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

    private void togglePasswordVisibility(JPasswordField field, JButton toggle) {
        boolean showing = Objects.equals(toggle.getText(), "Hide");
        if (showing) {
            Object v = UIManager.get("PasswordField.echoChar");
            char ch = v instanceof Character ? (Character) v : '\u2022';
            field.setEchoChar(ch);
            toggle.setText("Show");
        } else {
            field.setEchoChar((char) 0);
            toggle.setText("Hide");
        }
        field.requestFocusInWindow();
    }

    private void updatePasswordStrength() {
        String pw = new String(passwordField.getPassword());
        int score = passwordScore(pw);
        int value = Math.min(100, score * 25);
        strengthBar.setValue(value);
        if (score <= 1) {
            strengthLabel.setText("Password strength: Weak");
            strengthLabel.setForeground(ERROR);
            strengthBar.setForeground(ERROR);
        } else if (score == 2) {
            strengthLabel.setText("Password strength: Fair");
            strengthLabel.setForeground(new Color(180, 120, 10));
            strengthBar.setForeground(new Color(180, 120, 10));
        } else if (score == 3) {
            strengthLabel.setText("Password strength: Good");
            strengthLabel.setForeground(new Color(40, 120, 60));
            strengthBar.setForeground(new Color(40, 120, 60));
        } else {
            strengthLabel.setText("Password strength: Strong");
            strengthLabel.setForeground(new Color(16, 185, 129));
            strengthBar.setForeground(new Color(16, 185, 129));
        }
        validatePassword(true);
        validateConfirmPassword(true);
        updateCreateEnabled();
    }

    private static int passwordScore(String pw) {
        String v = pw == null ? "" : pw;
        int s = 0;
        if (v.length() >= 8) {
            s++;
        }
        if (v.matches(".*[a-z].*") && v.matches(".*[A-Z].*")) {
            s++;
        }
        if (v.matches(".*\\d.*")) {
            s++;
        }
        if (v.matches(".*[^a-zA-Z0-9].*")) {
            s++;
        }
        return s;
    }

    private boolean validateFirstName(boolean show) {
        String v = firstNameField.getText() == null ? "" : firstNameField.getText().trim();
        String msg = v.isEmpty() ? "First name is required." : null;
        if (msg == null) {
            firstNameError.fadeOut();
            animateWrapBorder(firstNameWrap, currentWrapColor(firstNameWrap), firstNameField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            firstNameError.fadeIn(msg);
            animateWrapBorder(firstNameWrap, currentWrapColor(firstNameWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validateLastName(boolean show) {
        String v = lastNameField.getText() == null ? "" : lastNameField.getText().trim();
        String msg = v.isEmpty() ? "Last name is required." : null;
        if (msg == null) {
            lastNameError.fadeOut();
            animateWrapBorder(lastNameWrap, currentWrapColor(lastNameWrap), lastNameField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            lastNameError.fadeIn(msg);
            animateWrapBorder(lastNameWrap, currentWrapColor(lastNameWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validateAccountType(boolean show) {
        accountTypeError.fadeOut();
        animateWrapBorder(accountTypeWrap, currentWrapColor(accountTypeWrap), BORDER, 120);
        return true;
    }

    private void detectAccountTypeFromEmail() {
        // Account type is always Structural Engineer for public registration
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
            emailError.fadeOut();
            animateWrapBorder(emailWrap, currentWrapColor(emailWrap), emailField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            emailError.fadeIn(msg);
            animateWrapBorder(emailWrap, currentWrapColor(emailWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validatePhone(boolean show) {
        String v = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String msg = null;
        if (v.isEmpty()) {
            msg = "Phone number is required.";
        } else if (!v.matches("^\\+?[0-9][0-9\\s\\-]{6,}$")) {
            msg = "Enter a valid phone number.";
        }
        if (msg == null) {
            phoneError.fadeOut();
            animateWrapBorder(phoneWrap, currentWrapColor(phoneWrap), phoneField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            phoneError.fadeIn(msg);
            animateWrapBorder(phoneWrap, currentWrapColor(phoneWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validateLocation(boolean show) {
        String v = locationField.getText() == null ? "" : locationField.getText().trim();
        String msg = v.isEmpty() ? "City, Province is required." : null;
        if (msg == null) {
            locationError.fadeOut();
            animateWrapBorder(locationWrap, currentWrapColor(locationWrap), locationField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            locationError.fadeIn(msg);
            animateWrapBorder(locationWrap, currentWrapColor(locationWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validatePassword(boolean show) {
        String v = new String(passwordField.getPassword());
        int score = passwordScore(v);
        String msg = null;
        if (v.trim().isEmpty()) {
            msg = "Password is required.";
        } else if (score < 2) {
            msg = "Password is too weak. Use at least 8 characters and mix letters and numbers.";
        }
        if (msg == null) {
            passwordError.fadeOut();
            animateWrapBorder(passwordWrap, currentWrapColor(passwordWrap), passwordField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            passwordError.fadeIn(msg);
            animateWrapBorder(passwordWrap, currentWrapColor(passwordWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validateConfirmPassword(boolean show) {
        String a = new String(passwordField.getPassword());
        String b = new String(confirmPasswordField.getPassword());
        String msg = null;
        if (b.trim().isEmpty()) {
            msg = "Confirm your password.";
        } else if (!Objects.equals(a, b)) {
            msg = "Passwords do not match.";
        }
        if (msg == null) {
            confirmError.fadeOut();
            animateWrapBorder(confirmWrap, currentWrapColor(confirmWrap), confirmPasswordField.isFocusOwner() ? FOCUS : BORDER, 120);
            return true;
        }
        if (show) {
            confirmError.fadeIn(msg);
            animateWrapBorder(confirmWrap, currentWrapColor(confirmWrap), ERROR, 120);
        }
        return false;
    }

    private boolean validateConsent(boolean show) {
        if (consent.isSelected()) {
            clearConsentError();
            return true;
        }
        if (show) {
            consentError.fadeIn("Consent is required to create an account.");
        }
        return false;
    }

    private void clearConsentError() {
        consentError.fadeOut();
    }

    private void updateCreateEnabled() {
        boolean ok = validateFirstName(false)
                & validateLastName(false)
                & validateAccountType(false)
                & validateEmail(false)
                & validatePhone(false)
                & validateLocation(false)
                & validatePassword(false)
                & validateConfirmPassword(false)
                & consent.isSelected();
        createButton.setEnabled(ok && !loadingState);
    }

    private void attemptCreate() {
        if (loadingState) {
            return;
        }
        formError.fadeOut();
        boolean ok = validateFirstName(true)
                & validateLastName(true)
                & validateAccountType(true)
                & validateEmail(true)
                & validatePhone(true)
                & validateLocation(true)
                & validatePassword(true)
                & validateConfirmPassword(true)
                & validateConsent(true);
        if (!ok) {
            updateCreateEnabled();
            return;
        }

        setLoading(true);
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        char[] password = passwordField.getPassword();

        new javax.swing.SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                boolean created = UserStore.createUser(email, RoleMenuBar.Role.ENGINEER, password);
                if (created) {
                    EngineerProfileStore.setName(firstName, lastName);
                }
                return created;
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    setLoading(false);
                    if (ok) {
                        new CreateAccountConfirmation().setVisible(true);
                        dispose();
                    } else {
                        formError.fadeIn("An account with this email already exists. Please use a different email or log in.");
                    }
                } catch (Exception ex) {
                    setLoading(false);
                    formError.fadeIn("Account creation failed. Please retry.");
                }
            }
        }.execute();
    }

    private void setLoading(boolean on) {
        loadingState = on;
        firstNameField.setEnabled(!on);
        lastNameField.setEnabled(!on);
        accountType.setEnabled(false);
        emailField.setEnabled(!on);
        phoneField.setEnabled(!on);
        locationField.setEnabled(!on);
        passwordField.setEnabled(!on);
        confirmPasswordField.setEnabled(!on);
        togglePassword.setEnabled(!on);
        toggleConfirmPassword.setEnabled(!on);
        consent.setEnabled(!on);
        createButton.setEnabled(!on && createButton.isEnabled());
        loading.setVisible(on);
        createButton.setText(on ? "Creating…" : "Create account");
        JRootPane rp = getRootPane();
        if (rp != null) {
            rp.setDefaultButton(on ? null : createButton);
        }
        SwingUtilities.invokeLater(() -> {
            layoutRoot.revalidate();
            layoutRoot.repaint();
        });
    }

    private void openPolicyDialog(String title, String body) {
        JDialog d = new JDialog(this, title, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel t = new JLabel(title);
        t.setFont(H2);
        t.setForeground(TEXT);

        JLabel msg = new JLabel("<html><div style='width:420px'>" + escapeHtml(body) + "</div></html>");
        msg.setFont(BODY);
        msg.setForeground(MUTED);

        JButton ok = new JButton("Close");
        ok.setFont(BODY_BOLD);
        ok.addActionListener(e -> d.dispose());

        p.add(t, BorderLayout.NORTH);
        p.add(msg, BorderLayout.CENTER);
        p.add(ok, BorderLayout.SOUTH);

        d.setContentPane(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
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

    private interface SimpleDocumentRunnable {
        void run();
    }

    private static final class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final SimpleDocumentRunnable r;

        private SimpleDocumentListener(SimpleDocumentRunnable r) {
            this.r = r;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            r.run();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            r.run();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            r.run();
        }
    }

    public static void main(String[] args) {
        new CreateAccount();
    }
}
