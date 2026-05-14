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

    public static File pickSaveCsvFile(Frame owner, String title, String defaultFileName) {
        String lastDir = PREFS.get(KEY_LAST_DIR_CSV, System.getProperty("user.home"));

        FileDialog dialog = new FileDialog(owner, title == null ? "Save CSV File" : title, FileDialog.SAVE);
        dialog.setDirectory(lastDir);
        if (defaultFileName != null && !defaultFileName.isBlank()) {
            dialog.setFile(defaultFileName);
        } else {
            dialog.setFile("export.csv");
        }
        dialog.setFilenameFilter((dir, name) -> name != null && name.toLowerCase().endsWith(".csv"));
        dialog.setVisible(true);

        String file = dialog.getFile();
        String dir = dialog.getDirectory();
        if (file == null || dir == null) {
            return null;
        }

        PREFS.put(KEY_LAST_DIR_CSV, dir);
        if (!file.toLowerCase().endsWith(".csv")) {
            file = file + ".csv";
        }
        return new File(dir, file);
    }

    public static File pickSaveFile(Frame owner, String title, String defaultFileName, String requiredExtension) {
        String lastDir = PREFS.get(KEY_LAST_DIR_CSV, System.getProperty("user.home"));

        String ext = requiredExtension == null ? "" : requiredExtension.trim();
        if (!ext.isEmpty() && !ext.startsWith(".")) {
            ext = "." + ext;
        }

        FileDialog dialog = new FileDialog(owner, title == null ? "Save File" : title, FileDialog.SAVE);
        dialog.setDirectory(lastDir);
        if (defaultFileName != null && !defaultFileName.isBlank()) {
            dialog.setFile(defaultFileName);
        } else {
            dialog.setFile("export" + (ext.isEmpty() ? "" : ext));
        }
        if (!ext.isEmpty()) {
            String extLower = ext.toLowerCase();
            dialog.setFilenameFilter((dir, name) -> name != null && name.toLowerCase().endsWith(extLower));
        }
        dialog.setVisible(true);

        String file = dialog.getFile();
        String dir = dialog.getDirectory();
        if (file == null || dir == null) {
            return null;
        }

        PREFS.put(KEY_LAST_DIR_CSV, dir);
        if (!ext.isEmpty() && !file.toLowerCase().endsWith(ext.toLowerCase())) {
            file = file + ext;
        }
        return new File(dir, file);
    }
}

