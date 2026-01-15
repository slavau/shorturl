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

@Controller
@AllArgsConstructor
public class UrlShorteningApiController implements UrlShorteningApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;
	private final UrlShortenerService urlShortenerService;
	private final UrlShortenerConfig urlShortenerConfig;

	@Override
	public ResponseEntity<CreateShortUrlResponse> _createShortUrl(CreateShortUrlRequest createShortUrlRequest) {
		String fullUrl = createShortUrlRequest.getUrl().toString();
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
