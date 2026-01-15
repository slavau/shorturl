package com.example.origin.technical.exercise.shorturl.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for generating unique, short, and non-sequential URLs using one-way hashing. 
 * Uses SHA-256 hashing with salt and Base62 encoding to create irreversible short URLs.
 */
@Service
public class UrlShortenerService {

    private static final String ALGORITHM = "SHA-256";
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_URL_LENGTH = 7;
    
    private final AtomicLong counter;
    private final String salt;
    private final SecureRandom secureRandom;

    public UrlShortenerService() {
        this.counter = new AtomicLong(System.currentTimeMillis());
        this.salt = generateRandomSalt();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a unique short URL identifier using one-way hashing.
     * Combines timestamp, counter, and random value for uniqueness.
     * 
     * @return A short, unique, non-sequential identifier (e.g., "aB3xK9p")
     */
    public String generateShortUrl() {
        // Create unique input by combining multiple sources
        long id = counter.incrementAndGet();
        long timestamp = System.nanoTime();
        int randomValue = secureRandom.nextInt();
        
        String input = id + "-" + timestamp + "-" + randomValue + "-" + salt;
        
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // Convert hash to Base62 and take first N characters
            return toBase62(hash).substring(0, SHORT_URL_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate short URL", e);
        }
    }

    /**
     * Converts a byte array to Base62 encoded string.
     * Base62 uses alphanumeric characters (0-9, A-Z, a-z) for URL-friendly output.
     * 
     * @param bytes The byte array to encode
     * @return Base62 encoded string
     */
    private String toBase62(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        
        // Convert bytes to a large number and encode in Base62
        for (byte b : bytes) {
            int value = b & 0xFF;
            result.append(BASE62_ALPHABET.charAt(value % 62));
        }
        
        return result.toString();
    }

    /**
     * Generates a random salt for additional security.
     * 
     * @return Random salt string
     */
    private String generateRandomSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return toBase62(saltBytes);
    }

    /**
     * Validates if a short URL is in the correct format.
     * Only checks format, not existence in database.
     * 
     * @param shortUrl The short URL to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidShortUrlFormat(String shortUrl) {
        if (shortUrl == null || shortUrl.isEmpty()) {
            return false;
        }
        
        if (shortUrl.length() != SHORT_URL_LENGTH) {
            return false;
        }
        
        // Check if all characters are in Base62 alphabet
        for (char c : shortUrl.toCharArray()) {
            if (BASE62_ALPHABET.indexOf(c) == -1) {
                return false;
            }
        }
        
        return true;
    }
}