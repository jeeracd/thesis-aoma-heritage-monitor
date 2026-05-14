import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class AppSessionCsvListenerTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Runnable remove = AppSession.addLastUploadedCsvListener(latch::countDown);

        long before = AppSession.getLastUploadedCsvSequence();
        File f = File.createTempFile("aoma-test", ".csv");
        f.deleteOnExit();

        AppSession.setLastUploadedCsv(f);
        long after = AppSession.getLastUploadedCsvSequence();
        assert after == before + 1 : "sequence must increment";

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assert ok : "listener must be notified";

        long runBefore = AppSession.getLastPyOma2RunSequence();
        AppSession.markPyOma2RunStartedForCurrentCsv();
        long runAfter = AppSession.getLastPyOma2RunSequence();
        assert runAfter >= runBefore : "run sequence must not decrease";
        assert runAfter == AppSession.getLastUploadedCsvSequence() : "run sequence must match current csv sequence";

        remove.run();
        System.out.println("ok");
    }
}

