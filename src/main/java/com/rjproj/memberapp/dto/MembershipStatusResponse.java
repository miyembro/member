package com.rjproj.memberapp.dto;

import java.util.UUID;

public record MembershipStatusResponse(
        UUID membershipStatusId,

        String name,

        String description
) {
}
