package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.model.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipMapper {

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    MembershipStatusMapper membershipStatusMapper;

    @Autowired
    RoleMapper roleMapper;

    public MembershipResponse fromMembership(Membership membership) {
        return new MembershipResponse(
                membership.getMembershipId(),
                membership.getOrganizationId(),
                memberMapper.fromMember(membership.getMember()),
                membership.getMembershipType(),
                membershipStatusMapper.fromMembershipStatus(membership.getMembershipStatus()),
                null,
                membership.getStartDate(),
                membership.getEndDate()
        );
    }

    public MembershipResponse fromMembershipWithRole(Membership membership, Role role) {
        return new MembershipResponse(
                membership.getMembershipId(),
                membership.getOrganizationId(),
                memberMapper.fromMember(membership.getMember()),
                membership.getMembershipType(),
                membershipStatusMapper.fromMembershipStatus(membership.getMembershipStatus()),
                roleMapper.fromRole(role),
                membership.getStartDate(),
                membership.getEndDate()
        );
    }

    public Membership toMembership(@Valid MembershipRequest membershipRequest) {
        return Membership.builder()
                .membershipId(membershipRequest.membershipId())
                .organizationId(membershipRequest.organizationId())
                .member(membershipRequest.member())
                .membershipType(membershipRequest.membershipType())
                .membershipStatus(membershipStatusMapper.toMembershipStatus(membershipRequest.membershipStatus()))
                .startDate(membershipRequest.startDate())
                .endDate(membershipRequest.startDate())
                .build();
    }
}
