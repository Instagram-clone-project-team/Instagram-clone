package com.project.Instagram.global.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse(final ErrorCode errorCode){
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
