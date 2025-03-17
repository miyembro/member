package com.rjproj.memberapp.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JoinOrganizationRequest(
        @NotNull(message = "Organization is required")
        UUID organizationId,

        @NotNull(message = "Member is required")
        UUID memberId
) {
}
