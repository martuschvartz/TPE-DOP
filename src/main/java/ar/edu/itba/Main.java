import ar.edu.itba.domain.EventProcessor;

void main(){
    EventProcessor eventProcessor = new EventProcessor();

    eventProcessor.processEvents();
    eventProcessor.printEventsSizes();
}