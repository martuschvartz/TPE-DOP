package ar.edu.itba.domain;

import java.time.LocalDateTime;

public record TrafficEvent(
        String id,
        LocalDateTime timestamp,
        String schemaVersion,
        double speedKmh,
        String lane
) implements Event {}
