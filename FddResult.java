public record FddResult(double[] freqHz, double[][] singularValuesDb, String[] channelLabels, double[][] modeShape) {
    public int lines() {
        return singularValuesDb == null ? 0 : singularValuesDb.length;
    }
}

