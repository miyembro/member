package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public record MemberResponse (
        UUID memberId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String profilePicUrl,

        LocalDate birthDate,
        LoginType loginType,
        MemberAddressResponse memberAddress,
        Timestamp createdAt
) {
}
