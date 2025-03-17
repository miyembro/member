package com.rjproj.memberapp.dto;

public record LoginRequest(
        String email,
        String password
) {
}
