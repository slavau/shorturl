package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlRequest;
import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlResponse;
import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import com.example.origin.technical.exercise.shorturl.service.UrlShortenerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;

@Controller
@AllArgsConstructor
public class UrlShorteningApiController implements UrlShorteningApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;
	private final UrlShortenerService urlShortenerService;

	@Override
	public ResponseEntity<CreateShortUrlResponse> _createShortUrl(CreateShortUrlRequest createShortUrlRequest) {
		var urlMapping = UrlMapping.builder()
			.fullUrl(createShortUrlRequest.getUrl().toString())
			.shortUrl(urlShortenerService.generateShortUrl())
			.build();
		inMemoryUrlMappingRepository.save(urlMapping);

		URI shortUrl = URI.create(urlMapping.getShortUrl());
		return ResponseEntity.created(shortUrl).body(new CreateShortUrlResponse().shortUrl(shortUrl));
	}
}
