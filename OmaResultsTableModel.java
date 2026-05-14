import javax.swing.table.AbstractTableModel;
import java.util.List;

public final class OmaResultsTableModel extends AbstractTableModel {
    private static final String[] COLS = new String[] {
            "Mode",
            "Frequency (Hz)",
            "Damping Ratio",
            "MPC",
            "MPD",
            "Severity"
    };

    private List<OmaResultsModel.ModeRow> rows = List.of();

    public void setRows(List<OmaResultsModel.ModeRow> rows) {
        this.rows = rows == null ? List.of() : List.copyOf(rows);
        fireTableDataChanged();
    }

    public OmaResultsModel.ModeRow rowAt(int modelRow) {
        if (modelRow < 0 || modelRow >= rows.size()) {
            return null;
        }
        return rows.get(modelRow);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            case 5 -> String.class;
            default -> Double.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OmaResultsModel.ModeRow r = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.modeIndex();
            case 1 -> r.frequencyHz();
            case 2 -> r.dampingRatio();
            case 3 -> r.mpc();
            case 4 -> r.mpd();
            case 5 -> r.severity().name();
            default -> "";
        };
    }
}

