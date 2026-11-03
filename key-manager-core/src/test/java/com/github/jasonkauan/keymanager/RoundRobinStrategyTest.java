package com.github.jasonkauan.keymanager;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RoundRobinStrategyTest {
    private final Instant now = Instant.parse("2026-01-01T00:00:00Z");

    private List<ApiKey> keys() {
        return List.of(new ApiKey("a", "1"), new ApiKey("b", "2"), new ApiKey("c", "3"));
    }

    @Test
    void alternaEntreAsKeys() {
        var s = new RoundRobinStrategy();
        var k = keys();
        assertEquals("a", s.pick(k, now).get().id());
        assertEquals("b", s.pick(k, now).get().id());
        assertEquals("c", s.pick(k, now).get().id());
        assertEquals("a", s.pick(k, now).get().id());
    }

    @Test
    void puloKeyEmCooldown() {
        var s = new RoundRobinStrategy();
        var k = keys();
        k.get(0).cooldownUntil(now.plus(Duration.ofMinutes(1)));
        assertEquals("b", s.pick(k, now).get().id());
    }

    @Test
    void todasEmCooldownRetornaVazio() {
        var s = new RoundRobinStrategy();
        var k = keys();
        k.forEach(x -> x.cooldownUntil(now.plus(Duration.ofMinutes(1))));
        assertTrue(s.pick(k, now).isEmpty());
    }
}