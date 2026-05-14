import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class PyOma2ResultsPanelAutoRunTest {
    public static void main(String[] args) throws Exception {
        AtomicInteger calls = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        PyOma2ResultsPanel.Runner runner = (csv, out, fs) -> {
            calls.incrementAndGet();
            Properties p = new Properties();
            p.setProperty("status", "ok");
            latch.countDown();
            return new PyOma2Runner.RunResult(true, "ok", p);
        };

        File f = File.createTempFile("aoma-test", ".csv");
        f.deleteOnExit();

        AppSession.setLastUploadedCsv(f);

        SwingUtilities.invokeAndWait(() -> {
            PyOma2ResultsPanel panel = new PyOma2ResultsPanel(null, runner);
            panel.setSourceCsv(f);
        });

        boolean ok = latch.await(3, TimeUnit.SECONDS);
        assert ok : "runner should be called automatically";
        assert calls.get() == 1 : "runner should be called once";

        System.out.println("ok");
    }
}

