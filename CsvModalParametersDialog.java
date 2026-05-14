import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.List;
 
public final class CsvModalParametersDialog extends JDialog {
    public CsvModalParametersDialog(Frame owner, File csv, CsvModalParameters params) {
        super(owner, "CSV Modal Parameters", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
 
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
 
        JLabel title = new JLabel(params.title());
        title.setFont(new Font("Arial", Font.BOLD, 16));
        top.add(title);
 
        String fileLine = csv == null ? "" : csv.getAbsolutePath();
        JLabel file = new JLabel(fileLine);
        file.setFont(new Font("Arial", Font.PLAIN, 12));
        file.setForeground(Color.DARK_GRAY);
        top.add(file);
 
        JLabel meta = new JLabel(metaText(params));
        meta.setFont(new Font("Arial", Font.PLAIN, 12));
        meta.setForeground(Color.DARK_GRAY);
        top.add(Box.createVerticalStrut(4));
        top.add(meta);
 
        add(top, BorderLayout.NORTH);
 
        JTable table = new JTable(new FieldsModel(params.fields()));
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);
 
        JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        add(sp, BorderLayout.CENTER);
 
        JButton copy = new JButton("Copy JSON");
        JButton close = new JButton("Close");
 
        copy.addActionListener(e -> {
            String txt = params.toJsonLikeString();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txt), null);
            Toast.show(this, "Copied to clipboard", new Color(0, 128, 0), 1600);
        });
        close.addActionListener(e -> dispose());
 
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.add(copy);
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
 
        setPreferredSize(new Dimension(980, 520));
        pack();
        setLocationRelativeTo(owner);
    }
 
    private static String metaText(CsvModalParameters params) {
        String delim = params.blockFormat() ? "block" : printableDelimiter(params.delimiter());
        return "Encoding: " + params.charset().name()
                + " | Delimiter: " + delim
                + " | Rows: " + params.rowCount()
                + " | Columns: " + params.columnCount();
    }
 
    private static String printableDelimiter(char d) {
        if (d == '\t') {
            return "TAB";
        }
        if (d == '\n') {
            return "LF";
        }
        return String.valueOf(d);
    }
 
    private static final class FieldsModel extends AbstractTableModel {
        private final List<CsvModalParameters.Field> fields;
        private final String[] cols = new String[]{"CSV Header", "Key", "Type", "Display Format", "Validation Rules"};
 
        private FieldsModel(List<CsvModalParameters.Field> fields) {
            this.fields = fields == null ? List.of() : fields;
        }
 
        @Override
        public int getRowCount() {
            return fields.size();
        }
 
        @Override
        public int getColumnCount() {
            return cols.length;
        }
 
        @Override
        public String getColumnName(int column) {
            return cols[column];
        }
 
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CsvModalParameters.Field f = fields.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> f.csvHeader();
                case 1 -> f.key();
                case 2 -> f.type().name();
                case 3 -> f.displayFormat();
                case 4 -> String.join(", ", f.validationRules());
                default -> "";
            };
        }
 
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
