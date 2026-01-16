package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlRedirectApiControllerTest {

	@Mock
	private InMemoryUrlMappingRepository repository;

	private UrlRedirectApiController controller;

	@BeforeEach
	void setUp() {
		controller = new UrlRedirectApiController(repository);
	}

	@Test
	void testRedirectToFullUrlSuccess() {
		String shortPath = "abc123";
		String fullUrl = "https://example.com/long/path";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertNotNull(response.getHeaders());
		assertEquals(URI.create(fullUrl), response.getHeaders().getLocation());
		assertNull(response.getBody());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testRedirectToFullUrlNotFound() {
		String shortPath = "nonexistent";

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.empty());

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());

		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testRedirectIncrementsAccessCount() {
		String shortPath = "xyz789";
		String fullUrl = "https://test.com";
		UrlMapping mapping = spy(new UrlMapping(shortPath, fullUrl));

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		controller._redirectToFullUrl(shortPath);

		verify(mapping, times(1)).incrementAccessCount();
		verify(repository, times(1)).findByShortUrlPath(shortPath);
	}

	@Test
	void testRedirectDoesNotIncrementAccessCountWhenNotFound() {
		String shortPath = "notfound";

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.empty());

		controller._redirectToFullUrl(shortPath);

		verify(repository, times(1)).findByShortUrlPath(shortPath);
		verify(repository, never()).save(any());
	}

	@Test
	void testRedirectWithDifferentUrls() {
		String shortPath1 = "url1";
		String fullUrl1 = "https://example1.com";
		String shortPath2 = "url2";
		String fullUrl2 = "https://example2.com";

		UrlMapping mapping1 = new UrlMapping(shortPath1, fullUrl1);
		UrlMapping mapping2 = new UrlMapping(shortPath2, fullUrl2);

		when(repository.findByShortUrlPath(shortPath1)).thenReturn(Optional.of(mapping1));
		when(repository.findByShortUrlPath(shortPath2)).thenReturn(Optional.of(mapping2));

		ResponseEntity<Void> response1 = controller._redirectToFullUrl(shortPath1);
		ResponseEntity<Void> response2 = controller._redirectToFullUrl(shortPath2);

		assertEquals(URI.create(fullUrl1), response1.getHeaders().getLocation());
		assertEquals(URI.create(fullUrl2), response2.getHeaders().getLocation());
	}

	@Test
	void testRedirectWithUrlContainingQueryParameters() {
		String shortPath = "query123";
		String fullUrl = "https://example.com/path?param1=value1&param2=value2";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertEquals(URI.create(fullUrl), response.getHeaders().getLocation());
	}

	@Test
	void testRedirectWithUrlContainingFragment() {
		String shortPath = "frag123";
		String fullUrl = "https://example.com/page#section";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertEquals(URI.create(fullUrl), response.getHeaders().getLocation());
	}

	@Test
	void testRedirectWithLongPath() {
		String shortPath = "short";
		String fullUrl = "https://example.com/very/long/path/with/many/segments/to/test";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertEquals(URI.create(fullUrl), response.getHeaders().getLocation());
	}


	@Test
	void testRedirectResponseHasNoBody() {
		String shortPath = "test";
		String fullUrl = "https://test.com";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertNull(response.getBody());
	}

	@Test
	void testRedirectResponseHasLocationHeader() {
		String shortPath = "header";
		String fullUrl = "https://header-test.com";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		HttpHeaders headers = response.getHeaders();
		assertNotNull(headers);
		assertNotNull(headers.getLocation());
		assertEquals(URI.create(fullUrl), headers.getLocation());
	}

	@Test
	void testRedirectWithSpecialCharactersInUrl() {
		String shortPath = "special";
		String fullUrl = "https://example.com/path?name=John%20Doe&age=30";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertEquals(URI.create(fullUrl), response.getHeaders().getLocation());
	}

	@Test
	void testAccessCountIncrementedBeforeRedirect() {
		String shortPath = "increment";
		String fullUrl = "https://example.com";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		assertEquals(0, mapping.getAccessCount());

		controller._redirectToFullUrl(shortPath);

		assertEquals(1, mapping.getAccessCount());
	}

	@Test
	void testNotFoundReturnsNoLocationHeader() {
		String shortPath = "missing";

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.empty());

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getHeaders().getLocation());
	}

	@Test
	void testRepositoryCalledWithExactPath() {
		String shortPath = "exact123";
		String fullUrl = "https://example.com";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		controller._redirectToFullUrl(shortPath);

		verify(repository, times(1)).findByShortUrlPath(eq(shortPath));
	}

	@Test
	void testRedirectWithComplexQueryString() {
		String shortPath = "complex";
		String fullUrl = "https://api.example.com/search?q=test&filter=active&sort=date&order=desc";
		UrlMapping mapping = new UrlMapping(shortPath, fullUrl);

		when(repository.findByShortUrlPath(shortPath)).thenReturn(Optional.of(mapping));

		ResponseEntity<Void> response = controller._redirectToFullUrl(shortPath);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertEquals(URI.create(fullUrl), response.getHeaders().getLocation());
	}
}
