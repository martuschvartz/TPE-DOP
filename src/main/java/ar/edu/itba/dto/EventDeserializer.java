package ar.edu.itba.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventDeserializer extends JsonDeserializer<EventDto> {
    private static final Logger logger = LogManager.getLogger(EventDeserializer.class);

    @Override
    public EventDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext){
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

    private EventDto parseV10(JsonNode node) {
        String id = node.get("id").asText();
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String type = node.get("TYPE").asText();
        JsonNode payload = node.get("PAYLOAD");

        return switch (type) {
            case "TRF" -> new TrafficEventV10(
                    id,
                    timestamp,
                    payload.hasNonNull("SPD") ? payload.get("SPD").asDouble() : null,
                    payload.hasNonNull("LNE") ? payload.get("LNE").asInt() : null
            );
            case "WTH" -> new WeatherEventV10(
                    id,
                    timestamp,
                    payload.hasNonNull("T") ? payload.get("T").asDouble() : null,
                    payload.hasNonNull("H") ? payload.get("H").asDouble() : null
            );
            case "REPORT" -> {
                String cat = payload.hasNonNull("CAT") ? payload.get("CAT").asText() : "UNKNOWN";
                yield new ReportEventV10(id, timestamp, cat, extractMap(payload, "CAT"));
            }
            default -> throw new IllegalArgumentException("Unknown v1.0 TYPE: " + type);
        };
    }

    private EventDto parseV15(JsonNode node) {
        String id = node.get("id").asText();
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String kind = node.get("kind").asText();

        return switch (kind) {
            case "traffic" -> {
                JsonNode attributes = node.get("attributes");
                yield new TrafficEventV15(
                        id,
                        timestamp,
                        node.hasNonNull("velocity") ? node.get("velocity").asDouble() : null,
                        attributes != null && attributes.hasNonNull("lane_id") ? attributes.get("lane_id").asText() : null
                );
            }
            case "weather" -> {
                JsonNode attributes = node.get("attributes");
                yield new WeatherEventV15(
                        id,
                        timestamp,
                        node.hasNonNull("temp_c") ? node.get("temp_c").asDouble() : null,
                        attributes != null && attributes.hasNonNull("HUMIDITY") ? attributes.get("HUMIDITY").asDouble() : null
                );
            }
            case "report" -> {
                String category = node.hasNonNull("category") ? node.get("category").asText() : "UNKNOWN";
                yield new ReportEventV15(id, timestamp, category, extractMap(node.get("attributes")));
            }
            default -> throw new IllegalArgumentException("Unknown v1.5 kind: " + kind);
        };
    }

    private EventDto parseV20(JsonNode node) {
        String id = node.get("id").asText();
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String eventType = node.get("eventType").asText();
        JsonNode data = node.get("data");

        return switch (eventType) {
            case "TRAFFIC" -> new TrafficEventV20(
                    id,
                    timestamp,
                    data.hasNonNull("speedKmh") ? data.get("speedKmh").asDouble() : null,
                    data.hasNonNull("lane") ? data.get("lane").asInt() : null
            );
            case "WEATHER" -> new WeatherEventV20(
                    id,
                    timestamp,
                    data.hasNonNull("temperature") ? data.get("temperature").asDouble() : null,
                    data.hasNonNull("humidity") ? data.get("humidity").asDouble() : null
            );
            case "REPORT" -> {
                String category = data.hasNonNull("category") ? data.get("category").asText() : "UNKNOWN";
                yield new ReportEventV20(id, timestamp, category, extractMap(data, "category"));
            }
            default -> throw new IllegalArgumentException("Unknown v2.0 eventType: " + eventType);
        };
    }


    private Map<String, String> extractMap(JsonNode container, String... keysToSkip) {
        Map<String, String> map = new HashMap<>();
        if (container == null || !container.isObject()) return map;

        Iterator<Map.Entry<String, JsonNode>> fields = container.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();

            boolean skip = false;
            for (String k : keysToSkip) {
                if (key.equals(k)) { skip = true; break; }
            }

            if (!skip && !field.getValue().isNull()) {
                map.put(key, field.getValue().asText());
            }
        }
        return map;
    }
}