package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MembershipType;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipRequest (

        UUID membershipId,

        @NotNull(message = "Organization is required")
        UUID organizationId,

        @NotNull(message = "Member is required")
//        UUID memberId,
        Member member,

//        UUID membershipTypeId,
        MembershipType membershipType,

        MembershipStatusRequest membershipStatus,

        RoleRequest role,

        Timestamp startDate,

        Timestamp endDate
) {
}
