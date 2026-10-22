package com.example.usermanagement.exception;

import com.example.usermanagement.dtos.common.ApiError;
import com.example.usermanagement.dtos.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(ex.getStatus()));
    }

    @ExceptionHandler(NoAuthenticationError.class)
    public ResponseEntity<ApiResponse<Void>> handlerNoAuthenticationError(NoAuthenticationError ex){
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.error(ApiError.of(ex.getStatus().toString(), null, ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ApiError.of(e)));
    }
}
