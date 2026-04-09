package ar.edu.itba.domain;

import java.time.LocalDateTime;

public record WeatherEvent(
        String id,
        LocalDateTime timestamp,
        String schemaVersion,
        double temperatureCelsius,
        double humidity
) implements Event {}
