package com.github.jasonkauan.keymanager;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class KeyPool {
    private final List<ApiKey> keys;
    private final RotationStrategy strategy;
    private final Clock clock;

    public KeyPool(List<ApiKey> keys, RotationStrategy strategy, Clock clock) {
        if (keys.isEmpty()) throw new IllegalArgumentException("Pool sem keys");
        this.keys = List.copyOf(keys);
        this.strategy = strategy;
        this.clock = clock;
    }

    public int size() { return keys.size(); }

    public ApiKey acquire() {
        Instant now = clock.instant();
        return strategy.pick(keys, now).orElseThrow(() -> new NoKeyAvailableException(earliestAvailable()));
    }

    public void markCooldown(ApiKey key, Duration d) { key.cooldownUntil(clock.instant().plus(d)); }
    public void markInvalid(ApiKey key) { key.disable(); }

    private Instant earliestAvailable() {
        return keys.stream()
                .filter(k -> k.status() != KeyStatus.DISABLED)
                .map(ApiKey::availableAt)
                .min(Comparator.naturalOrder())
                .orElse(Instant.MAX); // todas desativadas
    }
}