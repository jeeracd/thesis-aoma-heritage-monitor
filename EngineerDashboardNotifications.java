import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class EngineerDashboardNotifications {
    private final SwingDebouncer saveDebouncer = new SwingDebouncer(400, this::persistSettings);

    private final JCheckBox inAppCheck = new JCheckBox("In-app notifications");
    private final JCheckBox emailCheck = new JCheckBox("Email notifications");

    private final JToggleButton rightAway = createToggleOption("Right away", false);
    private final JToggleButton onceADay = createToggleOption("Once a day", false);
    private final JToggleButton never = createToggleOption("Never", false);

    private final JSpinner vibrationThreshold = new JSpinner(new SpinnerNumberModel(0.50, 0.00, 10.00, 0.05));
    private final JSpinner frequencyShiftThreshold = new JSpinner(new SpinnerNumberModel(0.10, 0.00, 10.00, 0.01));

    private final DefaultListModel<EngineerNotificationCenter.Notification> feedModel = new DefaultListModel<>();
    private final JList<EngineerNotificationCenter.Notification> feedList = new JList<>(feedModel);
    private Runnable removeFeedListener = () -> {};

    public JPanel createNotificationsTab() {
        EngineerNotificationCenter.get();
        EngineerNotificationRouter.get();

        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
        tabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tabPanel.add(createRoutingSection());
        tabPanel.add(Box.createVerticalStrut(16));
        tabPanel.add(createEmailFrequencySection());
        tabPanel.add(Box.createVerticalStrut(16));
        tabPanel.add(createThresholdSection());
        tabPanel.add(Box.createVerticalStrut(16));
        tabPanel.add(createFeedSection());
        tabPanel.add(Box.createVerticalStrut(18));
        tabPanel.add(createSaveButton());

        return tabPanel;
    }

    private JPanel createRoutingSection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(16, 16, 16, 16)));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel label = new JLabel("Notification channels");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(0, 0, 10, 0));

        inAppCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        inAppCheck.setSelected(EngineerPreferences.isNotifyInApp());
        emailCheck.setSelected(EngineerPreferences.isNotifyEmail());

        inAppCheck.addActionListener(e -> saveDebouncer.call());
        emailCheck.addActionListener(e -> saveDebouncer.call());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        row.add(inAppCheck);
        row.add(emailCheck);

        wrapper.add(label);
        wrapper.add(row);
        return wrapper;
    }

    private JPanel createEmailFrequencySection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(16, 16, 16, 16)));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel label = new JLabel("Email frequency");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        optionsPanel.setBorder(new LineBorder(Color.BLACK, 2));

        ButtonGroup group = new ButtonGroup();
        group.add(rightAway);
        group.add(onceADay);
        group.add(never);

        EngineerPreferences.NotificationFrequency freq = EngineerPreferences.getNotificationFrequency();
        rightAway.setSelected(freq == EngineerPreferences.NotificationFrequency.RIGHT_AWAY);
        onceADay.setSelected(freq == EngineerPreferences.NotificationFrequency.ONCE_A_DAY);
        never.setSelected(freq == EngineerPreferences.NotificationFrequency.NEVER);

        rightAway.addActionListener(e -> saveDebouncer.call());
        onceADay.addActionListener(e -> saveDebouncer.call());
        never.addActionListener(e -> saveDebouncer.call());

        optionsPanel.add(rightAway);
        optionsPanel.add(onceADay);
        optionsPanel.add(never);

        wrapper.add(label);
        wrapper.add(optionsPanel);
        return wrapper;
    }

    private JPanel createThresholdSection() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(16, 16, 16, 16)));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel label = new JLabel("Alert thresholds");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(0, 0, 12, 0));

        vibrationThreshold.setValue(EngineerPreferences.getVibrationRmsThreshold());
        frequencyShiftThreshold.setValue(EngineerPreferences.getFrequencyShiftThreshold());

        vibrationThreshold.addChangeListener(e -> saveDebouncer.call());
        frequencyShiftThreshold.addChangeListener(e -> saveDebouncer.call());

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        grid.add(new JLabel("Vibration RMS"), gbc);
        gbc.gridx = 1;
        grid.add(vibrationThreshold, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        grid.add(new JLabel("Frequency shift"), gbc);
        gbc.gridx = 1;
        grid.add(frequencyShiftThreshold, gbc);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFeedSection() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(16, 16, 16, 16)));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JLabel label = new JLabel("Real-time notifications");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        feedList.setVisibleRowCount(8);
        feedList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof EngineerNotificationCenter.Notification n) {
                    String text = n.getSeverity().name() + ": " + n.getTitle();
                    setText(text);
                    Font f = getFont();
                    setFont(f.deriveFont(n.isRead() ? Font.PLAIN : Font.BOLD));
                }
                return c;
            }
        });

        feedList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            EngineerNotificationCenter.Notification n = feedList.getSelectedValue();
            if (n == null) {
                return;
            }
            n.setRead(true);
            feedList.repaint();
            if (n.getMessage() != null && !n.getMessage().isBlank()) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(feedList), n.getMessage(), n.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton test = new JButton("Send Test Notification");
        test.setFocusPainted(false);
        test.addActionListener(e -> EngineerNotificationCenter.get().push(EngineerNotificationCenter.Severity.INFO, "Test Notification", "This is a test message."));

        JButton markAllRead = new JButton("Mark All Read");
        markAllRead.setFocusPainted(false);
        markAllRead.addActionListener(e -> {
            EngineerNotificationCenter.get().markAllRead();
            for (int i = 0; i < feedModel.size(); i++) {
                feedModel.get(i).setRead(true);
            }
            feedList.repaint();
        });

        JButton sendDigestNow = new JButton("Send Digest Now");
        sendDigestNow.setFocusPainted(false);
        sendDigestNow.addActionListener(e -> {
            boolean ok = EngineerNotificationRouter.get().sendDigestNow();
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(feedList), ok ? "Digest sent (or queued in mail client)." : "No digest to send.", "Email Digest", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.add(test);
        actions.add(markAllRead);
        actions.add(sendDigestNow);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(feedList), BorderLayout.CENTER);
        wrapper.add(actions, BorderLayout.SOUTH);

        loadFeedSnapshot();
        removeFeedListener = EngineerNotificationCenter.get().addListener(n -> SwingUtilities.invokeLater(() -> {
            if (!EngineerPreferences.isNotifyInApp()) {
                return;
            }
            feedModel.add(0, n);
            if (feedModel.size() > 200) {
                feedModel.remove(feedModel.size() - 1);
            }
        }));

        boolean enabled = EngineerPreferences.isNotifyInApp();
        feedList.setEnabled(enabled);

        inAppCheck.addActionListener(e -> feedList.setEnabled(inAppCheck.isSelected()));

        return wrapper;
    }

    private void loadFeedSnapshot() {
        feedModel.clear();
        if (!EngineerPreferences.isNotifyInApp()) {
            return;
        }
        List<EngineerNotificationCenter.Notification> snapshot = EngineerNotificationCenter.get().snapshot();
        for (EngineerNotificationCenter.Notification n : snapshot) {
            feedModel.addElement(n);
        }
    }

    private JToggleButton createToggleOption(String text, boolean selected) {
        JToggleButton button = new JToggleButton(text, selected);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
        button.setBorder(new LineBorder(Color.BLACK, 1));

        button.setBackground(selected ? new Color(150, 190, 240) : Color.WHITE);
        button.setOpaque(true);

        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setBackground(new Color(150, 190, 240));
            } else {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createSaveButton() {
        JButton saveButton = new JButton(EngineerUiNames.ACTION_SAVE_CHANGES_EXIT);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(220, 45));
        saveButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
        persistSettings();
        JOptionPane.showMessageDialog(
                null,
                EngineerUiNames.DIALOG_CHANGES_SAVED,
                EngineerUiNames.DIALOG_SUCCESS_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );

        new EngineerStartingPage().setVisible(true);

        SwingUtilities.getWindowAncestor(saveButton).dispose();
        });

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false); 
        wrapper.add(saveButton);

        return wrapper;
    }

    private void persistSettings() {
        EngineerPreferences.setNotifyInApp(inAppCheck.isSelected());
        EngineerPreferences.setNotifyEmail(emailCheck.isSelected());

        EngineerPreferences.NotificationFrequency freq;
        if (never.isSelected()) {
            freq = EngineerPreferences.NotificationFrequency.NEVER;
        } else if (onceADay.isSelected()) {
            freq = EngineerPreferences.NotificationFrequency.ONCE_A_DAY;
        } else {
            freq = EngineerPreferences.NotificationFrequency.RIGHT_AWAY;
        }
        EngineerPreferences.setNotificationFrequency(freq);

        EngineerPreferences.setVibrationRmsThreshold(((Number) vibrationThreshold.getValue()).doubleValue());
        EngineerPreferences.setFrequencyShiftThreshold(((Number) frequencyShiftThreshold.getValue()).doubleValue());
    }
}
