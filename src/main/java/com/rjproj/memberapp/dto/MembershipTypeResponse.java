package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MembershipTypeValidity;

import java.util.UUID;

public record MembershipTypeResponse (
        UUID membershipTypeId,
        UUID organizationId,
        MembershipTypeValidity membershipTypeValidity,
        String name,
        String description,
        Boolean isDefault
) {
}
