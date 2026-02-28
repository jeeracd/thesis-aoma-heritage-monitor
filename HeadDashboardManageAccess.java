import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class HeadDashboardManageAccess extends JFrame {

    public HeadDashboardManageAccess() {

        setTitle("AOMA-Heritage Monitor - Manage Access (HEAD)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel titleLabel = new JLabel("Manage Access");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 20, 200, 25);
        add(titleLabel);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBorder(new LineBorder(Color.BLACK, 2));
        mainPanel.setBounds(20, 60, 550, 480);
        add(mainPanel);

        JLabel membersLabel = new JLabel("Project Members");
        membersLabel.setFont(new Font("Arial", Font.BOLD, 13));
        membersLabel.setBounds(15, 10, 200, 20);
        mainPanel.add(membersLabel);

        JLabel roleLabel = new JLabel("Roles");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roleLabel.setBounds(450, 10, 50, 20);
        mainPanel.add(roleLabel);

        JPanel headerLine = new JPanel();
        headerLine.setBackground(Color.BLACK);
        headerLine.setBounds(2, 38, 580, 2);
        mainPanel.add(headerLine);

        addMember(mainPanel, 50, "LGU OFFICER", "LGUOFFICER@officer.com");
        addMember(mainPanel, 110, "STRUCTURAL ENGINEER", "STRUCTURALENGINEER@engr.com");
        addMember(mainPanel, 170, "LGU HEAD", "LGUHEAD@head.com");

        JLabel otherMembersLabel = new JLabel("Other members");
        otherMembersLabel.setFont(new Font("Arial", Font.BOLD, 12));
        otherMembersLabel.setBounds(15, 240, 200, 20);
        mainPanel.add(otherMembersLabel);

        JLabel addMembersLabel = new JLabel("Add other Project Members:");
        addMembersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        addMembersLabel.setBounds(15, 265, 200, 20);
        mainPanel.add(addMembersLabel);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 13));
        emailField.setBounds(15, 295, 260, 34);
        emailField.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        emailField.setFocusable(true);
        mainPanel.add(emailField);

        JLabel roleComboLabel = new JLabel("Roles");
        roleComboLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleComboLabel.setBounds(300, 275, 170, 15);
        mainPanel.add(roleComboLabel);

        JComboBox<String> roleComboBox = new JComboBox<>();
        roleComboBox.addItem("Can View");
        roleComboBox.addItem("Can View and Edit");
        roleComboBox.setBounds(290, 295, 170, 34);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        roleComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainPanel.add(roleComboBox);

        JButton sendEmailBtn = new JButton("Send email");
        sendEmailBtn.setBounds(455, 295, 90, 34);
        sendEmailBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        sendEmailBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainPanel.add(sendEmailBtn);

        JButton saveBtn = new JButton("Save Changes & Exit");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBounds(200, 560, 200, 35);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(saveBtn);

        setVisible(true);
    }

    private void addMember(JPanel panel, int y, String role, String email) {

        JLabel iconLabel = new JLabel("👤");
        iconLabel.setBounds(15, y, 30, 30);
        panel.add(iconLabel);

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roleLabel.setBounds(55, y, 300, 18);
        panel.add(roleLabel);

        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(Color.DARK_GRAY);
        emailLabel.setBounds(55, y + 18, 300, 15);
        panel.add(emailLabel);

        JComboBox<String> roleDropDown = new JComboBox<>();
        roleDropDown.addItem("Can View");
        roleDropDown.addItem("Can View and Edit");

        roleDropDown.setFont(new Font("Arial", Font.PLAIN, 12));
        roleDropDown.setBounds(390, y, 150, 34);
        roleDropDown.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(roleDropDown);

        // For LGU HEAD, set role to "Owner" and disable dropdown
        if (role.equals("LGU HEAD")) {
        roleDropDown.addItem("Owner");
        roleDropDown.setSelectedItem("Owner");
        roleDropDown.setEnabled(false);
        }


    }

    public static void main(String[] args) {
        new HeadDashboardManageAccess();
    }
}