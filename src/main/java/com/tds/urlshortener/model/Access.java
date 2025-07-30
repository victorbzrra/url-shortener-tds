package com.tds.urlshortener.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Access {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    @Column(nullable = false, updatable = false)
    private LocalDateTime accessedAt;

    @PrePersist
    public void prePersist() {
        this.accessedAt = LocalDateTime.now();
    }
}
