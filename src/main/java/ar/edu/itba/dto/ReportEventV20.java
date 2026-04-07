package ar.edu.itba.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ReportEventV20(
        String id,
        LocalDateTime timestamp,
        String category,
        Map<String, String> data
) implements EventDto {
}
