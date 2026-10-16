package com.example.usermanagement.config;

import com.example.usermanagement.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "errorCode", ex.getStatus().getErrorCode(),
                "title", ex.getStatus().getTitle(),
                "message", ex.getStatus().getMessage()
        ));
    }
}
