package ar.edu.itba.dto;

import ar.edu.itba.domain.Event;
import ar.edu.itba.domain.ReportEvent;
import ar.edu.itba.domain.TrafficEvent;
import ar.edu.itba.domain.WeatherEvent;

public class DtoMapper {
    private final static String SCHEMA_VERSION_10 = "1.0";
    private final static String SCHEMA_VERSION_15 = "1.5";
    private final static String SCHEMA_VERSION_20 = "2.0";

    public static Event toDomainEvent(EventDto eventDto){
            if(eventDto == null) return null;

            return switch(eventDto){
                case TrafficEventV10 t10 -> new TrafficEvent(
                            t10.id(),
                            t10.timestamp(),
                            SCHEMA_VERSION_10,
                            t10.spd(),
                            t10.lne().toString()
                    );

                case TrafficEventV15 t15 -> new TrafficEvent(
                        t15.id(),
                        t15.timestamp(),
                        SCHEMA_VERSION_15,
                        t15.velocity(),
                        t15.lane_id());

                case TrafficEventV20 t20 -> new TrafficEvent(
                        t20.id(),
                        t20.timestamp(),
                        SCHEMA_VERSION_20,
                        t20.speedKmh(),
                        t20.lane().toString()
                        );

                case WeatherEventV10 w10 -> new WeatherEvent(
                        w10.id(),
                        w10.timestamp(),
                        SCHEMA_VERSION_10,
                        (w10.T() - 32.0) * 5.0 / 9.0,
                        w10.H()
                );

                case WeatherEventV15 w15 -> new WeatherEvent(
                        w15.id(),
                        w15.timestamp(),
                        SCHEMA_VERSION_15,
                        w15.temp_c(),
                        w15.HUMIDITY()
                );

                case WeatherEventV20 w20 -> new WeatherEvent(
                        w20.id(),
                        w20.timestamp(),
                        SCHEMA_VERSION_20,
                        w20.temperature(),
                        w20.humidity()
                );


                case ReportEventV10 r10 -> new ReportEvent(
                        r10.id(),
                        r10.timestamp(),
                        SCHEMA_VERSION_10,
                        r10.CAT(),
                        r10.values()
                );

                case ReportEventV15 r15 -> new ReportEvent(
                        r15.id(),
                        r15.timestamp(),
                        SCHEMA_VERSION_15,
                        r15.category(),
                        r15.attributes()
                );

                case ReportEventV20 r20 -> new ReportEvent(
                        r20.id(),
                        r20.timestamp(),
                        SCHEMA_VERSION_20,
                        r20.category(),
                        r20.data()
                );

            };
    }
}
