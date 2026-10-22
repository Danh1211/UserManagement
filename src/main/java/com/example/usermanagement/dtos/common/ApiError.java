package com.example.usermanagement.dtos.common;

import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.example.usermanagement.exception.IErrorMessage;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String title,
        String message
) {
    public static ApiError of(String code, String title, String message){
        return new ApiError(code, title, message);
    }

    public static ApiError of(IErrorMessage message){
        return new ApiError(message.getErrorCode(), message.getTitle(), message.getMessage());
    }

    public static ApiError of(Throwable ex){
        if(ex instanceof AppException exception){
            return ApiError.of(exception.getStatus());
        }
        return ApiError.of(ErrorMessage.ERR_UNKNOWN);
    }
}
