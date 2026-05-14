import javax.swing.SwingUtilities;
 
public final class FddPlotViewerTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(FddPlotViewerTest::run);
        System.out.println("ALL TESTS PASSED");
    }
 
    private static void run() {
        FddPlotViewer v = new FddPlotViewer();
 
        v.getMinHzFieldForTesting().setText("10");
        v.getMaxHzFieldForTesting().setText("5");
        v.getApplyBtnForTesting().doClick();
        assertTrue(v.getMinHzForTesting() == null && v.getMaxHzForTesting() == null, "invalid range should not apply");
 
        v.getMinHzFieldForTesting().setText("0.1");
        v.getMaxHzFieldForTesting().setText("1000");
        v.getApplyBtnForTesting().doClick();
        assertTrue(Math.abs(v.getMinHzForTesting() - 0.1) < 1e-9, "min should apply");
        assertTrue(Math.abs(v.getMaxHzForTesting() - 1000.0) < 1e-9, "max should apply");
    }
 
    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

