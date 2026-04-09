import ar.edu.itba.domain.*;
import ar.edu.itba.processor.EventProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventProcessorTest {

    private final LocalDateTime now = LocalDateTime.now();


    @Test
    void testProcessSensorEvents_FiltersOutNullsAndInvalidData(@TempDir Path tempDir) throws Exception {
        String jsonContent = """
                [
                    {
                        "id": "1", "timestamp": "2026-03-04T10:00:00Z", "SCHEMA_VER": "1.0",
                        "TYPE": "TRF", "PAYLOAD": { "SPD": 65.5, "LNE": 1 }
                    },
                    {
                        "id": "2", "timestamp": "2026-03-04T10:05:00Z", "SCHEMA_VER": "1.0",
                        "TYPE": "WTH", "PAYLOAD": { "T": 95.0, "H": null }
                    },
                    {
                        "id": "3", "timestamp": "2026-03-04T10:10:00Z", "unknown_schema": "3.0"
                    },
                    {
                        "id": "4", "timestamp": "2026-03-04T10:10:00Z", "schemaVersion": "2.0",
                        "eventType": "WEATHER", "data": { "temperature": 15.0, "humidity": 120.0 }
                    }
                ]
                """;

        File tempFile = tempDir.resolve("test_input.json").toFile();
        Files.writeString(tempFile.toPath(), jsonContent);

        List<Event> result = EventProcessor.processSensorEvents(tempFile.getAbsolutePath());

        assertEquals(2, result.size());
        assertFalse(result.contains(null));
    }

    @Test
    void testProcessSensorEvents_FileNotFound_ReturnsEmptyList() {
        List<Event> result = EventProcessor.processSensorEvents("non_existent_file.json");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testObtainResults_ExhaustiveMetrics() {
        List<Event> events = Arrays.asList(
                new TrafficEvent("1", now, "1.0", 100.0, "1"),
                new TrafficEvent("2", now, "1.0", 150.0, "1"),
                new TrafficEvent("3", now, "1.5", 151.0, "2"),

                new WeatherEvent("4", now, "2.0", 45.0, 50.0),
                new WeatherEvent("5", now, "2.0", 46.0, 50.0),
                new WeatherEvent("6", now, "1.5", 20.0, 100.0),
                new WeatherEvent("7", now, "1.5", 20.0, 101.0),

                new ReportEvent("8", now, "2.0", "POTHOLE", ReportSeverity.HIGH),
                new ReportEvent("9", now, "2.0", "TRAFFIC_LIGHT", ReportSeverity.LOW),
                new ReportEvent("10", now, "2.0", "TRAFFIC_LIGHT", ReportSeverity.HIGH),
                new ReportEvent("11", now, "1.0", "STREET_CLEAN", ReportSeverity.UNKNOWN)
        );

        MetricSummary summary = EventProcessor.obtainResults(events);

        assertEquals(11, summary.processedEventsAmount());
        assertEquals(133.666, summary.trafficSpeedAvg(), 0.001);

        assertEquals(5, summary.criticalProcessedEventsAmount());

        assertEquals(3, summary.schemaDistribution().get("1.0"));
        assertEquals(3, summary.schemaDistribution().get("1.5"));
        assertEquals(5, summary.schemaDistribution().get("2.0"));
    }

    @Test
    void testObtainResults_EmptyList_ReturnsZeroes() {
        MetricSummary summary = EventProcessor.obtainResults(List.of());

        assertEquals(0, summary.processedEventsAmount());
        assertEquals(0.0, summary.trafficSpeedAvg());
        assertEquals(0, summary.criticalProcessedEventsAmount());
        assertTrue(summary.schemaDistribution().isEmpty());
    }

    @Test
    void testObtainResults_NoTraffic_PreventsNaN() {
        List<Event> events = List.of(new WeatherEvent("1", now, "1.0", 20.0, 50.0));
        MetricSummary summary = EventProcessor.obtainResults(events);

        assertEquals(0.0, summary.trafficSpeedAvg());
    }
}