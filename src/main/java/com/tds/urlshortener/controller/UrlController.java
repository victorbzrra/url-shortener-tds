package com.tds.urlshortener.controller;

import com.tds.urlshortener.dto.UrlRequestDTO;
import com.tds.urlshortener.dto.UrlResponseDTO;
import com.tds.urlshortener.dto.UrlStatsDTO;
import com.tds.urlshortener.model.Url;
import com.tds.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlResponseDTO> createShortUrl(@RequestBody UrlRequestDTO request) {
        Url url = urlService.createShortUrl(request.getUrl());
        String shortUrl = "/" + url.getShortened();

        UrlResponseDTO response = new UrlResponseDTO(
                url.getOriginalUrl(),
                url.getShortened(),
                shortUrl
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortened}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortened) {
        Optional<Url> url = Optional.ofNullable(urlService.accessShortnedUrl(shortened)
                .orElseThrow(() -> new NoSuchElementException("Short URL not found")));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.get().getOriginalUrl()))
                .build();
    }

    @GetMapping
    public ResponseEntity<UrlStatsDTO> getStats(@PathVariable String shortened) {
        Map<String, Object> stats = urlService.getUrlStats(shortened);

        UrlStatsDTO response = new UrlStatsDTO(
                (Long) stats.get("total"),
                (Double) stats.get("averagePerDay"),
                (Map<LocalDate, Long>) stats.get("accessPerDay")
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
