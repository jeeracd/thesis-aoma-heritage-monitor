import javax.swing.*;
import java.awt.*;
 
public final class CalendarDatePicker {
    private CalendarDatePicker() {}
 
    public static void show(Window owner, JTextField dateField) {
        JDialog calendarDialog = new JDialog(owner, "Select Date", Dialog.ModalityType.APPLICATION_MODAL);
        calendarDialog.setSize(340, 320);
        calendarDialog.setLocationRelativeTo(owner);
        calendarDialog.setLayout(new BorderLayout());
 
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
 
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
 
        JComboBox<String> monthBox = new JComboBox<>(months);
        monthBox.setFont(new Font("Arial", Font.PLAIN, 12));
 
        java.time.LocalDate now = java.time.LocalDate.now();
        monthBox.setSelectedIndex(now.getMonthValue() - 1);
 
        JSpinner yearSpinner = new JSpinner(
                new SpinnerNumberModel(now.getYear(), 1500, 2100, 1)
        );
        yearSpinner.setFont(new Font("Arial", Font.PLAIN, 12));
 
        topPanel.add(monthBox);
        topPanel.add(yearSpinner);
 
        calendarDialog.add(topPanel, BorderLayout.NORTH);
 
        JPanel calendarPanel = new JPanel();
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        Runnable buildCalendar = () -> {
            calendarPanel.removeAll();
            calendarPanel.setLayout(new GridLayout(0, 7, 5, 5));
 
            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String d : days) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("Arial", Font.BOLD, 12));
                calendarPanel.add(lbl);
            }
 
            int year = (int) yearSpinner.getValue();
            int month = monthBox.getSelectedIndex() + 1;
 
            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);
 
            int startDay = firstDay.getDayOfWeek().getValue() % 7;
            int daysInMonth = firstDay.lengthOfMonth();
 
            for (int i = 0; i < startDay; i++) {
                calendarPanel.add(new JLabel(""));
            }
 
            for (int day = 1; day <= daysInMonth; day++) {
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setFocusPainted(false);
 
                int selectedDay = day;
                dayBtn.addActionListener(ev -> {
                    dateField.setText(String.format("%04d-%02d-%02d", year, month, selectedDay));
                    calendarDialog.dispose();
                });
 
                calendarPanel.add(dayBtn);
            }
 
            calendarPanel.revalidate();
            calendarPanel.repaint();
        };
 
        buildCalendar.run();
 
        monthBox.addActionListener(ev -> buildCalendar.run());
        yearSpinner.addChangeListener(ev -> buildCalendar.run());
 
        calendarDialog.add(calendarPanel, BorderLayout.CENTER);
        calendarDialog.setVisible(true);
    }
}
