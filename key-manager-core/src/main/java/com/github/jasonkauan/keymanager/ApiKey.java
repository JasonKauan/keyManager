package com.github.jasonkauan.keymanager;

import java.time.Instant;

public final class ApiKey {
    private final String id;
    private final String value;
    private volatile KeyStatus status = KeyStatus.ACTIVE;
    private volatile Instant availableAt = Instant.EPOCH;

    public ApiKey(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String id() { return id; }
    public String value() { return value; }
    public KeyStatus status() { return status; }
    public Instant availableAt() { return availableAt; }

    public boolean isAvailable(Instant now) {
        return status != KeyStatus.DISABLED && !now.isBefore(availableAt);
    }

    void cooldownUntil(Instant t) { this.status = KeyStatus.COOLDOWN; this.availableAt = t; }
    void disable() { this.status = KeyStatus.DISABLED; }

    @Override
    public String toString() { return "ApiKey[" + id + ", " + status + "]"; }
}