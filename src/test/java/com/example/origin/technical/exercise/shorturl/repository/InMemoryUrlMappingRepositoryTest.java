package com.example.origin.technical.exercise.shorturl.repository;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUrlMappingRepositoryTest {

    private InMemoryUrlMappingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUrlMappingRepository();
    }

    @Test
    void testSaveAndFindByShortUrlPath() {
        UrlMapping mapping = new UrlMapping("shortUrl", "http://fullUrl.com");
        repository.save(mapping);
        Optional<UrlMapping> found = repository.findByShortUrlPath("shortUrl");
        assertTrue(found.isPresent());
        assertEquals(mapping, found.get());
    }

    @Test
    void testCount() {
        assertEquals(0, repository.count());
        repository.save(new UrlMapping("shortUrl", "http://fullUrl.com"));
        assertEquals(1, repository.count());
    }

    @Test
    void testDeleteByShortUrlPath() {
        UrlMapping mapping = new UrlMapping("shortUrl", "http://fullUrl.com");
        repository.save(mapping);
        assertTrue(repository.deleteByShortUrlPath("shortUrl"));
        assertFalse(repository.existsByShortUrlPath("shortUrl"));
    }
}
