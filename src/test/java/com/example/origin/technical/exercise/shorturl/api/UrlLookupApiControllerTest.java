package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.GetFullUrlResponse;
import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlLookupApiControllerTest {

	@Mock
	private InMemoryUrlMappingRepository repository;

	private UrlLookupApiController controller;

	@BeforeEach
	void setUp() {
		controller = new UrlLookupApiController(repository);
	}

	@Test
	void testGetFullUrlSuccess() {
		String shortPath = "abc123";
		String shortUrl = "http://localhost:8080/" + shortPath;
		String fullUrl = "https://example.com/long/path";
		LocalDateTime now = LocalDateTime.now();

		UrlMapping mapping = UrlMapping.builder()
			.shortUrlPath(shortPath)
			.fullUrl(fullUrl)
			.createdAt(now)
			.lastAccessedAt(now)
			.expiresAt(now.plusDays(360))
			.accessCount(5)
			.build();

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(URI.create(fullUrl), response.getBody().getFullUrl());
		assertEquals(now, response.getBody().getCreatedAt());
		assertEquals(now, response.getBody().getLastAccessedAt());
		assertEquals(now.plusDays(360), response.getBody().getExpiresAt());
		assertEquals(5, response.getBody().getAccessCount());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testGetFullUrlNotFound() {
		String shortPath = "nonexistent";
		String shortUrl = "http://localhost:8080/" + shortPath;

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.empty());

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testGetFullUrlWithLeadingSlash() {
		String shortPath = "xyz789";
		String shortUrl = "/" + shortPath;
		String fullUrl = "https://test.com";

		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);
		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(URI.create(fullUrl), response.getBody().getFullUrl());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testGetFullUrlWithoutLeadingSlash() {
		String shortPath = "def456";
		String shortUrl = shortPath;
		String fullUrl = "https://another-test.com";

		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);
		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(URI.create(fullUrl), response.getBody().getFullUrl());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testGetFullUrlWithCompleteUrl() {
		String shortPath = "ghi789";
		String shortUrl = "http://short.url/" + shortPath;
		String fullUrl = "https://long.example.com/path?query=value";

		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);
		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(URI.create(fullUrl), response.getBody().getFullUrl());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testGetFullUrlWithZeroAccessCount() {
		String shortPath = "new123";
		String shortUrl = shortPath;
		String fullUrl = "https://fresh.com";

		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);
		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(0, response.getBody().getAccessCount());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testGetFullUrlResponseMapping() {
		String shortPath = "map123";
		String shortUrl = shortPath;
		String fullUrl = "https://mapping.test.com";
		LocalDateTime created = LocalDateTime.of(2025, 1, 1, 10, 0);
		LocalDateTime accessed = LocalDateTime.of(2025, 1, 15, 14, 30);
		LocalDateTime expires = LocalDateTime.of(2025, 12, 31, 23, 59);

		UrlMapping mapping = UrlMapping.builder()
			.shortUrlPath(shortPath)
			.fullUrl(fullUrl)
			.createdAt(created)
			.lastAccessedAt(accessed)
			.expiresAt(expires)
			.accessCount(42)
			.build();

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		GetFullUrlResponse body = response.getBody();
		assertNotNull(body);
		assertEquals(URI.create(fullUrl), body.getFullUrl());
		assertEquals(created, body.getCreatedAt());
		assertEquals(accessed, body.getLastAccessedAt());
		assertEquals(expires, body.getExpiresAt());
		assertEquals(42, body.getAccessCount());
	}

	@Test
	void testGetFullUrlWithNestedPath() {
		String shortPath = "nested/path/123";
		String shortUrl = "http://localhost:8080/" + shortPath;
		String fullUrl = "https://example.com";

		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);
		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<GetFullUrlResponse> response = controller._getFullUrl(shortUrl);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testRepositoryCalledWithCorrectParameter() {
		String shortPath = "verify123";
		String shortUrl = "http://example.com/" + shortPath;

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.empty());

		controller._getFullUrl(shortUrl);

		verify(repository, times(1)).findByShortUrlPath(eq(shortPath));
		verify(repository, never()).findByShortUrlPath(argThat(arg -> arg.startsWith("/")));
	}
}
