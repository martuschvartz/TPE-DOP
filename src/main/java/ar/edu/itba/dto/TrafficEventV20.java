package ar.edu.itba.dto;

import java.time.LocalDateTime;

public record TrafficEventV20(
        String id,
        LocalDateTime timestamp,
        Double speedKmh,
        Integer lane
) implements EventDto {
}
