import ar.edu.itba.domain.*;
import ar.edu.itba.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }


    @Test
    void testParseV10_Traffic_Success() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"SCHEMA_VER\": \"1.0\", \"TYPE\": \"TRF\", \"PAYLOAD\": { \"SPD\": 65.5, \"LNE\": 1 } }";
        assertInstanceOf(TrafficEventV10.class, mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV10_Traffic_MissingSpd_ReturnsNull() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"SCHEMA_VER\": \"1.0\", \"TYPE\": \"TRF\", \"PAYLOAD\": { \"LNE\": 1 } }";
        assertNull(mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV10_Weather_Success() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"SCHEMA_VER\": \"1.0\", \"TYPE\": \"WTH\", \"PAYLOAD\": { \"T\": 95.0, \"H\": 40.0 } }";
        assertInstanceOf(WeatherEventV10.class, mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV10_Report_Success() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"SCHEMA_VER\": \"1.0\", \"TYPE\": \"REPORT\", \"PAYLOAD\": { \"CAT\": \"pothole\", \"DESC\": \"Big hole\" } }";
        EventDto result = mapper.readValue(json, EventDto.class);
        assertInstanceOf(ReportEventV10.class, result);
        assertEquals("Big hole", ((ReportEventV10) result).values().get("DESC"));
    }


    @Test
    void testParseV15_Traffic_Success() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"version\": 1.5, \"kind\": \"traffic\", \"velocity\": 120.0, \"attributes\": { \"lane_id\": \"L-2\" } }";
        assertInstanceOf(TrafficEventV15.class, mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV15_Weather_MissingTempC_ReturnsNull() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"version\": 1.5, \"kind\": \"weather\", \"attributes\": { \"HUMIDITY\": 85.0 } }";
        assertNull(mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV15_Report_Success() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"version\": 1.5, \"kind\": \"report\", \"category\": \"Pothole\", \"attributes\": { \"severity\": \"High\" } }";
        assertInstanceOf(ReportEventV15.class, mapper.readValue(json, EventDto.class));
    }


    @Test
    void testParseV20_Traffic_Success() throws Exception {
        String json = "{ \"schemaVersion\": \"2.0\", \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"eventType\": \"TRAFFIC\", \"data\": { \"speedKmh\": 45.0, \"lane\": 3 } }";
        assertInstanceOf(TrafficEventV20.class, mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV20_Weather_Success() throws Exception {
        String json = "{ \"schemaVersion\": \"2.0\", \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"eventType\": \"WEATHER\", \"data\": { \"temperature\": 15.0, \"humidity\": 120.0 } }";
        assertInstanceOf(WeatherEventV20.class, mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParseV20_Report_MissingCategory_ReturnsNull() throws Exception {
        String json = "{ \"schemaVersion\": \"2.0\", \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"eventType\": \"REPORT\", \"data\": { \"status\": \"BROKEN\" } }";
        assertNull(mapper.readValue(json, EventDto.class));
    }


    @Test
    void testParse_MalformedJson_ReturnsNull() throws Exception {
        String json = "{ invalid_json: ";
        assertNull(mapper.readValue(json, EventDto.class));
    }

    @Test
    void testParse_UnknownSchema_ReturnsNull() throws Exception {
        String json = "{ \"id\": \"1\", \"timestamp\": \"2026-03-04T10:00:00Z\", \"unknown_schema\": \"3.0\" }";
        assertNull(mapper.readValue(json, EventDto.class));
    }
}