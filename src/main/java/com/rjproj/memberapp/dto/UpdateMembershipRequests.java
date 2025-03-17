package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MembershipType;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record UpdateMembershipRequests (
    MembershipType membershipType,
    MembershipStatusRequest membershipStatus,
    RoleRequest role,
    Timestamp startDate,
    Timestamp endDate,
    UUID organizationId,
    List<MembershipRequest> membershipRequests
) {
}
