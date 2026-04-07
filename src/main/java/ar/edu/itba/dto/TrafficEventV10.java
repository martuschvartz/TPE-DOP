package ar.edu.itba.dto;

import java.time.LocalDateTime;

public record TrafficEventV10(
        String id,
        LocalDateTime timestamp, // ex "2026-03-04T10:00:00Z"
        Double spd,
        Integer lne
) implements EventDto {
}
