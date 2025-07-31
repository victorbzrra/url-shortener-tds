package com.tds.urlshortener.exception;
public class StatsUnavailableException extends RuntimeException {
    public StatsUnavailableException(String message) {
        super(message);
    }
}
