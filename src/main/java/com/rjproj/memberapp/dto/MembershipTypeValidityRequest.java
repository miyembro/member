package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipTypeValidityRequest(
        UUID membershipTypeValidityId,
        String name,
        Integer duration,
        String description,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
