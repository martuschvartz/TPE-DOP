import ar.edu.itba.domain.*;
import ar.edu.itba.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoMapperTest {

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void testNullDto() {
        assertNull(DtoMapper.toDomainEvent(null));
    }

    @Test
    void testMapTrafficV10() {
        Event result = DtoMapper.toDomainEvent(new TrafficEventV10("1", now, 60.0, 1));
        assertEquals("1.0", result.schemaVersion());
        assertEquals(60.0, ((TrafficEvent) result).speedKmh());
        assertEquals("1", ((TrafficEvent) result).lane());
    }

    @Test
    void testMapTrafficV15() {
        Event result = DtoMapper.toDomainEvent(new TrafficEventV15("1", now, 120.0, "L-2"));
        assertEquals("1.5", result.schemaVersion());
        assertEquals(120.0, ((TrafficEvent) result).speedKmh());
        assertEquals("L-2", ((TrafficEvent) result).lane());
    }

    @Test
    void testMapTrafficV20() {
        Event result = DtoMapper.toDomainEvent(new TrafficEventV20("1", now, 45.0, 3));
        assertEquals("2.0", result.schemaVersion());
        assertEquals(45.0, ((TrafficEvent) result).speedKmh());
        assertEquals("3", ((TrafficEvent) result).lane());
    }

    @Test
    void testMapWeatherV10_FahrenheitConversion() {
        Event result = DtoMapper.toDomainEvent(new WeatherEventV10("1", now, 50.0, 80.0));
        assertEquals("1.0", result.schemaVersion());
        assertEquals(10.0, ((WeatherEvent) result).temperatureCelsius());
        assertEquals(80.0, ((WeatherEvent) result).humidity());
    }

    @Test
    void testMapWeatherV15() {
        Event result = DtoMapper.toDomainEvent(new WeatherEventV15("1", now, 32.5, 85.0));
        assertEquals("1.5", result.schemaVersion());
        assertEquals(32.5, ((WeatherEvent) result).temperatureCelsius());
    }

    @Test
    void testMapWeatherV20() {
        Event result = DtoMapper.toDomainEvent(new WeatherEventV20("1", now, 15.0, 120.0));
        assertEquals("2.0", result.schemaVersion());
        assertEquals(15.0, ((WeatherEvent) result).temperatureCelsius());
    }

    @Test
    void testMapReportV10_ExtractsSeverityFromDescription() {
        Event result = DtoMapper.toDomainEvent(new ReportEventV10("1", now, "pothole", Map.of("DESC", "Big hole in avenue")));
        assertEquals("1.0", result.schemaVersion());
        assertEquals("pothole", ((ReportEvent) result).category());
        assertEquals(ReportSeverity.HIGH, ((ReportEvent) result).severity()); // Typed assertion!
    }

    @Test
    void testMapReportV15_ExtractsSeverityFromAttributes() {
        Event result = DtoMapper.toDomainEvent(new ReportEventV15("1", now, "Pothole", Map.of("severity", "High")));
        assertEquals("1.5", result.schemaVersion());
        assertEquals("Pothole", ((ReportEvent) result).category());
        assertEquals(ReportSeverity.HIGH, ((ReportEvent) result).severity()); // Typed assertion!
    }

    @Test
    void testMapReportV20_ExtractsSeverityFromStatus() {
        Event result = DtoMapper.toDomainEvent(new ReportEventV20("1", now, "TRAFFIC_LIGHT", Map.of("status", "BROKEN")));
        assertEquals("2.0", result.schemaVersion());
        assertEquals("TRAFFIC_LIGHT", ((ReportEvent) result).category());
        assertEquals(ReportSeverity.HIGH, ((ReportEvent) result).severity()); // Typed assertion!
    }
}