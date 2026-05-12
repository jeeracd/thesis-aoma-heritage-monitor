import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.prefs.Preferences;

public final class NativeFilePicker {
    private static final Preferences PREFS = Preferences.userRoot().node("aoma-heritage-monitor");
    private static final String KEY_LAST_DIR_CSV = "lastDirCsv";

    private NativeFilePicker() {}

    public static File pickCsvFile(Frame owner, String title) {
        String lastDir = PREFS.get(KEY_LAST_DIR_CSV, System.getProperty("user.home"));

        FileDialog dialog = new FileDialog(owner, title == null ? "Select CSV File" : title, FileDialog.LOAD);
        dialog.setDirectory(lastDir);
        dialog.setFile("*.csv");
        dialog.setFilenameFilter((dir, name) -> name != null && name.toLowerCase().endsWith(".csv"));
        dialog.setVisible(true);

        String file = dialog.getFile();
        String dir = dialog.getDirectory();
        if (file == null || dir == null) {
            return null;
        }

        PREFS.put(KEY_LAST_DIR_CSV, dir);
        return new File(dir, file);
    }
}

