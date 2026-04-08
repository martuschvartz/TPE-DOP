package ar.edu.itba.domain;


import ar.edu.itba.domain.events.Event;
import ar.edu.itba.domain.events.ReportEvent;
import ar.edu.itba.domain.events.TrafficEvent;
import ar.edu.itba.domain.events.WeatherEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class EventProcessor {
    private static final Logger logger = LogManager.getLogger(EventProcessor.class);

    public EventProcessor(){}


    /**
     * Processes heterogeneous sensor events for traffic, report and weather types
     * @param fileName type String, name of the json file
     * @return List<Event> on success, empty list on failure
     */
    public List<Event> processSensorEvents(String fileName){

        try {
            List<Event> events = new ObjectMapper().readValue(
                    new File(fileName),
                    new TypeReference<>() {}
            );
            return events;
        } catch (IOException ioException){
            logger.error("Error reading values from json file", ioException);
            return List.of();
        }
    }

    /**
     * Based on the events processed, outputs a summary of metrics based
     * on the data consumed by the sensors
     * @param processedEvents type List<Event>, list of processed events
     * @return MetricSummary
     */
    public MetricSummary obtainResults(
            List<Event> processedEvents
    ){
        int processedEventsAmount = 0;
        double speedSum = 0.0;
        int trafficEventsAmount = 0;
        int criticalEventsAmount = 0;

        for(Event event: processedEvents){
            // Ignores invalid events
            if(event == null){
                continue;
            }

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
                    if(reportEvent.category().equalsIgnoreCase("POTHOLE")){
                        criticalEventsAmount++;
                    }
                }
            }
        }

        return new MetricSummary(
                processedEventsAmount,
                speedSum/trafficEventsAmount,
                criticalEventsAmount
                );
    }
}
