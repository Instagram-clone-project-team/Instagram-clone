package com.project.Instagram.global.error;

import lombok.Getter;

public class BusinessException extends RuntimeException{
    @Getter
    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message,ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
