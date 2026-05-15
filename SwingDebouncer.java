import javax.swing.*;
import java.awt.event.ActionListener;

public final class SwingDebouncer {
    private final Timer timer;

    public SwingDebouncer(int delayMs, Runnable task) {
        ActionListener a = e -> {
            if (task != null) {
                task.run();
            }
        };
        timer = new Timer(Math.max(0, delayMs), a);
        timer.setRepeats(false);
    }

    public void call() {
        if (SwingUtilities.isEventDispatchThread()) {
            timer.restart();
        } else {
            SwingUtilities.invokeLater(timer::restart);
        }
    }

    public void stop() {
        timer.stop();
    }
}

