package com.project.Instagram.global.error;

import lombok.Getter;

@Getter
public class ValidationResponse {
    private boolean success;
    private String message;

    public ValidationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
