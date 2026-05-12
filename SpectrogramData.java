public record SpectrogramData(
        double[][] db,
        int frequencyBins,
        int timeFrames,
        int nfft,
        int winSize,
        int hopSize,
        double fsHz,
        double durationSec,
        double vminDb,
        double vmaxDb
) {
    public SpectrogramData {
        if (db == null) {
            throw new IllegalArgumentException("db is required");
        }
        if (frequencyBins <= 0 || timeFrames <= 0) {
            throw new IllegalArgumentException("invalid spectrogram dimensions");
        }
        if (db.length != frequencyBins) {
            throw new IllegalArgumentException("db rows must equal frequencyBins");
        }
        for (double[] row : db) {
            if (row == null || row.length != timeFrames) {
                throw new IllegalArgumentException("each db row must have timeFrames columns");
            }
        }
    }
}

