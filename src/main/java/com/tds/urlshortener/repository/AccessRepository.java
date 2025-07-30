package com.tds.urlshortener.repository;

import com.tds.urlshortener.model.Access;
import com.tds.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AccessRepository extends JpaRepository<Access, UUID> {
    List<Access> findByUrlAndAccessedAtBetween(Url url, LocalDateTime start, LocalDateTime end);
    Long countByUrl(Url url);
}
