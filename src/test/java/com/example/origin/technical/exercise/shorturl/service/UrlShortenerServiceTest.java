package com.example.origin.technical.exercise.shorturl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerServiceTest {

	private UrlShortenerService urlShortenerService;

	@BeforeEach
	void setUp() {
		urlShortenerService = new UrlShortenerService();
	}

	@Test
	void testGenerateShortUrlPathNotNull() {
		String shortUrl = urlShortenerService.generateShortUrlPath();
		assertNotNull(shortUrl);
	}

	@Test
	void testGenerateShortUrlPathCorrectLength() {
		String shortUrl = urlShortenerService.generateShortUrlPath();
		assertEquals(7, shortUrl.length());
	}

	@Test
	void testGenerateShortUrlPathContainsOnlyBase62Characters() {
		String shortUrl = urlShortenerService.generateShortUrlPath();
		String base62Alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		for (char c : shortUrl.toCharArray()) {
			assertTrue(base62Alphabet.indexOf(c) != -1,
				"Character '" + c + "' is not a valid Base62 character");
		}
	}

	@RepeatedTest(100)
	void testGenerateShortUrlPathUniqueness() {
		Set<String> generatedUrls = new HashSet<>();
		int numberOfUrls = 1000;

		for (int i = 0; i < numberOfUrls; i++) {
			String shortUrl = urlShortenerService.generateShortUrlPath();
			generatedUrls.add(shortUrl);
		}

		// Should generate unique URLs (collision rate should be very low)
		assertTrue(generatedUrls.size() > numberOfUrls * 0.99,
			"Expected at least 99% unique URLs, got " + generatedUrls.size() + " out of " + numberOfUrls);
	}

	@Test
	void testGenerateShortUrlPathNonSequential() {
		String url1 = urlShortenerService.generateShortUrlPath();
		String url2 = urlShortenerService.generateShortUrlPath();
		String url3 = urlShortenerService.generateShortUrlPath();

		assertNotEquals(url1, url2);
		assertNotEquals(url2, url3);
		assertNotEquals(url1, url3);
	}

	@Test
	void testIsValidShortUrlFormatValid() {
		String validUrl = "aB3xK9p";
		assertTrue(urlShortenerService.isValidShortUrlFormat(validUrl));
	}

	@Test
	void testIsValidShortUrlFormatWithNumbers() {
		String validUrl = "1234567";
		assertTrue(urlShortenerService.isValidShortUrlFormat(validUrl));
	}

	@Test
	void testIsValidShortUrlFormatWithUpperCase() {
		String validUrl = "ABCDEFG";
		assertTrue(urlShortenerService.isValidShortUrlFormat(validUrl));
	}

	@Test
	void testIsValidShortUrlFormatWithLowerCase() {
		String validUrl = "abcdefg";
		assertTrue(urlShortenerService.isValidShortUrlFormat(validUrl));
	}

	@Test
	void testIsValidShortUrlFormatMixed() {
		String validUrl = "aB1cD2e";
		assertTrue(urlShortenerService.isValidShortUrlFormat(validUrl));
	}

	@Test
	void testIsValidShortUrlFormatNull() {
		assertFalse(urlShortenerService.isValidShortUrlFormat(null));
	}

	@Test
	void testIsValidShortUrlFormatEmpty() {
		assertFalse(urlShortenerService.isValidShortUrlFormat(""));
	}

	@Test
	void testIsValidShortUrlFormatTooShort() {
		String shortUrl = "abc123";
		assertFalse(urlShortenerService.isValidShortUrlFormat(shortUrl));
	}

	@Test
	void testIsValidShortUrlFormatTooLong() {
		String shortUrl = "abc12345";
		assertFalse(urlShortenerService.isValidShortUrlFormat(shortUrl));
	}

	@Test
	void testIsValidShortUrlFormatInvalidCharacters() {
		String invalidUrl = "abc-123";
		assertFalse(urlShortenerService.isValidShortUrlFormat(invalidUrl));
	}

	@Test
	void testIsValidShortUrlFormatWithSpecialCharacters() {
		assertFalse(urlShortenerService.isValidShortUrlFormat("abc@123"));
		assertFalse(urlShortenerService.isValidShortUrlFormat("abc#123"));
		assertFalse(urlShortenerService.isValidShortUrlFormat("abc$123"));
		assertFalse(urlShortenerService.isValidShortUrlFormat("abc%123"));
	}

	@Test
	void testIsValidShortUrlFormatWithWhitespace() {
		assertFalse(urlShortenerService.isValidShortUrlFormat("abc 123"));
		assertFalse(urlShortenerService.isValidShortUrlFormat(" abc123"));
		assertFalse(urlShortenerService.isValidShortUrlFormat("abc123 "));
	}

	@Test
	void testGeneratedUrlIsValidFormat() {
		String generatedUrl = urlShortenerService.generateShortUrlPath();
		assertTrue(urlShortenerService.isValidShortUrlFormat(generatedUrl),
			"Generated URL '" + generatedUrl + "' should be valid");
	}

	@RepeatedTest(50)
	void testMultipleGeneratedUrlsAreValidFormat() {
		for (int i = 0; i < 100; i++) {
			String generatedUrl = urlShortenerService.generateShortUrlPath();
			assertTrue(urlShortenerService.isValidShortUrlFormat(generatedUrl),
				"Generated URL '" + generatedUrl + "' should be valid");
		}
	}

	@Test
	void testServiceNotNullAfterConstruction() {
		UrlShortenerService service = new UrlShortenerService();
		assertNotNull(service);
	}

	@Test
	void testMultipleServicesGenerateDifferentUrls() {
		UrlShortenerService service1 = new UrlShortenerService();
		UrlShortenerService service2 = new UrlShortenerService();

		String url1 = service1.generateShortUrlPath();
		String url2 = service2.generateShortUrlPath();

		// Different service instances should produce different URLs due to different salts
		assertNotNull(url1);
		assertNotNull(url2);
	}

	@Test
	void testConcurrentGeneration() throws InterruptedException {
		Set<String> urls = new HashSet<>();
		int numberOfThreads = 10;
		int urlsPerThread = 100;

		Thread[] threads = new Thread[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i] = new Thread(() -> {
				for (int j = 0; j < urlsPerThread; j++) {
					synchronized (urls) {
						urls.add(urlShortenerService.generateShortUrlPath());
					}
				}
			});
			threads[i].start();
		}

		for (Thread thread : threads) {
			thread.join();
		}

		// Should have generated mostly unique URLs
		assertTrue(urls.size() > (numberOfThreads * urlsPerThread) * 0.99,
			"Expected at least 99% unique URLs in concurrent scenario");
	}
}
