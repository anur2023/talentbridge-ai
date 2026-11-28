package com.talentbridge.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** Caps page size at 50 per the public API contract (FR-PUB) and keeps paging consistent. */
public final class PageRequestUtil {

    public static final int MAX_SIZE = 50;
    public static final int DEFAULT_SIZE = 20;

    private PageRequestUtil() {
    }

    public static PageRequest of(Integer page, Integer size) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return PageRequest.of(p, s);
    }

    public static PageRequest of(Integer page, Integer size, Sort sort) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return PageRequest.of(p, s, sort);
    }
}
