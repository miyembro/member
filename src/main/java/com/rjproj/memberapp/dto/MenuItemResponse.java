package com.rjproj.memberapp.dto;

public record MenuItemResponse(
        String labelKey,
        String label,
        String route
) {
}
