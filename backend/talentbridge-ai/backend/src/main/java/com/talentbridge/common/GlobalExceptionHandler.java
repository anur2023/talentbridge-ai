package com.talentbridge.common;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts exceptions into the ApiResponse/ApiError envelope. Never leaks
 * stack traces or internal messages to the client; logs the detail instead.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        String correlationId = CorrelationIdFilter.current();
        log.warn("API exception [{}] code={} msg={}", correlationId, ex.getCode(), ex.getMessage());
        ApiError error = new ApiError(ex.getCode(), ex.getMessage());
        error.setCorrelationId(correlationId);
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(error, correlationId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String correlationId = CorrelationIdFilter.current();
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), safeMessage(fe)))
                .toList();
        ApiError error = new ApiError("VALIDATION_ERROR", "One or more fields are invalid.", fieldErrors);
        error.setCorrelationId(correlationId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error, correlationId));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        String correlationId = CorrelationIdFilter.current();
        ApiError error = new ApiError("ACCESS_DENIED", "You do not have permission to do this.");
        error.setCorrelationId(correlationId);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(error, correlationId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        String correlationId = CorrelationIdFilter.current();
        log.error("Unexpected error [{}]", correlationId, ex);
        ApiError error = new ApiError("INTERNAL_ERROR", "Something went wrong. Please try again.");
        error.setCorrelationId(correlationId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(error, correlationId));
    }

    private String safeMessage(FieldError fe) {
        return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value";
    }
}
