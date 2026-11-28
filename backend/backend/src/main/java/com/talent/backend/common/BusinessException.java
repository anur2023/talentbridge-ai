package com.talent.backend.common;

import org.springframework.http.HttpStatus;

import java.util.List;

public class BusinessException extends RuntimeException {

	private final String code;
	private final HttpStatus status;
	private final List<FieldErrorDetail> fieldErrors;

	public BusinessException(String code, HttpStatus status, String message) {
		this(code, status, message, List.of());
	}

	public BusinessException(String code, HttpStatus status, String message, List<FieldErrorDetail> fieldErrors) {
		super(message);
		this.code = code;
		this.status = status;
		this.fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
	}

	public String getCode() {
		return code;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public List<FieldErrorDetail> getFieldErrors() {
		return fieldErrors;
	}
}
