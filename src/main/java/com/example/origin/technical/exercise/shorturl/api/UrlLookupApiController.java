package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.GetFullUrlResponse;
import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.URL;

@Controller
@AllArgsConstructor
public class UrlLookupApiController implements UrlLookupApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;

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
