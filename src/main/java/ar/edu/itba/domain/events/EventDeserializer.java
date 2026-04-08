package ar.edu.itba.domain.events;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventDeserializer extends JsonDeserializer<Event> {
    private static final Logger logger = LogManager.getLogger(EventDeserializer.class);

    @Override
    public Event deserialize(JsonParser jsonParser, DeserializationContext deserializationContext){
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            if (node == null) {
                return null;
            }

            if (node.has("SCHEMA_VER")) {
                return parseV10(node);
            } else if (node.has("version")) {
                return parseV15(node);
            } else if (node.has("schemaVersion")) {
                return parseV20(node);
            }
            // TODO Maybe implement an UnknownEventDto (?
            throw new IllegalStateException("Unknown version schema");

        } catch (Exception e){
            logger.error("Could not parse input json :{0}", e);
        }

        return null;
    }


    /* ------------------------------- PARSERS ------------------------------- */

    /* All parsers ignore invalid sensor input, by returning null in that case */

    /* ----------------------------------------------------------------------- */

    private Event parseV10(JsonNode node) {
        String SCHEMA_VERSION_10 = "1.0";
        String id = node.get("id").asText();
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String type = node.get("TYPE").asText();
        JsonNode payload = node.get("PAYLOAD");

        return switch (type) {
            case "TRF" -> {
                if (!payload.hasNonNull("SPD") || !payload.hasNonNull("LNE")) {
                    yield null;
                }
                yield new TrafficEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_10,
                        payload.get("SPD").asDouble(),
                        payload.get("LNE").asText()
                );
            }
            case "WTH" -> {
                if(!payload.hasNonNull("T") || !payload.hasNonNull("H")){
                    yield null;
                }

                yield new WeatherEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_10,
                        (payload.get("T").asDouble() - 32.0) * 5.0 / 9.0,
                        payload.get("H").asDouble()
                );
            }

            case "REPORT" -> {
                if(!payload.hasNonNull("CAT")){
                    yield null;
                }

                yield new ReportEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_10,
                        payload.get("CAT").asText(),
                        extractMap(payload, List.of("CAT"))
                );
            }
            default -> throw new IllegalArgumentException("Unknown v1.0 TYPE: " + type);
        };
    }

    private Event parseV15(JsonNode node) {
        String SCHEMA_VERSION_15 = "1.5";
        String id = node.get("id").asText();
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String kind = node.get("kind").asText();

        return switch (kind) {
            case "traffic" -> {
                if (!node.hasNonNull("velocity") || !node.hasNonNull("attributes") || !node.get("attributes").hasNonNull("lane_id")) {
                    yield null;
                }
                JsonNode attributes = node.get("attributes");
                yield new TrafficEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_15,
                        node.get("velocity").asDouble(),
                        attributes.get("lane_id").asText()
                );
            }
            case "weather" -> {
                if(!node.hasNonNull("temp_c") || !node.hasNonNull("attributes") || !node.get("attributes").hasNonNull("HUMIDITY")){
                    yield null;
                }
                JsonNode attributes = node.get("attributes");
                yield new WeatherEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_15,
                        node.get("temp_c").asDouble(),
                        attributes.get("HUMIDITY").asDouble()
                );
            }

            case "report" -> {
                if(!node.hasNonNull("category")){
                    yield null;
                }

                yield new ReportEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_15,
                        node.get("category").asText(),
                        extractMap(node.get("attributes"), List.of())
                );
            }
            default -> throw new IllegalArgumentException("Unknown v1.5 kind: " + kind);
        };
    }

    private Event parseV20(JsonNode node) {
        String SCHEMA_VERSION_20 = "2.0";
        String id = node.get("id").asText();
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String eventType = node.get("eventType").asText();
        JsonNode data = node.get("data");

        return switch (eventType) {
            case "TRAFFIC" -> {
                if(!data.hasNonNull("speedKmh") || !data.hasNonNull("lane")){
                    yield null;
                }

                yield new TrafficEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_20,
                        data.get("speedKmh").asDouble(),
                        data.get("lane").asText()
                );
            }
            case "WEATHER" -> {
                if(!data.hasNonNull("temperature") || !data.hasNonNull("humidity")){
                    yield null;
                }

                yield new WeatherEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_20,
                        data.get("temperature").asDouble(),
                        data.get("humidity").asDouble()
                );
            }
            case "REPORT" -> {
                if(!data.hasNonNull("category")){
                    yield null;
                }

                yield new ReportEvent(
                        id,
                        timestamp,
                        SCHEMA_VERSION_20,
                        data.get("category").asText(),
                        extractMap(data, List.of("category"))
                );
            }
            default -> throw new IllegalArgumentException("Unknown v2.0 eventType: " + eventType);
        };
    }


    private Map<String, String> extractMap(JsonNode container, List<String> keysToSkip) {
        Map<String, String> map = new HashMap<>();
        if (container == null || !container.isObject()) {
            return map;
        }

        for(Map.Entry<String, JsonNode> entry: container.properties()){
            String key = entry.getKey();

            if(!keysToSkip.contains(key) && !entry.getValue().isNull()){
                map.put(key, entry.getValue().asText());
            }
        }

        return map;
    }
}