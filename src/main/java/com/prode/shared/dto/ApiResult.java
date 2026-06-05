package com.prode.shared.dto;

import java.time.LocalDateTime;

public class ApiResult<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResult() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> response = new ApiResult<>();
        response.success = true;
        response.message = "Operacion exitosa";
        response.data = data;
        return response;
    }

    public static <T> ApiResult<T> ok(String message, T data) {
        ApiResult<T> response = new ApiResult<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> ApiResult<T> error(String message) {
        ApiResult<T> response = new ApiResult<>();
        response.success = false;
        response.message = message;
        return response;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
