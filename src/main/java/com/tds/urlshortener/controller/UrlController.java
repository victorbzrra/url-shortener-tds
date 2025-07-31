package com.tds.urlshortener.controller;

import com.tds.urlshortener.dto.UrlRequestDTO;
import com.tds.urlshortener.dto.UrlResponseDTO;
import com.tds.urlshortener.dto.UrlStatsDTO;
import com.tds.urlshortener.model.Url;
import com.tds.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/urls")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @Operation(summary = "Shorten an original URL")
    @PostMapping
    public ResponseEntity<UrlResponseDTO> createShortUrl(@Parameter(description = "Original URL to be shortened")
                                                         @RequestBody UrlRequestDTO request) {
        Url url = urlService.createShortUrl(request.getUrl());
        String shortUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{shortened}")
                .buildAndExpand(url.getShortened())
                .toUriString();

        UrlResponseDTO response = new UrlResponseDTO(
                url.getOriginalUrl(),
                url.getShortened(),
                shortUrl
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Redirects to original URL",
            description = "O Swagger UI não segue redirecionamentos, então teste acessando a URL encurtada diretamente no navegador."
    )
    @GetMapping("/{shortened}")
    public ResponseEntity<Void> redirectToOriginalUrl(@Parameter(description = "Shortened URL code")
                                                      @PathVariable String shortened) {
        Optional<Url> url = Optional.ofNullable(urlService.accessShortnedUrl(shortened)
                .orElseThrow(() -> new NoSuchElementException("Short URL not found")));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.get().getOriginalUrl()))
                .build();
    }

    @Operation(summary = "URL access statistics")
    @GetMapping("/{shortened}/stats")
    public ResponseEntity<UrlStatsDTO> getStats(@Parameter(description = "Shortened URL code")
                                                @PathVariable String shortened) {
        Map<String, Object> stats = urlService.getUrlStats(shortened);

        UrlStatsDTO response = new UrlStatsDTO(
                (Long) stats.get("total"),
                (Double) stats.get("averagePerDay"),
                (Map<LocalDate, Long>) stats.get("accessPerDay")
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
