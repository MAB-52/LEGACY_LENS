package com.legacy_lens.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int statusCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private List<String> errors;
    private Integer remainingTokens;
    private Long tokenResetTime;

    // Constructor
    public ApiResponse(boolean success, String message, T data,
                       int statusCode, List<String> errors,
                       Integer remainingTokens, Long tokenResetTime) {

        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;

        //  ALWAYS set timestamp
        //this.timestamp = LocalDateTime.now();
        //UTC CONSISTENCY
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);

        this.errors = errors;
        this.remainingTokens = remainingTokens;
        this.tokenResetTime = tokenResetTime;
    }

    //  Success
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true, message, data,
                200, null, null, null
        );
    }

    //  Success with tokens
    public static <T> ApiResponse<T> successWithTokens(
            String message, T data,
            Integer remainingTokens, Long resetTime) {

        return new ApiResponse<>(
                true, message, data,
                200, null,
                remainingTokens, resetTime
        );
    }

    //  Error
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
                false, message, null,
                400, List.of(message),
                null, null
        );
    }

    //  Validation Error
    public static <T> ApiResponse<T> validationError(List<String> errors) {
        return new ApiResponse<>(
                false, "Validation failed", null,
                422, errors,
                null, null
        );
    }

    // Getters

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public int getStatusCode() { return statusCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<String> getErrors() { return errors; }
    public Integer getRemainingTokens() { return remainingTokens; }
    public Long getTokenResetTime() { return tokenResetTime; }

    //Setters

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setData(T data) {
        this.data = data;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    public void setRemainingTokens(Integer remainingTokens) {
        this.remainingTokens = remainingTokens;
    }
    public void setTokenResetTime(Long tokenResetTime) {
        this.tokenResetTime = tokenResetTime;
    }

}