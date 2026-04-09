package ar.edu.itba.dto;

import java.time.LocalDateTime;

public record WeatherEventV20(
        String id,
        LocalDateTime timestamp,
        Double temperature,
        Double humidity
) implements EventDto {
}