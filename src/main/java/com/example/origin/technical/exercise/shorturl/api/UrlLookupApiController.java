package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.GetFullUrlResponse;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;

@Controller
@AllArgsConstructor
public class UrlLookupApiController implements UrlLookupApi {

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;

	@Override
	public ResponseEntity<GetFullUrlResponse> _getFullUrl(URI shortUrl) {
		var mappingOpt = inMemoryUrlMappingRepository.findByShortUrl(shortUrl.toString());
		return mappingOpt
			.map(mapping -> ResponseEntity.ok(new GetFullUrlResponse().fullUrl(URI.create(mapping.getFullUrl()))))
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
