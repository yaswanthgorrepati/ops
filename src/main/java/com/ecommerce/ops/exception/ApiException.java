package com.ecommerce.ops.exception;

public class ApiException extends RuntimeException {

    private final ResponseCode errorCode;

    public ApiException(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ResponseCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResponseCode getErrorCode() {
        return errorCode;
    }
}
