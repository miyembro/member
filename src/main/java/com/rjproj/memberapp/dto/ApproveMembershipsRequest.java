package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MembershipType;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record ApproveMembershipsRequest(
        MembershipType membershipType,
        UUID organizationId,
        List<MembershipRequest> membershipRequests
) {
}
