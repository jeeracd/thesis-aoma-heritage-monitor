import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class EditStructuralDetails extends JFrame {

    public EditStructuralDetails() {
        setTitle("AOMA-Heritage Monitor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(600, 650);
        setLocationRelativeTo(null);
        setLayout(null);

        int labelX = 40;
        int fieldX = 220;
        int yStart = 150;
        int yGap = 45;
        int fieldWidth = 320;
        int labelWidth = 180;
        int height = 30;

        JLabel projectName = new JLabel("Project Name");
        projectName.setFont(new Font("Arial", Font.BOLD, 20));
        projectName.setBounds(20, 20, 300, 30);
        add(projectName);

        JTextField projectField = new JTextField("Monitoring of San Diego de Alcala Parish Church"); 
        int projectFieldY = 55; 
        setupTextField(projectField, 20, projectFieldY, 500, height);
        add(projectField);   

        JLabel headerLabel = new JLabel("Edit Structural Details");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBounds(20, 100, 300, 30);
        add(headerLabel);

        addLabel("Building Name:", labelX, yStart, labelWidth, height);
        JTextField nameField = new JTextField("San Diego de Alcala Parish Church"); 
        setupTextField(nameField, fieldX, yStart, fieldWidth, height);
        add(nameField);

        addLabel("Date Constructed:", labelX, yStart + yGap, labelWidth, height);
        JTextField dateField = new JTextField("1697");
        setupTextField(dateField, fieldX, yStart + yGap, fieldWidth, height);
        add(dateField);

        JButton calendarBtn = new JButton("📅");
        calendarBtn.setBounds(fieldX + fieldWidth + 5, yStart + yGap, 30, 30);
        add(calendarBtn);

        addLabel("Materials Used:", labelX, yStart + (yGap * 2), labelWidth, height);
        JTextField materialField = new JTextField("Wood, Brick");
        setupTextField(materialField, fieldX, yStart + (yGap * 2), fieldWidth, height);
        add(materialField);

        addLabel("Conservation Status:", labelX, yStart + (yGap * 3), labelWidth, height);
        JTextField statusField = new JTextField("National Heritage");
        setupTextField(statusField, fieldX, yStart + (yGap * 3), fieldWidth, height);
        add(statusField);

        addLabel("Address:", labelX, yStart + (yGap * 4), labelWidth, height);
        JTextField addressField = new JTextField("1444 Marcelo H. Del Pilar St, Pulo, Poblacion");
        setupTextField(addressField, fieldX, yStart + (yGap * 4), fieldWidth, height);
        add(addressField);

        addLabel("Function:", labelX, yStart + (yGap * 5), labelWidth, height);
        JTextField functionField = new JTextField("Church");
        setupTextField(functionField, fieldX, yStart + (yGap * 5), fieldWidth, height);
        add(functionField);

        addLabel("Description:", labelX, yStart + (yGap * 6), labelWidth, height);
        JTextArea descArea = new JTextArea("San Diego de Alcala Parish Church, colloquially known as Polo Church, is a Roman Catholic church in Valenzuela...");
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBounds(fieldX, yStart + (yGap * 6), fieldWidth, 100);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        descScroll.setBorder(border);
        add(descScroll);

        int btnY = yStart + (yGap * 6) + 120;
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setForeground(Color.RED);
        cancelBtn.setBounds(180, btnY, 100, 25);
        cancelBtn.addActionListener(e -> {

            Confirmation exitConfirm = new Confirmation(
                this, 
                "Confirm Exit", 
                "This window is asking you to confirm that you want to leave - data you have entered may not be save", 
                "Exit", 
                "Stay on Window", 
                Color.RED, 
                new Color(0, 153, 0)
            );
            exitConfirm.setVisible(true);

            if (exitConfirm.isConfirmActionTaken()) {
                dispose(); 
            }
        });
        add(cancelBtn);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setForeground(new Color(0, 153, 0));
        saveBtn.setBounds(300, btnY, 150, 25);
        saveBtn.addActionListener(e -> {

            Confirmation saveConfirm = new Confirmation(
                this, 
                "Confirm Save", 
                "This window is asking you to confirm that you want to save the changes - data you have entered will be save", 
                "Save Changes", 
                "Go Back", 
                new Color(0, 153, 0), 
                Color.RED
            );
            saveConfirm.setVisible(true);

            if (saveConfirm.isConfirmActionTaken()) {
                JOptionPane.showMessageDialog(this, "Changes Saved Successfully!");
                dispose();
            }
        });
        add(saveBtn);

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBounds(0, 570, 600, 60);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        
        JLabel footerLabel = new JLabel(" Status: ESP32 Hub Is Connected");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        footerLabel.setForeground(new Color(0, 153, 0));
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        add(footerPanel);

        setVisible(true);
    }

    private void addLabel(String text, int x, int y, int w, int h) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBounds(x, y, w, h);
        add(label);
    }

    private void setupTextField(JTextField tf, int x, int y, int w, int h) {
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setBounds(x, y, w, h);
        tf.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public static void main(String[] args) {
        new EditStructuralDetails();
    }
}