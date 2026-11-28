package com.talent.backend.config;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitService {

	private final Map<String, Window> windows = new ConcurrentHashMap<>();

	public boolean allow(String key, int limit, Duration period) {
		long now = System.nanoTime();
		long window = period.toNanos();
		Window current = windows.compute(key, (ignored, existing) -> {
			if (existing == null || now - existing.start >= window) {
				return new Window(now, 1);
			}
			existing.count++;
			return existing;
		});
		return current.count <= limit;
	}

	private static final class Window {
		private final long start;
		private int count;

		private Window(long start, int count) {
			this.start = start;
			this.count = count;
		}
	}
}
