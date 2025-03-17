package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MembershipTypeValidity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MembershipTypeRequest(
        UUID membershipTypeId,

        @NotNull(message = "Organization is required")
        UUID organizationId,

        MembershipTypeValidity membershipTypeValidity,

        @NotNull(message = "Name is required")
        String name,

        String description,

        Boolean isDefault

        ) {
}
