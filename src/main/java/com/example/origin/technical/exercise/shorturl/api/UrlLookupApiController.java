package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.GetFullUrlResponse;
import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;

/**
 * REST controller responsible for URL lookup operations.
 * <p>
 * This controller provides an endpoint for retrieving the full URL and associated
 * metadata from a shortened URL. It extracts the short URL path, looks up the
 * corresponding mapping, and returns detailed information including access statistics
 * and expiration data.
 * </p>
 */
@Controller
@AllArgsConstructor
public class UrlLookupApiController implements UrlLookupApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;

	/**
	 * Retrieves the full URL and metadata for a given short URL.
	 * <p>
	 * This method performs the following operations:
	 * <ul>
	 *   <li>Extracts the short URL path from the provided URL string</li>
	 *   <li>Returns HTTP 200 (OK) with detailed metadata if mapping exists</li>
	 *   <li>Returns HTTP 404 (Not Found) if the mapping doesn't exist</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The response includes the full URL, creation timestamp, last access timestamp,
	 * expiration timestamp, and total access count.
	 * </p>
	 */
	@Override
	public ResponseEntity<GetFullUrlResponse> _getFullUrl(String shortUrl) {
		var mappingOpt = inMemoryUrlMappingRepository.findByShortUrlPath(toShortPath(shortUrl));
		return mappingOpt
			.map(mapping -> ResponseEntity.ok(toResponse(mapping)))
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	private static String toShortPath(String shortUrl) {
		String path = URI.create(shortUrl).getPath();
		return path.startsWith("/") ? path.substring(1) : path;
	}

	private static GetFullUrlResponse toResponse(UrlMapping mapping) {
		return new GetFullUrlResponse()
			.fullUrl(URI.create(mapping.getFullUrl()))
			.createdAt(mapping.getCreatedAt())
			.lastAccessedAt(mapping.getLastAccessedAt())
			.expiresAt(mapping.getExpiresAt())
			.accessCount(mapping.getAccessCount());
	}
}
