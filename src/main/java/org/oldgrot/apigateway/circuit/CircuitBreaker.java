package org.oldgrot.apigateway.circuit;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    private final int failureThreshold;
    private final Duration retryTimeout;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile State state = State.CLOSED;
    private volatile Instant lastFailureTime = Instant.MIN;

    public CircuitBreaker(int failureThreshold, Duration retryTimeout) {
        this.failureThreshold = failureThreshold;
        this.retryTimeout = retryTimeout;
    }

    public <T> Mono<T> execute(Mono<T> action, Mono<T> fallback) {
        return Mono.defer(() -> {
            if (state == State.OPEN) {
                if (Instant.now().isAfter(lastFailureTime.plus(retryTimeout))) {
                    state = State.HALF_OPEN;
                } else {
                    return fallback;
                }
            }

            return action
                    .doOnSuccess(result -> reset())
                    .onErrorResume(ex -> {
                        recordFailure();
                        return fallback;
                    });
        });
    }

    private void recordFailure() {
        failureCount.incrementAndGet();
        lastFailureTime = Instant.now();
        if (failureCount.get() >= failureThreshold) {
            state = State.OPEN;
        }
    }

    private void reset() {
        failureCount.set(0);
        state = State.CLOSED;
    }

    private enum State {
        CLOSED, OPEN, HALF_OPEN
    }
}
