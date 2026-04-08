package ar.edu.itba.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EventDeserializer.class)
public sealed interface Event permits TrafficEvent, ReportEvent, WeatherEvent {
}
