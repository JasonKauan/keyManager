package com.github.jasonkauan.keymanager;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

public class KeyManager {
    private final KeyPool pool;
    private final LlmProvider provider;

    public KeyManager(KeyPool pool, LlmProvider provider) {
        this.pool = pool;
        this.provider = provider;
    }

    public <T> T execute(Function<ApiKey, T> call) {
        for (int i = 0; i < pool.size(); i++) {
            ApiKey key = pool.acquire();
            try {
                return call.apply(key);
            } catch (RuntimeException e) {
                if (provider.isInvalidKey(e)) {
                    pool.markInvalid(key);
                    continue;
                }
                Optional<Duration> cooldown = provider.asRateLimit(e);
                if (cooldown.isEmpty()) throw e;
                pool.markCooldown(key, cooldown.get());
            }
        }
        return call.apply(pool.acquire()); // esgotou as tentativas: lança NoKeyAvailableException
    }
}