package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.config.UrlShortenerConfig;
import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlRequest;
import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlResponse;
import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import com.example.origin.technical.exercise.shorturl.service.UrlShortenerService;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Optional;

/**
 * REST controller responsible for handling URL shortening operations.
 * <p>
 * This controller provides an endpoint for creating shortened URLs from full URLs.
 * It checks for existing mappings to avoid duplicate short URLs for the same
 * full URL and generates new short URLs using the {@link UrlShortenerService}.
 * </p>
 */
@Controller
@AllArgsConstructor
public class UrlShorteningApiController implements UrlShorteningApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;
	private final UrlShortenerService urlShortenerService;
	private final UrlShortenerConfig urlShortenerConfig;


	/**
	 * Creates a shortened URL for the provided full URL.
	 * <p>
	 * This method first checks if a mapping already exists for the given full URL.
	 * If found, it returns the existing short URL to maintain idempotency.
	 * Otherwise, it generates a new short URL path, saves the mapping, and returns
	 * the complete short URL.
	 * </p>
	 *
	 * @param createShortUrlRequest the request containing the full URL to shorten
	 * @return a {@link ResponseEntity} containing the {@link CreateShortUrlResponse}
	 *         with the shortened URL
	 */
	@Override
	public ResponseEntity<CreateShortUrlResponse> _createShortUrl(CreateShortUrlRequest createShortUrlRequest) {
		String fullUrl = createShortUrlRequest.getUrl();
		Optional<UrlMapping> existingMapping = inMemoryUrlMappingRepository.findByFullUrl(fullUrl);
		if (existingMapping.isPresent()) {
			URI shortUrl = toShortUrl(existingMapping.get().getShortUrlPath());
			return ResponseEntity.ok(new CreateShortUrlResponse().shortUrl(shortUrl));
		}

		var urlMapping = UrlMapping.builder()
			.fullUrl(fullUrl)
			.shortUrlPath(urlShortenerService.generateShortUrlPath())
			.build();
		UrlMapping savedMapping = inMemoryUrlMappingRepository.save(urlMapping);

		return ResponseEntity.ok(new CreateShortUrlResponse()
			.shortUrl(toShortUrl(savedMapping.getShortUrlPath())));
	}

	private @NonNull URI toShortUrl(String shortUrlPath) {
		return URI.create(urlShortenerConfig.getBaseUrl() + shortUrlPath);
	}
}
