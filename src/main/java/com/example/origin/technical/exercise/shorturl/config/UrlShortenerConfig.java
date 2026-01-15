package com.example.origin.technical.exercise.shorturl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for URL shortener service.
 * Can be customized via application.properties or application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "url.shortener")
public class UrlShortenerConfig {

    /**
     * The base URL for shortened URLs (e.g., "https://short.url")
     */
    private String baseUrl = "http://localhost:8080";

    /**
     * Length of generated short URL
     */
    private int shortUrlLength = 7;
}