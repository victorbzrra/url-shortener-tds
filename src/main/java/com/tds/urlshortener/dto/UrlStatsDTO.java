package com.tds.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class UrlStatsDTO {
    private long total;
    private double averagePerDay;
    private Map<LocalDate, Long> accessPerDay;
}
