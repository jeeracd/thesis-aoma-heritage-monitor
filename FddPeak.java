public record FddPeak(
        int peakIndex,
        int binIndex,
        double frequencyHz,
        double svd1Db,
        double bandwidthHz,
        double dampingRatioPercent
) {
}

