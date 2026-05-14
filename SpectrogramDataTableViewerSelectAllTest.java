import javax.swing.SwingUtilities;
 
public final class SpectrogramDataTableViewerSelectAllTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(SpectrogramDataTableViewerSelectAllTest::run);
        System.out.println("ALL TESTS PASSED");
    }
 
    private static void run() {
        SpectrogramDataTableViewer v = new SpectrogramDataTableViewer();
        v.setSpectrogram(smallData());
 
        int total = v.getTableForTesting().getRowCount();
        assertTrue(total > 0, "expected table rows");
 
        v.getSelectAllForTesting().doClick();
        assertTrue(v.getTableForTesting().getSelectedRowCount() == total, "select all should select all rows");
 
        v.getSelectAllForTesting().doClick();
        assertTrue(v.getTableForTesting().getSelectedRowCount() == 0, "deselect all should clear selection");
 
        v.getTableForTesting().setRowSelectionInterval(0, 0);
        assertTrue(!v.getSelectAllForTesting().isSelected(), "select all should be off when not all rows are selected");
 
        v.getSelectAllForTesting().doClick();
        assertTrue(v.getTableForTesting().getSelectedRowCount() == total, "select all should reselect all rows");
    }
 
    private static SpectrogramData smallData() {
        int bins = 4;
        int frames = 5;
        double[][] db = new double[bins][frames];
        for (int k = 0; k < bins; k++) {
            for (int f = 0; f < frames; f++) {
                db[k][f] = k * 10 + f;
            }
        }
        return new SpectrogramData(db, bins, frames, 8, 8, 2, 100.0, 4.0, -100.0, 0.0);
    }
 
    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

