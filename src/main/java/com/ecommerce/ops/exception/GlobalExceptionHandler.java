package com.ecommerce.ops.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.ecommerce.ops.exception.ResponseCode.INTERNAL_ERROR;
import static com.ecommerce.ops.utils.Constants.ERROR_CODE;
import static com.ecommerce.ops.utils.Constants.ERROR_MESSAGE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException apiException) {

        return new ResponseEntity<>(Map.of(ERROR_CODE, apiException.getErrorCode(),
                ERROR_MESSAGE, apiException.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
        return new ResponseEntity<>(Map.of(ERROR_CODE, INTERNAL_ERROR.getCode(),
                ERROR_MESSAGE, exception.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
