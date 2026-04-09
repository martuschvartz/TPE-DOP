package ar.edu.itba.domain;

public sealed interface Event permits TrafficEvent, ReportEvent, WeatherEvent {
    String schemaVersion();
}
