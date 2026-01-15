import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryUrlMappingRepositoryTest {

    private InMemoryUrlMappingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUrlMappingRepository();
    }

    @Test
    void testSaveAndFindByShortUrl() {
        UrlMapping mapping = new UrlMapping("shortUrl", "http://fullUrl.com");
        repository.save(mapping);
        Optional<UrlMapping> found = repository.findByShortUrl("shortUrl");
        assertTrue(found.isPresent());
        assertEquals(mapping, found.get());
    }

    @Test
    void testCount() {
        assertEquals(0, repository.count());
        repository.save(new UrlMapping("shortUrl", "http://fullUrl.com"));
        assertEquals(1, repository.count());
    }

    @Test
    void testDeleteByShortUrl() {
        UrlMapping mapping = new UrlMapping("shortUrl", "http://fullUrl.com");
        repository.save(mapping);
        assertTrue(repository.deleteByShortUrl("shortUrl"));
        assertFalse(repository.existsByShortUrl("shortUrl"));
    }
}