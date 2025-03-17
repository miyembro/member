package com.rjproj.memberapp.organization;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Organization(
        String organizationId,
        String name,
        String description,
        OrganizationAddress organizationAddress,
        Instant createdAt,
        Instant updatedAt
) {
}
