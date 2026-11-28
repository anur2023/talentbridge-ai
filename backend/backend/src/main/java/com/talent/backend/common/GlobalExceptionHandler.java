package com.talent.backend.common;

import org.slf4j.MDC;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
		return error(ex.getStatus(), ex.getCode(), ex.getMessage(), ex.getFieldErrors());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		List<FieldErrorDetail> fields = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
				.toList();
		return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Check the highlighted fields.", fields);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
		return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "The request body could not be read.", List.of());
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public ResponseEntity<ApiError> handleDuplicate(DuplicateKeyException ex) {
		return error(HttpStatus.CONFLICT, "DUPLICATE", "This record already exists.", List.of());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleDenied(AccessDeniedException ex) {
		return error(HttpStatus.FORBIDDEN, "FORBIDDEN", "You do not have access to this resource.", List.of());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiError> handleAuth(AuthenticationException ex) {
		return error(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication is required.", List.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Something went wrong. Try again shortly.", List.of());
	}

	private ResponseEntity<ApiError> error(HttpStatus status, String code, String message, List<FieldErrorDetail> fields) {
		String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
		return ResponseEntity.status(status).body(new ApiError(code, message, fields, correlationId));
	}
}
