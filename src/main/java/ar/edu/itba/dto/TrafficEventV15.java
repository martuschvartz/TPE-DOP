package ar.edu.itba.dto;

import java.time.LocalDateTime;

public record TrafficEventV15(
        String id,
        LocalDateTime timestamp,
        Double velocity,
        String lane_id
) implements EventDto {
}
