package com.example.usermanagement.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppException extends RuntimeException {
    private IErrorMessage status;

    public AppException(IErrorMessage message) {
        super(message.getErrorCode());
        this.status = message;
    }
}
