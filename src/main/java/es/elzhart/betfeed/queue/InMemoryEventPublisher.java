package es.elzhart.betfeed.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import es.elzhart.betfeed.model.CommonEvent;

@Component("inMemoryPublisher")
public class InMemoryEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryEventPublisher.class);

    private final Queue<CommonEvent> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void publish(CommonEvent event) {
        queue.add(event);
        log.info("Published event: eventId='{}', eventType='{}', queueSize={}",
                event.eventId(), event.eventType(), queue.size());
        if (log.isDebugEnabled()) log.debug("Event payload: {}", event);
    }

    public List<CommonEvent> snapshot() {
        return List.copyOf(queue);
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}