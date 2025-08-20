package es.elzhart.betfeed.queue;

import es.elzhart.betfeed.model.CommonEvent;

public interface EventPublisher {
    void publish(CommonEvent event);
}
