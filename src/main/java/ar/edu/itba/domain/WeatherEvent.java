package ar.edu.itba.domain;

import java.time.LocalDateTime;

public record WeatherEvent(
        String id,
        LocalDateTime timestamp,
        String schemaVersion,
        Double temperatureCelsius,
        Double humidity
) implements Event {}
