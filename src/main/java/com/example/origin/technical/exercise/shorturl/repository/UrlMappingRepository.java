package com.example.origin.technical.exercise.shorturl.repository;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;

import java.util.Optional;

/**
 * Repository interface for managing URL mappings.
 * Stores mappings with short URL as the key.
 */
public interface UrlMappingRepository {

    /**
     * Saves a URL mapping with short URL as the key.
     *
     * @param mapping The URL mapping to save
     * @return The saved URL mapping
     */
    UrlMapping save(UrlMapping mapping);

    /**
     * Finds a URL mapping by short URL (key).
     *
     * @param shortUrl The short URL key
     * @return Optional containing the mapping if found
     */
    Optional<UrlMapping> findByShortUrl(String shortUrl);

    /**
     * Finds a URL mapping by full URL.
     * Useful to check if a full URL has already been shortened.
     *
     * @param fullUrl The full URL
     * @return Optional containing the mapping if found
     */
    Optional<UrlMapping> findByFullUrl(String fullUrl);

    /**
     * Checks if a short URL already exists.
     *
     * @param shortUrl The short URL to check
     * @return true if exists, false otherwise
     */
    boolean existsByShortUrl(String shortUrl);

    /**
     * Deletes a URL mapping by short URL.
     *
     * @param shortUrl The short URL key
     * @return true if deleted, false if not found
     */
    boolean deleteByShortUrl(String shortUrl);

    /**
     * Returns the total number of mappings.
     *
     * @return The count of mappings
     */
    long count();

}
