package ar.edu.itba.processor;


import ar.edu.itba.domain.*;
import ar.edu.itba.dto.DtoMapper;
import ar.edu.itba.dto.EventDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class EventProcessor {
    private static final Logger logger = LogManager.getLogger(EventProcessor.class);

    public EventProcessor(){}


    /**
     * Processes heterogeneous sensor events for traffic, report and weather types
     * @param fileName type String, name of the json file
     * @return List<Event> on success, empty list on failure
     */
    public static List<Event> processSensorEvents(String fileName){
        List<EventDto> rawEvents;

        try {
            rawEvents = new ObjectMapper().readValue(
                    new File(fileName),
                    new TypeReference<>() {}
            );
        } catch (IOException ioException){
            logger.error("Error reading values from json file: ", ioException);
            return List.of();
        }

        return rawEvents.stream()
                .filter(Objects::nonNull)
                .map(DtoMapper::toDomainEvent)
                .toList();
    }

    /**
     * Based on the events processed, outputs a summary of metrics based
     * on the data consumed by the sensors
     * @param processedEvents type List<Event>, list of processed events
     * @return MetricSummary
     */
    public static MetricSummary obtainResults(
            List<Event> processedEvents
    ){
        int processedEventsAmount = 0;
        double speedSum = 0.0;
        int trafficEventsAmount = 0;
        int criticalEventsAmount = 0;
        Map<String, Integer> distributionMap = new HashMap<>();

        for(Event event: processedEvents){

            String version = event.schemaVersion();
            distributionMap.put(version, distributionMap.getOrDefault(version, 0) + 1);

            switch (event){
                case TrafficEvent trafficEvent -> {
                    speedSum += trafficEvent.speedKmh();
                    trafficEventsAmount++;
                    processedEventsAmount++;

                    if(trafficEvent.speedKmh() > 150.0){
                        criticalEventsAmount++;
                    }
                }

                case WeatherEvent weatherEvent -> {
                    processedEventsAmount++;
                    if(weatherEvent.temperatureCelsius() > 45.0 || weatherEvent.humidity() > 100.0){
                        criticalEventsAmount++;
                    }
                }

                case ReportEvent reportEvent -> {
                    processedEventsAmount++;

                    String category = reportEvent.category().toUpperCase();

                    if (category.equals("POTHOLE") || reportEvent.severity() == ReportSeverity.HIGH) {
                        criticalEventsAmount++;
                    }
                }
            }
        }

        return new MetricSummary(
                processedEventsAmount,
                trafficEventsAmount > 0 ? speedSum/trafficEventsAmount : 0,
                criticalEventsAmount,
                distributionMap
                );
    }

}
