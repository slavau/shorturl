package com.example.origin.technical.exercise.shorturl.repository;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the UrlMappingRepository interface based on the {@link HashMap}.
 *
 * NOTE: This is purely for demonstration and testing purposes only. The real implementation should use a persistent storage solution leveraging Spring Data and JPA.
 */
@Repository
public class InMemoryUrlMappingRepository implements UrlMappingRepository {

    private final Map<String, UrlMapping> urlMappingStoreByShortUrlPath = new HashMap<>();
	private final Map<String, UrlMapping> urlMappingStoreByFullUrl = new HashMap<>();

    @Override
    public UrlMapping save(UrlMapping mapping) {
        urlMappingStoreByShortUrlPath.put(mapping.getShortUrlPath(), mapping);
		urlMappingStoreByFullUrl.put(mapping.getFullUrl(), mapping);

        return mapping;
    }

    @Override
    public Optional<UrlMapping> findByShortUrlPath(String shortUrlPath) {
        return Optional.ofNullable(urlMappingStoreByShortUrlPath.get(shortUrlPath));
    }

    @Override
    public Optional<UrlMapping> findByFullUrl(String fullUrl) {
		 return Optional.ofNullable(urlMappingStoreByFullUrl.get(fullUrl));
    }

    @Override
    public boolean existsByShortUrlPath(String shortUrlPath) {
        return urlMappingStoreByShortUrlPath.containsKey(shortUrlPath);
    }

    @Override
    public boolean deleteByShortUrlPath(String shortUrlPath) {
		UrlMapping removed = urlMappingStoreByShortUrlPath.remove(shortUrlPath);
		if (removed != null) {
			urlMappingStoreByFullUrl.remove(removed.getFullUrl());
		}
		return removed != null;
    }

    @Override
    public long count() {
        return urlMappingStoreByShortUrlPath.size();
    }
}
