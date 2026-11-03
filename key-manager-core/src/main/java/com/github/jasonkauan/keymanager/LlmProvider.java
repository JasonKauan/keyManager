package com.github.jasonkauan.keymanager;

import java.time.Duration;
import java.util.Optional;

public interface LlmProvider {
    String name();

    /** Se o erro for rate limit, devolve quanto esperar. Vazio = não é rate limit. */
    Optional<Duration> asRateLimit(Throwable error);

    /** 401/403: key inválida, desativa de vez. */
    default boolean isInvalidKey(Throwable error) { return false; }
}