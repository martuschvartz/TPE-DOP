package ar.edu.itba.domain;

import java.time.LocalDateTime;
import java.util.Map;

public record ReportEvent(
        String id,
        LocalDateTime timestamp,
        String schemaVersion,
        String category,
        ReportSeverity severity
) implements Event {}
