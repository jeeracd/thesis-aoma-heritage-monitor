import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class HeadStartingPage extends JFrame {

    public HeadStartingPage() {
        setTitle("AOMA-Heritage Monitor - LGU Head Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        JTabbedPane tabsUI = new JTabbedPane(JTabbedPane.TOP);
        tabsUI.setFont(new Font("Arial", Font.BOLD, 17));
        tabsUI.setBackground(Color.LIGHT_GRAY);
        tabsUI.setForeground(Color.BLACK);

        JPanel headPanel = new JPanel(null);

        tabsUI.addTab("Projects", headPanel);
        tabsUI.addTab("View", new JPanel());
        tabsUI.addTab("Help", new JPanel());

        JPopupMenu projectsMenu = new JPopupMenu();

        JMenuItem newProject = new JMenuItem("New Project");
        JMenuItem openProject = new JMenuItem("Open Project");
        JMenuItem importCsv = new JMenuItem("Import Sensor Data (.csv)");
        JMenuItem exportPdf = new JMenuItem("Export Report (PDF)");
        JMenuItem preferences = new JMenuItem("Preferences");
        JMenuItem exit = new JMenuItem("Exit");

        projectsMenu.add(newProject);
        projectsMenu.add(openProject);
        projectsMenu.addSeparator();
        projectsMenu.add(importCsv);
        projectsMenu.add(exportPdf);
        projectsMenu.addSeparator();
        projectsMenu.add(preferences);
        projectsMenu.addSeparator();
        projectsMenu.add(exit);

        JButton projectsDropdownBtn = new JButton("▼");
        projectsDropdownBtn.setFont(new Font("Arial", Font.BOLD, 12));
        projectsDropdownBtn.setFocusPainted(false);
        projectsDropdownBtn.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        projectsDropdownBtn.setBackground(Color.LIGHT_GRAY);
        projectsDropdownBtn.setForeground(Color.WHITE);
        projectsDropdownBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        projectsDropdownBtn.setBounds(92,11,28,22);

        projectsDropdownBtn.addActionListener(e ->
        projectsMenu.show(projectsDropdownBtn,0,projectsDropdownBtn.getHeight())
        );

        tabsUI.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
    @Override
        protected void paintTabBackground(
                Graphics g,
                int tabPlacement,
                int tabIndex,
                int x, int y, int w, int h,
                boolean isSelected
        ) {
            if (isSelected) {
                g.setColor(new Color(0, 102, 204)); 
                g.fillRect(x, y, w, h);
            }
        }

        @Override
        protected void paintText(
                Graphics g,
                int tabPlacement,
                Font font,
                FontMetrics metrics,
                int tabIndex,
                String title,
                Rectangle textRect,
                boolean isSelected
        ) {
            g.setFont(font);
            g.setColor(isSelected ? Color.WHITE : Color.BLACK);
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

        @Override
        protected Insets getTabInsets(int tabPlacement, int tabIndex) {
            return new Insets(6, 20, 6, 20);
        }

        @Override
        protected Insets getTabAreaInsets(int tabPlacement) {
            return new Insets(5, 10, 5, 0);
        }
    });
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(new java.awt.Dimension(1300, 850));

        layeredPane.add(projectsDropdownBtn, JLayeredPane.PALETTE_LAYER); //for dropdown btn

        tabsUI.setBounds(0, 0, 1395, 770);
        layeredPane.add(tabsUI, JLayeredPane.DEFAULT_LAYER);

        JLabel LGUHeadLabel = new JLabel("LGU HEAD ACCOUNT");
        LGUHeadLabel.setFont(new Font("Arial", Font.BOLD, 14));
        LGUHeadLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        LGUHeadLabel.setBounds(1080, 5, 280, 38);

        layeredPane.add(LGUHeadLabel, JLayeredPane.PALETTE_LAYER);

        JPanel centerPanelDescription = new JPanel(new BorderLayout());
        centerPanelDescription.setBounds(10, 20, 1380, 40);
        Border firstBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanelDescription.setBorder(firstBorder);

        JLabel centerTitleLabel = new JLabel(
                "Automated - Operational Modal Analysis to Monitor the Safety and Serviceability of Heritage Buildings",JLabel.CENTER
        );
        centerTitleLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
        centerTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ImageIcon userIcon = new ImageIcon("usericon.png");
        Image userImgScaled = userIcon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
        JLabel userIconLabel = new JLabel(new ImageIcon(userImgScaled));
        userIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        centerPanelDescription.add(centerTitleLabel, BorderLayout.CENTER);
        centerPanelDescription.add(userIconLabel, BorderLayout.EAST);

        headPanel.add(centerPanelDescription);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBounds(10, 70, 1380, 648);
        Border secondBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        centerPanel.setBorder(secondBorder);
        headPanel.add(centerPanel);

        JLabel greetingLabel = new JLabel("Welcome, Juan Dela Cruz!", JLabel.LEFT); //add database here [collect the fullname of the head]
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        greetingLabel.setBorder( BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)), 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        centerPanel.add(greetingLabel, BorderLayout.NORTH);

        JPanel textContainer = new JPanel();
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setOpaque(false);

        ImageIcon imageIcon = new ImageIcon("emptybox.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel textHeaderLabel = new JLabel(
                "Welcome to the AOMA-Heritage Monitor, LGU HEAD ACCOUNT!",JLabel.CENTER);
        textHeaderLabel.setFont(new Font("Arial", Font.BOLD, 22));
        textHeaderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel textSubheaderLabel = new JLabel("You do not have any active monitoring projects yet."
              + " To begin tracking the safety and serviceability of a heritage building,"
              + " please initialize a project.",
                JLabel.CENTER
        );
        textSubheaderLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        textSubheaderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JButton newProjectButton = new JButton("Start a New Project");
        newProjectButton.setFont(new Font("Arial", Font.BOLD, 16));
        newProjectButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        newProjectButton.setBackground(new Color(0, 45, 4));
        newProjectButton.setForeground(Color.WHITE);
        newProjectButton.setFocusPainted(false);
        newProjectButton.setOpaque(true);
        newProjectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newProjectButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 70, 150), 2),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        newProjectButton.addActionListener(e -> {
        JOptionPane.showMessageDialog(
                this,
                "New Project initialization process will start.",
                "New Project",
                JOptionPane.INFORMATION_MESSAGE
        );
        new HeadEditStructuralDetails();
        this.dispose();
        });

        textContainer.add(Box.createVerticalGlue());
        textContainer.add(imageLabel);
        textContainer.add(Box.createVerticalStrut(12)); 
        textContainer.add(textHeaderLabel);
        textContainer.add(Box.createVerticalStrut(12)); 
        textContainer.add(textSubheaderLabel);
        textContainer.add(Box.createVerticalStrut(20));
        textContainer.add(newProjectButton);
        textContainer.add(Box.createVerticalGlue());

        centerPanel.add(textContainer, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setPreferredSize(new java.awt.Dimension(1400, 45));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(120, 120, 120)) );

        JLabel footerLabel = new JLabel("Status: ESP32 Hub Not Connected");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        footerLabel.setForeground(Color.RED);
        footerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        footerPanel.add(footerLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(layeredPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    public static void main(String[] args) {
        new HeadStartingPage();
    }
}