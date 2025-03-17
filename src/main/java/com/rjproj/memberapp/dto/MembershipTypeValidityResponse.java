package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipTypeValidityResponse (
        UUID membershipTypeValidityId,
        String name,
        Integer duration,
        String description
) {
}
