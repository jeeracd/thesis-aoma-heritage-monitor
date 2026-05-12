public record SpectrogramViewWindow(double timeStartSec, double timeEndSec, double freqStartHz, double freqEndHz) {
    public SpectrogramViewWindow {
        if (timeEndSec < timeStartSec) {
            double t = timeStartSec;
            timeStartSec = timeEndSec;
            timeEndSec = t;
        }
        if (freqEndHz < freqStartHz) {
            double f = freqStartHz;
            freqStartHz = freqEndHz;
            freqEndHz = f;
        }
    }

    public static SpectrogramViewWindow full(SpectrogramData data) {
        if (data == null) {
            return new SpectrogramViewWindow(0, 0, 0, 0);
        }
        return new SpectrogramViewWindow(0, Math.max(0, data.durationSec()), 0, Math.max(0, data.fsHz() / 2.0));
    }
}

