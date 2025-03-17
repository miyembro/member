package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.organization.OrganizationResponse;

import java.util.List;
import java.util.UUID;

public record Session(
        String accessToken,
        String tokenType,
        MemberResponse member,
        RoleResponse role,
        List<String> permissions,
        OrganizationResponse organization,
        List<UUID> organizationIdsOfMember,
        MembershipResponse membership
) {
}
