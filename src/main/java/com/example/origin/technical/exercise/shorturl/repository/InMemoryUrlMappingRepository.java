package com.example.origin.technical.exercise.shorturl.repository;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the UrlMappingRepository interface.
 */
public class InMemoryUrlMappingRepository implements UrlMappingRepository {

    private final Map<String, UrlMapping> urlMappingStore = new HashMap<>();

    @Override
    public UrlMapping save(UrlMapping mapping) {
        urlMappingStore.put(mapping.getShortUrl(), mapping);
        return mapping;
    }

    @Override
    public Optional<UrlMapping> findByShortUrl(String shortUrl) {
        return Optional.ofNullable(urlMappingStore.get(shortUrl));
    }

    @Override
    public Optional<UrlMapping> findByFullUrl(String fullUrl) {
        return urlMappingStore.values().stream()
                .filter(mapping -> mapping.getFullUrl().equals(fullUrl))
                .findFirst();
    }

    @Override
    public boolean existsByShortUrl(String shortUrl) {
        return urlMappingStore.containsKey(shortUrl);
    }

    @Override
    public boolean deleteByShortUrl(String shortUrl) {
        return urlMappingStore.remove(shortUrl) != null;
    }

    @Override
    public long count() {
        return urlMappingStore.size();
    }

    @Override
    public void deleteAll() {
        urlMappingStore.clear();
    }
}