package com.example.usermanagement.dtos.common;

import com.example.usermanagement.exception.IErrorMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> ok(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ApiError error){
        return ApiResponse.<T>builder()
                .error(error)
                .build();
    }

    public static <T> ApiResponse<T> error(IErrorMessage message){
        return error(ApiError.of(message));
    }
}
