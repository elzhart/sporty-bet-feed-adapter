package es.elzhart.betfeed.service;

import java.time.Instant;

import es.elzhart.betfeed.model.CommonEvent;
import es.elzhart.betfeed.model.EventProvider;
import es.elzhart.betfeed.model.EventType;
import es.elzhart.betfeed.model.Odds;
import es.elzhart.betfeed.model.Result;


public abstract class AbstractStandardizer<T> implements Standardizer<T> {

    protected abstract EventProvider provider();

    protected final CommonEvent build(String eventId, EventType eventType, Odds odds, Result result) {
        return new CommonEvent(provider(), eventId, eventType, odds, result, Instant.now());
    }
}
