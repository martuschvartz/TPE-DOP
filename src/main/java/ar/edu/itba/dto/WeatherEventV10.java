package ar.edu.itba.dto;

import java.time.LocalDateTime;

public record WeatherEventV10(
        String id,
        LocalDateTime timestamp,
        Double T,
        Double H
) implements EventDto {
}
