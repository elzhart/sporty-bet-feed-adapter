package es.elzhart.betfeed.service;

import es.elzhart.betfeed.model.CommonEvent;

public interface Standardizer<T> {

    CommonEvent standardize(T msg);
}
