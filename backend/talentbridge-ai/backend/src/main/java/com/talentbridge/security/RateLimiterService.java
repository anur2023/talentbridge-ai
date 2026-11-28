package com.talentbridge.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * In-memory per-key rate limiting (per process). Good enough for a single
 * backend instance pilot deployment; move to a shared store if scaled out.
 */
@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int capacity, Duration window) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(capacity).refillIntervally(capacity, window).build())
                .build());
        return bucket.tryConsume(1);
    }
}
