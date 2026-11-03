package com.github.jasonkauan.keymanager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RotationStrategy {
    Optional<ApiKey> pick(List<ApiKey> keys, Instant now);
}