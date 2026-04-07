package ar.edu.itba.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EventDeserializer.class)
public sealed interface EventDto permits
        TrafficEventV10,
        TrafficEventV15,
        TrafficEventV20,
        ReportEventV10,
        ReportEventV15,
        ReportEventV20,
        WeatherEventV10,
        WeatherEventV15,
        WeatherEventV20 {
}
