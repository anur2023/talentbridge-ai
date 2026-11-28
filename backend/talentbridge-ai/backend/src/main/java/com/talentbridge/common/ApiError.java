package com.talentbridge.common;

import java.util.List;

/**
 * code: machine-readable error code (e.g. VALIDATION_ERROR, REFERRAL_CONSENT_MISSING)
 * message: human-readable, safe to show to the candidate
 * fieldErrors: per-field validation problems, if any
 * correlationId: duplicated here for clients that unwrap only the error object
 */
public class ApiError {

    private String code;
    private String message;
    private List<FieldError> fieldErrors;
    private String correlationId;

    public ApiError() {
    }

    public ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiError(String code, String message, List<FieldError> fieldErrors) {
        this.code = code;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public record FieldError(String field, String message) {
    }
}
