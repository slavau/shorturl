package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Optional;

/**
 * REST controller responsible for handling URL redirection operations.
 * <p>
 * This controller provides an endpoint for redirecting short URL paths to their
 * corresponding full URLs. When a short URL is accessed, this controller looks up
 * the mapping, increments the access counter, and returns an HTTP 302 (Found)
 * redirect response to the original full URL.
 * </p>
 */
@Controller
@AllArgsConstructor
public class UrlRedirectApiController implements UrlRedirectApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;

	/**
	 * Redirects a short URL path to its corresponding full URL.
	 * <p>
	 * This method performs the following operations:
	 * <ul>
	 *   <li>Looks up the URL mapping by the short URL path</li>
	 *   <li>Returns HTTP 404 (Not Found) if the mapping doesn't exist</li>
	 *   <li>Increments the access counter for the mapping</li>
	 *   <li>Returns HTTP 302 (Found) redirect response with the Location header
	 *       set to the full URL</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The access count is incremented before the redirect to track how many times
	 * each shortened URL has been accessed.
	 * </p>
	 */
	@Override
	public ResponseEntity<Void> _redirectToFullUrl(String path) {
		Optional<UrlMapping> byShortUrl = inMemoryUrlMappingRepository.findByShortUrlPath(path);

		if (byShortUrl.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		UrlMapping urlMapping = byShortUrl.get();
		urlMapping.incrementAccessCount();

		// Create HttpHeaders to set the Location header
		HttpHeaders headers = new HttpHeaders();
		// Set the Location header with the full URL
		headers.setLocation(URI.create(urlMapping.getFullUrl()));

		// Return a ResponseEntity with the headers and a 302 Found status
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
