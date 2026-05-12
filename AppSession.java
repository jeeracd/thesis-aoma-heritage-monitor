import java.io.File;

public final class AppSession {
    private static volatile File lastUploadedCsv;

    private AppSession() {}

    public static void setLastUploadedCsv(File file) {
        lastUploadedCsv = file;
    }

    public static File getLastUploadedCsv() {
        return lastUploadedCsv;
    }
}

