package com.tds.urlshortener.service;

import com.tds.urlshortener.model.Access;
import com.tds.urlshortener.model.Url;
import com.tds.urlshortener.repository.AccessRepository;
import com.tds.urlshortener.repository.UrlRepository;
import com.tds.urlshortener.util.Base62;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final AccessRepository accessRepository;

    public UrlService(UrlRepository urlRepository, AccessRepository accessRepository) {
        this.urlRepository = urlRepository;
        this.accessRepository = accessRepository;
    }

    public Url createShortUrl(String originalUrl) {
        Url url = Url.builder().originalUrl(originalUrl).build();
        url = urlRepository.save(url);

        long uniqueNumber = Math.abs(url.getId().getMostSignificantBits() ^ url.getId().getLeastSignificantBits());

        String shortUrl = Base62.encode(uniqueNumber);
        url.setShortened(shortUrl);

        return urlRepository.save(url);
    }

    public Optional<Url> accessShortnedUrl(String shortenedUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortened(shortenedUrl);

        urlOptional.ifPresent(url -> {
            Access access = Access.builder().url(url).build();
            accessRepository.save(access);
        });

        return urlOptional;
    }

    public Map<String, Object> getUrlStats(String shortenedUrl) {
        Url url = urlRepository.findByShortened(shortenedUrl)
                .orElseThrow(() -> new NoSuchElementException("Url not found"));

        Long total = accessRepository.countByUrl(url);

        List<Access> accesses = accessRepository.findByUrl(url);

        Map<LocalDate, Long> accessPerDay = new TreeMap<>();
        for (Access access : accesses) {
            LocalDate date = access.getAccessedAt().toLocalDate();
            accessPerDay.put(date, accessPerDay.getOrDefault(date, 0L) + 1);
        }

        double averagePerDay = accessPerDay.values().stream().mapToLong(v -> v).average().orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("averagePerDay", averagePerDay);
        stats.put("accessPerDay", accessPerDay);

        return stats;
    }

}
