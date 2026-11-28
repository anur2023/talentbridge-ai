package com.talent.backend.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequests {

	private PageRequests() {
	}

	public static Pageable of(int page, int size, int maxSize, Sort sort) {
		int safePage = Math.max(page, 0);
		int safeSize = size <= 0 ? 20 : Math.min(size, maxSize);
		return PageRequest.of(safePage, safeSize, sort == null ? Sort.unsorted() : sort);
	}
}
