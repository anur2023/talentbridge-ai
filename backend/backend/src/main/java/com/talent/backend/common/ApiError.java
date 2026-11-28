package com.talent.backend.common;

import java.util.List;

public record ApiError(
		String code,
		String message,
		List<FieldErrorDetail> fieldErrors,
		String correlationId
) {
	public ApiError {
		fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
	}
}
