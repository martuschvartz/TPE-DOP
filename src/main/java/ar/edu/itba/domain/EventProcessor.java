package ar.edu.itba.domain;


import ar.edu.itba.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// V0 for event processing
public class EventProcessor {
    private final List<ReportEvent> reportEvents;
    private final List<WeatherEvent> weatherEvents;
    private final List<TrafficEvent> trafficEvents;

    private static final Logger logger = LogManager.getLogger(EventProcessor.class);

    public EventProcessor(){
        this.reportEvents = new ArrayList<>();
        this.weatherEvents = new ArrayList<>();
        this.trafficEvents = new ArrayList<>();
    }


    public void processEvents(){
        List<EventDto> events;
        try {
            events = new ObjectMapper().readValue(
                    new File("src/main/resources/example_input.json"),
                    new TypeReference<>() {}
            );
        } catch (IOException ioException){
            logger.error("Error reading values from json file", ioException);
            return;
        }

        for(EventDto event : events){
            if(event == null) continue;

            switch(event){
                case TrafficEventV10 t10 -> {
                    Double speedKmh = t10.spd() != null ? t10.spd() * 1.60934 : null;
                    String lane = t10.lne() != null ? String.valueOf(t10.lne()) : "UNKNOWN";
                    TrafficEvent trafficEvent = new TrafficEvent(t10.id(), t10.timestamp(), "1.0", speedKmh, lane);
                    trafficEvents.add(trafficEvent);
                }
                case TrafficEventV15 t15 -> {
                    TrafficEvent trafficEvent = new TrafficEvent(t15.id(), t15.timestamp(), "1.5", t15.velocity(), t15.lane_id());
                    trafficEvents.add(trafficEvent);
                }
                case TrafficEventV20 t20 -> {
                    String lane = t20.lane() != null ? String.valueOf(t20.lane()) : "UNKNOWN";
                    TrafficEvent trafficEvent = new TrafficEvent(t20.id(), t20.timestamp(), "2.0", t20.speedKmh(), lane);
                    trafficEvents.add(trafficEvent);
                }


                case WeatherEventV10 w10 -> {
                    Double tempC = w10.T() != null ? (w10.T() - 32.0) * 5.0 / 9.0 : null;
                    WeatherEvent weatherEvent = new WeatherEvent(w10.id(), w10.timestamp(), "1.0", tempC, w10.H());
                    weatherEvents.add(weatherEvent);
                }
                case WeatherEventV15 w15 -> {
                    WeatherEvent weatherEvent = new WeatherEvent(w15.id(), w15.timestamp(), "1.5", w15.temp_c(), w15.HUMIDITY());
                    weatherEvents.add(weatherEvent);
                }
                case WeatherEventV20 w20 -> {
                    WeatherEvent weatherEvent = new WeatherEvent(w20.id(), w20.timestamp(), "2.0", w20.temperature(), w20.humidity());
                    weatherEvents.add(weatherEvent);

                }

                case ReportEventV10 r10 -> {
                    String category = r10.CAT() != null ? r10.CAT().toUpperCase() : "UNKNOWN";
                    ReportEvent reportEvent = new ReportEvent(r10.id(), r10.timestamp(), "1.0", category, r10.values());
                    reportEvents.add(reportEvent);
                }
                case ReportEventV15 r15 -> {
                    String category = r15.category() != null ? r15.category().toUpperCase() : "UNKNOWN";
                    ReportEvent reportEvent = new ReportEvent(r15.id(), r15.timestamp(), "1.5", category, r15.attributes());
                    reportEvents.add(reportEvent);
                }
                case ReportEventV20 r20 -> {
                    String category = r20.category() != null ? r20.category().toUpperCase() : "UNKNOWN";
                    ReportEvent reportEvent = new ReportEvent(r20.id(), r20.timestamp(), "2.0", category, r20.data());
                    reportEvents.add(reportEvent);
                }
            }
        }

        return;
    }

    public void printEventsSizes(){
        IO.println("Weather events amount: %d".formatted(weatherEvents.size()));
        IO.println("Traffic events amount: %d".formatted(trafficEvents.size()));
        IO.println("Report events amount: %d".formatted(reportEvents.size()));
    }
}
