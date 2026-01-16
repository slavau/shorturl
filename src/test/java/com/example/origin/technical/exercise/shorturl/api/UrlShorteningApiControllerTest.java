package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.config.UrlShortenerConfig;
import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlRequest;
import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlResponse;
import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import com.example.origin.technical.exercise.shorturl.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShorteningApiControllerTest {

	@Mock
	private InMemoryUrlMappingRepository repository;

	@Mock
	private UrlShortenerService urlShortenerService;

	@Mock
	private UrlShortenerConfig config;

	private UrlShorteningApiController controller;

	@BeforeEach
	void setUp() {
		controller = new UrlShorteningApiController(repository, urlShortenerService, config);
	}

	@Test
	void testCreateShortUrlSuccess() {
		String fullUrl = "https://example.com/long/path";
		String shortPath = "abc123";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath);
		when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(URI.create(baseUrl + shortPath), response.getBody().getShortUrl());

		verify(repository).findByFullUrl(fullUrl);
		verify(urlShortenerService).generateShortUrlPath();
		verify(repository).save(any(UrlMapping.class));
		verify(config).getBaseUrl();
	}

	@Test
	void testCreateShortUrlReturnsExistingMapping() {
		String fullUrl = "https://example.com/existing";
		String existingShortPath = "xyz789";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping existingMapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(existingShortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.of(existingMapping));
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(URI.create(baseUrl + existingShortPath), response.getBody().getShortUrl());

		verify(repository).findByFullUrl(fullUrl);
		verify(urlShortenerService, never()).generateShortUrlPath();
		verify(repository, never()).save(any());
		verify(config).getBaseUrl();
	}

	@Test
	void testCreateShortUrlSavesCorrectMapping() {
		String fullUrl = "https://test.com";
		String shortPath = "test123";
		String baseUrl = "http://short.url/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		ArgumentCaptor<UrlMapping> mappingCaptor = ArgumentCaptor.forClass(UrlMapping.class);
		UrlMapping savedMapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath);
		when(repository.save(mappingCaptor.capture())).thenReturn(savedMapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		controller._createShortUrl(request);

		UrlMapping capturedMapping = mappingCaptor.getValue();
		assertEquals(fullUrl, capturedMapping.getFullUrl());
		assertEquals(shortPath, capturedMapping.getShortUrlPath());
	}

	@Test
	void testCreateShortUrlWithQueryParameters() {
		String fullUrl = "https://example.com/search?q=test&filter=active";
		String shortPath = "query1";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath);
		when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void testCreateShortUrlWithFragment() {
		String fullUrl = "https://example.com/page#section";
		String shortPath = "frag1";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath);
		when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void testCreateShortUrlGeneratesUniqueShortPath() {
		String fullUrl = "https://example.com";
		String shortPath1 = "path001";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath1)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath1);
		when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		verify(urlShortenerService, times(1)).generateShortUrlPath();
		assertNotNull(response.getBody().getShortUrl());
	}

	@Test
	void testToShortUrlCombinesBaseUrlAndPath() {
		String shortPath = "test123";
		String baseUrl = "http://localhost:8080/";

		when(config.getBaseUrl()).thenReturn(baseUrl);

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl("https://example.com");

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl("https://example.com")
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(anyString())).thenReturn(Optional.of(mapping));

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertEquals(URI.create(baseUrl + shortPath), response.getBody().getShortUrl());
	}

	@Test
	void testCreateShortUrlWithTrailingSlashInBaseUrl() {
		String fullUrl = "https://example.com";
		String shortPath = "path1";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath);
		when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertEquals(URI.create("http://localhost:8080/path1"), response.getBody().getShortUrl());
	}

	@Test
	void testCreateShortUrlSameUrlTwiceReturnsExisting() {
		String fullUrl = "https://example.com";
		String shortPath = "existing";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping existingMapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.of(existingMapping));
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response1 = controller._createShortUrl(request);
		ResponseEntity<CreateShortUrlResponse> response2 = controller._createShortUrl(request);

		assertEquals(response1.getBody().getShortUrl(), response2.getBody().getShortUrl());
		verify(repository, times(2)).findByFullUrl(fullUrl);
		verify(repository, never()).save(any());
	}

	@Test
	void testCreateShortUrlResponseBodyNotNull() {
		String fullUrl = "https://example.com";
		String shortPath = "test";
		String baseUrl = "http://localhost:8080/";

		CreateShortUrlRequest request = new CreateShortUrlRequest();
		request.setUrl(fullUrl);

		UrlMapping mapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(shortPath)
			.build();

		when(repository.findByFullUrl(fullUrl)).thenReturn(Optional.empty());
		when(urlShortenerService.generateShortUrlPath()).thenReturn(shortPath);
		when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
		when(config.getBaseUrl()).thenReturn(baseUrl);

		ResponseEntity<CreateShortUrlResponse> response = controller._createShortUrl(request);

		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getShortUrl());
	}
}
