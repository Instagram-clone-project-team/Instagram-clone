package com.project.Instagram.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e){
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ValidationResponse> validException(MethodArgumentNotValidException e) {
        ValidationResponse validationResponse = new ValidationResponse(false,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<ValidationResponse> PathVariableValidException(ConstraintViolationException e) {
        String exceptionMessage = e.getMessage();
        int lastIndex = exceptionMessage.lastIndexOf(":");
        String errorMessage = lastIndex >= 0 ? exceptionMessage.substring(lastIndex + 1).trim() : "예상과 다른 오류입니다.";
        ValidationResponse validationResponse = new ValidationResponse(false, errorMessage);
        return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    protected ResponseEntity<ValidationResponse> handleMissingParams(MissingServletRequestParameterException e) {
        ValidationResponse validationResponse = new ValidationResponse(false,
                "Params의 값을 입력 해주세요.");
        return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
    }
}
