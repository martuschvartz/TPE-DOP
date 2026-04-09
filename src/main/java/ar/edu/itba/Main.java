import ar.edu.itba.domain.Event;
import ar.edu.itba.processor.EventProcessor;
import ar.edu.itba.domain.MetricSummary;

void main(){
    IO.println("=== CityTyci Data Refinery ===");
    IO.println();

    String filename = "src/main/resources/example_input.json";
    IO.println("...processing events from: %s".formatted(filename));
    IO.println("...please stand by...");
    IO.println("...");
    IO.println("...");


    List<Event> events = EventProcessor.processSensorEvents(filename);
    IO.println("|---------|");
    IO.println("| Results |");
    IO.println("|---------|");
    IO.println();
    MetricSummary metrics = EventProcessor.obtainResults(events);

    IO.println("Total events processed: %d".formatted(metrics.processedEventsAmount()));
    IO.println("Average Speed: %.3f".formatted(metrics.trafficSpeedAvg()));
    IO.println("Total CRITICAL events processed: %d".formatted(metrics.criticalProcessedEventsAmount()));
    IO.println("Schema Version Distribution:");
    metrics.schemaDistribution().forEach((version, count) -> {
        IO.println("  - Version " + version + ": " + count + " events");
    });
}