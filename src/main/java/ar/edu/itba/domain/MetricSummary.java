package ar.edu.itba.domain;

import java.util.Map;

public record MetricSummary(
        int processedEventsAmount,
        double trafficSpeedAvg,
        int criticalProcessedEventsAmount,
        Map<String, Integer> schemaDistribution
) {
}
