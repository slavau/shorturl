package com.example.origin.technical.exercise.shorturl.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Entity representing the mapping between a short URL and a full URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UrlMapping {

    @EqualsAndHashCode.Include
    private String shortUrlPath;

    private String fullUrl;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime lastAccessedAt = LocalDateTime.now();

	@Builder.Default
	private LocalDateTime expiresAt = LocalDateTime.now().plusDays(360);

    @Builder.Default
    private long accessCount = 0;

    public UrlMapping(String shortUrlPath, String fullUrl) {
        this.shortUrlPath = shortUrlPath;
        this.fullUrl = fullUrl;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
		this.expiresAt = LocalDateTime.now().plusDays(360);
        this.accessCount = 0;
    }

    public void incrementAccessCount() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }
}
