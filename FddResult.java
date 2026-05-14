public record FddResult(double[] freqHz, double[][] singularValuesDb) {
    public int lines() {
        return singularValuesDb == null ? 0 : singularValuesDb.length;
    }
}

