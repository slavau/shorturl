package api;

import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlRequest;
import com.example.origin.technical.exercise.shorturl.model.CreateShortUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class UrlShorteningApiController implements com.example.origin.technical.exercise.shorturl.api.UrlShorteningApi {

	@Override
	public ResponseEntity<CreateShortUrlResponse> _createShortUrl(CreateShortUrlRequest createShortUrlRequest) {
		return null;
	}
}
