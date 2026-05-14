import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class FddDampingPanel extends JPanel {
    public interface PeakSelectionListener {
        void onPeakSelected(FddPeak peak);
    }

    private final JLabel status = new JLabel("");
    private final JButton exportBtn = new JButton("Export peaks...");
    private final JTable table;
    private final PeaksModel model = new PeaksModel();

    private volatile PeakSelectionListener listener;

    public FddDampingPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        status.setFont(UiControlMetrics.CONTROL_FONT);
        status.setForeground(Color.DARK_GRAY);

        exportBtn.setMnemonic('X');
        UiControlMetrics.applyControlFont(status, exportBtn);
        UiControlMetrics.setPreferredHeight(exportBtn, UiControlMetrics.CONTROL_HEIGHT);

        JPanel top = new JPanel(new BorderLayout(UiControlMetrics.HGAP, 0));
        top.setOpaque(false);
        UiControlMetrics.setRowMaxHeight(top);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, UiControlMetrics.HGAP, UiControlMetrics.VGAP));
        left.setOpaque(false);
        left.add(status);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, UiControlMetrics.HGAP, UiControlMetrics.VGAP));
        right.setOpaque(false);
        right.add(exportBtn);

        top.add(left, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        UiControlMetrics.applyControlFont(table.getTableHeader(), table);

        JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        wire();
        setPeaks(List.of());
    }

    public void setPeakSelectionListener(PeakSelectionListener listener) {
        this.listener = listener;
    }

    public void setPeaks(List<FddPeak> peaks) {
        model.setPeaks(peaks);
        if (peaks == null || peaks.isEmpty()) {
            status.setText("No peaks detected.");
        } else {
            status.setText("Detected peaks: " + peaks.size() + " (select one to view damping + mode shape)");
        }
        table.clearSelection();
    }

    public void selectPeakByBinIndex(int binIndex) {
        int row = model.findRowByBinIndex(binIndex);
        if (row >= 0) {
            table.getSelectionModel().setSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
        }
    }

    private void wire() {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int viewRow = table.getSelectedRow();
                if (viewRow < 0) {
                    return;
                }
                int modelRow = table.convertRowIndexToModel(viewRow);
                FddPeak p = model.getAt(modelRow);
                PeakSelectionListener l = listener;
                if (l != null && p != null) {
                    l.onPeakSelected(p);
                }
            }
        });

        exportBtn.addActionListener(e -> exportPeaks());
    }

    private void exportPeaks() {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame f ? f : null;
        File file = NativeFilePicker.pickSaveFile(owner, "Export Peaks", "oma_peaks.csv", ".csv");
        if (file == null) {
            Toast.show(w, "Export canceled", new Color(80, 80, 80), 1600);
            return;
        }
        try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
            out.println("peak,freq_hz,svd1_db,bandwidth_hz,damping_percent");
            for (FddPeak p : model.peaks) {
                out.print(p.peakIndex());
                out.print(',');
                out.print(p.frequencyHz());
                out.print(',');
                out.print(p.svd1Db());
                out.print(',');
                out.print(Double.isFinite(p.bandwidthHz()) ? p.bandwidthHz() : "");
                out.print(',');
                out.print(Double.isFinite(p.dampingRatioPercent()) ? p.dampingRatioPercent() : "");
                out.println();
            }
            Toast.show(w, "Peaks exported", new Color(0, 128, 0), 1600);
        } catch (Exception ex) {
            Toast.show(w, "Export failed", new Color(160, 40, 40), 2200);
        }
    }

    private static final class PeaksModel extends AbstractTableModel {
        private final String[] cols = new String[]{"Peak", "Freq (Hz)", "SVD1 (dB)", "Bandwidth (Hz)", "Damping (%)"};
        private List<FddPeak> peaks = List.of();

        void setPeaks(List<FddPeak> peaks) {
            this.peaks = peaks == null ? List.of() : new ArrayList<>(peaks);
            fireTableDataChanged();
        }

        FddPeak getAt(int row) {
            if (row < 0 || row >= peaks.size()) {
                return null;
            }
            return peaks.get(row);
        }

        int findRowByBinIndex(int binIndex) {
            for (int i = 0; i < peaks.size(); i++) {
                if (peaks.get(i).binIndex() == binIndex) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getRowCount() {
            return peaks.size();
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
            FddPeak p = peaks.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> p.peakIndex();
                case 1 -> formatHz(p.frequencyHz());
                case 2 -> formatDb(p.svd1Db());
                case 3 -> Double.isFinite(p.bandwidthHz()) ? formatHz(p.bandwidthHz()) : "";
                case 4 -> Double.isFinite(p.dampingRatioPercent()) ? String.format("%.2f", p.dampingRatioPercent()) : "";
                default -> "";
            };
        }

        private static String formatHz(double hz) {
            if (!Double.isFinite(hz)) {
                return "";
            }
            if (hz >= 100) {
                return String.format("%.0f", hz);
            }
            if (hz >= 10) {
                return String.format("%.1f", hz);
            }
            if (hz >= 1) {
                return String.format("%.2f", hz);
            }
            return String.format("%.3f", hz);
        }

        private static String formatDb(double db) {
            if (!Double.isFinite(db)) {
                return "";
            }
            return String.format("%.2f", db);
        }
    }
}

