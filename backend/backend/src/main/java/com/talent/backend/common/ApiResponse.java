package com.talent.backend.common;

public record ApiResponse<T>(T data, PageMeta meta) {

	public static <T> ApiResponse<T> of(T data) {
		return new ApiResponse<>(data, null);
	}

	public static <T> ApiResponse<T> page(T data, PageMeta meta) {
		return new ApiResponse<>(data, meta);
	}
}
