package ar.edu.itba.domain;

public record MetricSummary(
        int processedEventsAmount,
        double trafficSpeedAvg,
        int criticalProcessedEventsAmount
) {
}
