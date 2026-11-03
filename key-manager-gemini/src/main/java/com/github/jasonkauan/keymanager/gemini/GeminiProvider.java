package com.github.jasonkauan.keymanager.gemini;

import com.github.jasonkauan.keymanager.LlmProvider;
import org.springframework.web.client.HttpClientErrorException;
import java.time.Duration;
import java.util.Optional;

public class GeminiProvider implements LlmProvider {
    private static final Duration DEFAULT_COOLDOWN = Duration.ofSeconds(65);

    @Override public String name() { return "gemini"; }

    @Override
    public Optional<Duration> asRateLimit(Throwable e) {
        if (e instanceof HttpClientErrorException.TooManyRequests ex) {
            String ra = ex.getResponseHeaders() != null
                    ? ex.getResponseHeaders().getFirst("Retry-After") : null;
            try {
                return Optional.of(ra != null ? Duration.ofSeconds(Long.parseLong(ra)) : DEFAULT_COOLDOWN);
            } catch (NumberFormatException nfe) {
                return Optional.of(DEFAULT_COOLDOWN);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isInvalidKey(Throwable e) {
        return e instanceof HttpClientErrorException ex
                && (ex.getStatusCode().value() == 403
                    || (ex.getStatusCode().value() == 400
                        && ex.getResponseBodyAsString().contains("API_KEY_INVALID")));
    }
}