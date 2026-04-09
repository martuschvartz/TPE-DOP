package ar.edu.itba.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ReportEventV10(
        String id,
        LocalDateTime timestamp,
        String CAT,
        Map<String, String> values
) implements EventDto {
}