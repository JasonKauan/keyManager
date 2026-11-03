package com.github.jasonkauan.keymanager;

import java.time.Instant;

public class NoKeyAvailableException extends RuntimeException {
    private final Instant retryAt;

    public NoKeyAvailableException(Instant retryAt) {
        super("Nenhuma key disponível. Próxima libera em " + retryAt);
        this.retryAt = retryAt;
    }

    public Instant retryAt() { return retryAt; }
}