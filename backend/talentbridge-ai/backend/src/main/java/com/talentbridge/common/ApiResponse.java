package com.talentbridge.common;

/**
 * Standard API envelope. On success, `data` is populated and `error` is null.
 * On failure, `data` is null and `error` carries the ApiError.
 */
public class ApiResponse<T> {

    private T data;
    private ApiError error;
    private String correlationId;

    public static <T> ApiResponse<T> ok(T data, String correlationId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        response.correlationId = correlationId;
        return response;
    }

    public static <T> ApiResponse<T> error(ApiError error, String correlationId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.error = error;
        response.correlationId = correlationId;
        return response;
    }

    public T getData() {
        return data;
    }

    public ApiError getError() {
        return error;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
