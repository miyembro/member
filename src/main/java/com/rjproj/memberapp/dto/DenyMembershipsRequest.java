package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MembershipType;

import java.util.List;
import java.util.UUID;

public record DenyMembershipsRequest(
        UUID organizationId,
        List<MembershipRequest> membershipRequests
) {
}
