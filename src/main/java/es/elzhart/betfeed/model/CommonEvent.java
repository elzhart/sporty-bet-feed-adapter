package es.elzhart.betfeed.model;

import java.time.Instant;

public record CommonEvent(
        EventProvider provider,
        String eventId,
        EventType eventType,
        Odds odds,
        Result result,
        Instant timestamp
) {
}
