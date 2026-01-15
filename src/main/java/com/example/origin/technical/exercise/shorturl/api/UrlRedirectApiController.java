package com.example.origin.technical.exercise.shorturl.api;

import com.example.origin.technical.exercise.shorturl.model.UrlMapping;
import com.example.origin.technical.exercise.shorturl.repository.InMemoryUrlMappingRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class UrlRedirectApiController implements UrlRedirectApi {

	@Autowired
	private HttpServletRequest httpServletRequest;

	private final InMemoryUrlMappingRepository inMemoryUrlMappingRepository;

	@Override
	public ResponseEntity<Void> _redirectToFullUrl(String pathStar) {
		StringBuffer requestURL = httpServletRequest.getRequestURL();
		Optional<UrlMapping> byShortUrl = inMemoryUrlMappingRepository.findByShortUrl(requestURL.toString());

		if (byShortUrl.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		UrlMapping urlMapping = byShortUrl.get();
		// Create HttpHeaders to set the Location header
		HttpHeaders headers = new HttpHeaders();
		// Set the Location header with the full URL
		headers.setLocation(URI.create(urlMapping.getFullUrl()));

		// Return a ResponseEntity with the headers and a 302 Found status
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
