package ar.edu.itba.dto;

import java.time.LocalDateTime;

public record WeatherEventV15(
        String id,
        LocalDateTime timestamp,
        Double temp_c,
        Double HUMIDITY
) implements EventDto {
}
