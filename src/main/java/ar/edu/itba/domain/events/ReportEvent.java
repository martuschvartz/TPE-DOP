package ar.edu.itba.domain.events;

import java.time.LocalDateTime;
import java.util.Map;

public record ReportEvent(
        String id,
        LocalDateTime timestamp,
        String schemaVersion,
        String category,
        Map<String, String> details
) implements Event {}
