package ar.edu.itba.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ReportEventV15(
        String id,
        LocalDateTime timestamp,
        String category,
        Map<String, String> attributes
) implements EventDto {
}