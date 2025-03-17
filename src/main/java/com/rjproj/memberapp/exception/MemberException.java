package com.rjproj.memberapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class MemberException extends RuntimeException {

    private final String message;

    private final String errorMessage;

    private final HttpStatus httpStatus;
}