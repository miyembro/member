package com.rjproj.memberapp.dto;

public record MemberAddressResponse(
        String street,
        String city,
        String provinceState,
        String postalCode,
        String country
) {
}
