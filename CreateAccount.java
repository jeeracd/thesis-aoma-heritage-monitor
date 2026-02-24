import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class CreateAccount extends JFrame {

    public CreateAccount() {
        setTitle("AOMA-Heritage Monitor");
        setSize(800, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(root);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel appTitle = new JLabel("AOMA-Heritage Monitor");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel byLine = new JLabel("By RJ13 Connectors");
        byLine.setFont(new Font("SansSerif", Font.PLAIN, 13));
        byLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pageTitle = new JLabel("Create your Account");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        pageTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(appTitle);
        header.add(Box.createVerticalStrut(5));
        header.add(byLine);
        header.add(Box.createVerticalStrut(10));
        header.add(pageTitle);

        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(30, 0));
        body.setBorder(new EmptyBorder(20, 0, 0, 0));
        root.add(body, BorderLayout.CENTER);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel slogan = new JLabel("Monitoring heritage, preserving history.");
        slogan.setFont(new Font("SansSerif", Font.BOLD, 14));
        slogan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea leftText = createTextArea(
            "AOMA-Heritage Monitor provides a specialized platform for the real-time structural "
        + "health monitoring of historical landmarks. By leveraging E-M Enhanced Automated "
        + "Operational Modal Analysis (AOMA), our system ensures that heritage buildings are "
        + "preserved through precise, data-backed technical insights.\n\n"
        + "Whether you are overseeing city-wide projects or conducting on-site technical "
        + "evaluations, creating an account gives you the tools to monitor structural integrity "
        + "and ensure the longevity of our cultural treasures.\n\n"
        + "With an AOMA-Heritage account, you can:\n\n"
        + "• Oversee and Manage: Access high-level summaries of all heritage sites.\n\n"
        + "• Analyze Technical Data: View real-time vibration and integrity logs.\n\n"
        + "• Report in Real-Time: Submit field observations and monitor alerts.\n\n"
        + "• Collaborate for Conservation: Connect with engineers and officials."
        );
        leftText.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(slogan);
        left.add(Box.createVerticalStrut(15));
        left.add(leftText);

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setPreferredSize(new Dimension(420, 0));
        formWrapper.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 3),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel formTitle = new JLabel("Create your account");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formWrapper.add(formTitle);
        formWrapper.add(Box.createVerticalStrut(15));

        JPanel nameRow = new JPanel(new GridLayout(1, 2, 10, 0));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameRow.add(createField("First Name", new JTextField("Juan")));
        nameRow.add(createField("Last Name", new JTextField("Dela Cruz")));

        Dimension namePref = nameRow.getPreferredSize();
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, namePref.height));

        JComboBox<String> accountType = new JComboBox<>(new String[]{
                "Structural Engineer",
                "LGU Officer",
                "Local Government Unit Head"
        });

        JTextField email = new JTextField("JuanDelaCruz@email.com");

        JPanel contactRow = new JPanel(new GridLayout(1, 2, 10, 0));
        contactRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        contactRow.add(createField("Phone Number", new JTextField("+63123456789")));
        contactRow.add(createField("City, Province", new JTextField("Valenzuela, Metro Manila")));

        Dimension contactPref = contactRow.getPreferredSize();
        contactRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, contactPref.height));

        JPasswordField password = new JPasswordField("password");

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(120, 35));
        submitButton.setMaximumSize(new Dimension(120, 35));
        submitButton.setBorder(new LineBorder(Color.BLACK, 2));
        submitButton.setFocusPainted(false);

        

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        submitPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        submitPanel.setOpaque(false);
        submitPanel.add(submitButton);

        formWrapper.add(nameRow);
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(createField("Account type", accountType));
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(createField("Email Address", email));
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(contactRow);
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(createField("Password", password));
        formWrapper.add(Box.createVerticalStrut(25));
        formWrapper.add(submitPanel);

        body.add(left, BorderLayout.CENTER);
        body.add(formWrapper, BorderLayout.EAST);

        setVisible(true);
    }

    private JTextArea createTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return area;
    }

    private JPanel createField(String labelText, JComponent field) {
    JPanel panel = new JPanel(new BorderLayout(0, 5));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel label = new JLabel("*" + labelText);
    label.setFont(new Font("SansSerif", Font.PLAIN, 12));

    field.setFont(new Font("SansSerif", Font.PLAIN, 13));
    field.setBorder(new LineBorder(Color.BLACK, 2));

    panel.add(label, BorderLayout.NORTH);
    panel.add(field, BorderLayout.CENTER);

    Dimension pref = panel.getPreferredSize();
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

    return panel;
}
    public static void main(String[] args) {
        new CreateAccount();
    }
}