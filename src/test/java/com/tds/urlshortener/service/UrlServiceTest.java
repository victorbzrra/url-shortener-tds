package com.tds.urlshortener.service;

import com.tds.urlshortener.exception.DuplicateUrlException;
import com.tds.urlshortener.exception.InvalidUrlException;
import com.tds.urlshortener.exception.UrlNotFoundException;
import com.tds.urlshortener.model.Access;
import com.tds.urlshortener.model.Url;
import com.tds.urlshortener.repository.AccessRepository;
import com.tds.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private AccessRepository accessRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createShortUrl_success() {
        String originalUrl = "https://www.amazon.com.br";

        Url url = Url.builder()
                .id(UUID.randomUUID())
                .originalUrl(originalUrl)
                .build();

        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        Url result = urlService.createShortUrl(originalUrl);

        assertNotNull(result);
        assertEquals(originalUrl, result.getOriginalUrl());
    }

    @Test
    void createShortUrl_invalidUrl() {
        String originalUrl = "invalid-url";
        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(originalUrl));
    }

    @Test
    void createShortUrl_duplicateUrl() {
        String originalUrl = "https://www.amazon.com.br";
        Url existing = Url.builder().id(UUID.randomUUID()).originalUrl(originalUrl).build();

        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existing));

        assertThrows(DuplicateUrlException.class, () -> urlService.createShortUrl(originalUrl));
    }

    @Test
    void createShortUrl_nullOrEmpty() {
        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(null));
        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(""));
    }

    @Test
    void accessShortnedUrl_success() {
        String shortened = "abc123";
        Url url = Url.builder().id(UUID.randomUUID()).shortened(shortened).originalUrl("https://www.amazon.com.br").build();

        when(urlRepository.findByShortened(shortened)).thenReturn(Optional.of(url));

        Optional<Url> result = urlService.accessShortnedUrl(shortened);

        assertNotNull(result);
        assertEquals(shortened, result.get().getShortened());
    }

    @Test
    void accessShortnedUrl_notFound() {
        when(urlRepository.findByShortened("notfound")).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> urlService.accessShortnedUrl("notfound"));
    }

    @Test
    void getUrlStats_success() {
        String shortened = "abc123";
        Url url = Url.builder().id(UUID.randomUUID()).shortened(shortened).originalUrl("https://www.amazon.com.br").build();

        when(urlRepository.findByShortened(shortened)).thenReturn(Optional.of(url));
        when(accessRepository.countByUrl(url)).thenReturn(2L);

        Access access1 = Access.builder().url(url).accessedAt(java.time.LocalDateTime.now().minusDays(1)).build();
        Access access2 = Access.builder().url(url).accessedAt(java.time.LocalDateTime.now()).build();
        when(accessRepository.findByUrl(url)).thenReturn(List.of(access1, access2));

        Map<String, Object> stats = urlService.getUrlStats(shortened);

        assertEquals(2L, stats.get("total"));
        assertTrue((double) stats.get("averagePerDay") > 0);
        assertTrue(((Map<?, ?>) stats.get("accessPerDay")).size() > 0);
    }

    @Test
    void getUrlStats_noAccesses() {
        String shortened = "abc123";
        Url url = Url.builder().id(UUID.randomUUID()).shortened(shortened).originalUrl("https://www.amazon.com.br").build();

        when(urlRepository.findByShortened(shortened)).thenReturn(Optional.of(url));
        when(accessRepository.countByUrl(url)).thenReturn(0L);
        when(accessRepository.findByUrl(url)).thenReturn(List.of());

        Map<String, Object> stats = urlService.getUrlStats(shortened);

        assertEquals(0L, stats.get("total"));
        assertEquals(0.0, stats.get("averagePerDay"));
        assertTrue(((Map<?, ?>) stats.get("accessPerDay")).isEmpty());
    }

    @Test
    void getUrlStats_urlNotFound() {
        when(urlRepository.findByShortened("notfound")).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> urlService.getUrlStats("notfound"));
    }

    @Test
    void getUrlStats_statsUnavailable() {
        String shortened = "abc123";
        Url url = Url.builder().id(UUID.randomUUID()).shortened(shortened).originalUrl("https://www.amazon.com.br").build();

        when(urlRepository.findByShortened(shortened)).thenReturn(Optional.of(url));
        when(accessRepository.countByUrl(url)).thenThrow(new RuntimeException("DB error"));

        assertThrows(com.tds.urlshortener.exception.StatsUnavailableException.class,
                () -> urlService.getUrlStats(shortened));
    }

    @Test
    void accessShortnedUrl_createsAccess() {
        String shortened = "abc123";
        Url url = Url.builder().id(UUID.randomUUID()).shortened(shortened).originalUrl("https://www.amazon.com.br").build();
        when(urlRepository.findByShortened(shortened)).thenReturn(Optional.of(url));

        urlService.accessShortnedUrl(shortened);

        verify(accessRepository).save(any());
    }
}
