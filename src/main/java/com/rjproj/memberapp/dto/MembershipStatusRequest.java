package com.rjproj.memberapp.dto;

import java.util.UUID;

public record MembershipStatusRequest(
        UUID membershipStatusId,

        String name,

        String description
) {
}
