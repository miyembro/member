package com.rjproj.memberapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.rjproj.memberapp.exception.MemberErrorMessage.MEMBER_EXISTS;

@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<Object> handleMemberException(MemberException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getErrorMessage());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
