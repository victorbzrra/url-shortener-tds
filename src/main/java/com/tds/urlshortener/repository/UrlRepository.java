package com.tds.urlshortener.repository;

import com.tds.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UrlRepository extends JpaRepository<Url, UUID> {
    Optional<Url> findByShortened(String shortened);

    Optional<Object> findByOriginalUrl(String originalUrl);
}
