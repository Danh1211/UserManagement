package com.example.usermanagement.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Getter
@Setter
public class NoAuthenticationError extends RuntimeException{
    private HttpStatus status;
    private String messageVi;

    public NoAuthenticationError(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.messageVi = message;
    }
}
