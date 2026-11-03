package com.github.jasonkauan.keymanager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements RotationStrategy {
    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Optional<ApiKey> pick(List<ApiKey> keys, Instant now) {
        int start = Math.floorMod(counter.getAndIncrement(), keys.size());
        for (int i = 0; i < keys.size(); i++) {
            ApiKey k = keys.get((start + i) % keys.size());
            if (k.isAvailable(now)) return Optional.of(k);
        }
        return Optional.empty();
    }
}