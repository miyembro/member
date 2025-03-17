package com.rjproj.memberapp.exception;

public enum MemberErrorMessage {
    UNAUTHORIZED("Unauthorized"),
    ACCESS_DENIED("Access denied"),
    MEMBER_EXISTS("Member already exists"),
    MEMBER_NOT_EXISTS ("Member does not exists"),
    PASSWORD_INCORRECT ("Password is incorrect"),
    SIGN_IN_WITH_GOOGLE ("Sign in with your google account"),
    SIGN_UP_WITH_GOOGLE ("Error signing up with google account");

    private final String message;

    MemberErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
