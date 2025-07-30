package com.tds.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlResponseDTO {
    private String originalUrl;
    private String shortened;
    private String shortUrl;
}
