package com.tds.urlshortener.handler;

import com.tds.urlshortener.dto.ErrorResponseDTO;
import com.tds.urlshortener.exception.DuplicateUrlException;
import com.tds.urlshortener.exception.InvalidUrlException;
import com.tds.urlshortener.exception.StatsUnavailableException;
import com.tds.urlshortener.exception.UrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUrlNotFound(UrlNotFoundException ex, WebRequest request) {
        return buildError(ex, HttpStatus.NOT_FOUND, request, "Url Not Found");
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidUrl(InvalidUrlException ex, WebRequest request) {
        return buildError(ex, HttpStatus.BAD_REQUEST, request, "Invalid URL");
    }

    @ExceptionHandler(DuplicateUrlException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateUrl(DuplicateUrlException ex, WebRequest request) {
        return buildError(ex, HttpStatus.CONFLICT, request, "Duplicate URL");
    }

    @ExceptionHandler(StatsUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleStatsUnavailable(StatsUnavailableException ex, WebRequest request) {
        return buildError(ex, HttpStatus.SERVICE_UNAVAILABLE, request, "Statistics Unavailable");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR, request, "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponseDTO> buildError(Exception ex, HttpStatus status, WebRequest request, String errorMessage) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                errorMessage,
                ex.getMessage(),
                request.getDescription(false).replaceAll("uri=", "")
        );

        return new ResponseEntity<>(body, status);
    }
}
