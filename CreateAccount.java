import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class CreateAccount extends JFrame {

    public CreateAccount() {
        setTitle("AOMA-Heritage Monitor - Create Account");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(30, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        /* ================= LEFT PANEL ================= */
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("AOMA-Heritage Monitor");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("By RJ13 Connectors");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel tagline = new JLabel("Monitoring heritage, preserving history.");
        tagline.setFont(new Font("SansSerif", Font.BOLD, 14));

        JTextArea description = new JTextArea();
        description.setText(
                "AOMA-Heritage Monitor provides a specialized platform for the real-time structural "
              + "health monitoring of historical landmarks. By leveraging Enhanced Automated "
              + "Operational Modal Analysis (AOMA), the system ensures that heritage buildings are "
              + "preserved through precise, data-backed technical insights.\n\n"
              + "Whether you are overseeing city-wide projects or conducting on-site technical "
              + "evaluations, creating an account gives you the tools to monitor structural integrity "
              + "and ensure the longevity of cultural treasures.\n\n"
              + "With an AOMA-Heritage account, you can:\n\n"
              + "• Oversee and Manage – Access high-level summaries of heritage sites\n"
              + "• Analyze Technical Data – View real-time vibration and integrity logs\n"
              + "• Report in Real-Time – Submit observations and receive alerts\n"
              + "• Collaborate for Conservation – Work with engineers and officials"
        );
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));

        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subtitle);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(tagline);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(description);

        /* ================= RIGHT PANEL ================= */
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(360, 0));
        rightPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel formTitle = new JLabel("Create your account");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(15));

        JPanel namePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        namePanel.add(createField("First Name", new JTextField("Juan")));
        namePanel.add(createField("Last Name", new JTextField("Dela Cruz")));

        JComboBox<String> accountType = new JComboBox<>(new String[]{
                "Local Government Unit Head",
                "LGU Officer",
                "Structural Engineer"
        });

        JTextField email = new JTextField("JuanDelaCruz@email.com");

        JPanel contactPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contactPanel.add(createField("Phone Number", new JTextField("+63123456789")));
        contactPanel.add(createField("City, Province", new JTextField("Valenzuela, Metro Manila")));

        JPasswordField password = new JPasswordField();

        JButton submit = new JButton("Submit");
        submit.setFocusPainted(false);
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(namePanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createField("Account Type", accountType));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createField("Email Address", email));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(contactPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createField("Password", password));
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(submit);

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        setVisible(true);
    }

    /* ================= FIELD WRAPPER ================= */
    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(new LineBorder(Color.BLACK, 1));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CreateAccount::new);
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      