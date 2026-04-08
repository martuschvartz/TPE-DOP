import ar.edu.itba.domain.events.Event;
import ar.edu.itba.domain.EventProcessor;
import ar.edu.itba.domain.MetricSummary;

void main(){
    IO.println("=== CityTyci Data Refinery ===");
    IO.println();

    String filename = "src/main/resources/example_input.json...";
    IO.println("...processing events from: %s".formatted("src/main/resources/example_input.json"));
    IO.println("...please stand by...");
    IO.println("...");
    IO.println("...");

    EventProcessor eventProcessor = new EventProcessor();

    List<Event> events = eventProcessor.processSensorEvents("src/main/resources/example_input.json");
    IO.println("|---------|");
    IO.println("| Results |");
    IO.println("|---------|");
    IO.println();
    MetricSummary metrics = eventProcessor.obtainResults(events);

    IO.println("Total events processed: %d".formatted(metrics.processedEventsAmount()));
    IO.println("Average Speed: %.3f".formatted(metrics.trafficSpeedAvg()));
    IO.println("Total CRITICAL events processed: %d".formatted(metrics.criticalProcessedEventsAmount()));
}