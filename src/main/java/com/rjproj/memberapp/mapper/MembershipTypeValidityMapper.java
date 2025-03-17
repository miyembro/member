package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MembershipTypeValidityRequest;
import com.rjproj.memberapp.dto.MembershipTypeValidityResponse;
import com.rjproj.memberapp.model.MembershipTypeValidity;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MembershipTypeValidityMapper {

    public MembershipTypeValidityResponse fromMembershipTypeValidity(MembershipTypeValidity membershipTypeValidity) {
        return new MembershipTypeValidityResponse(
                membershipTypeValidity.getMembershipTypeValidityId(),
                membershipTypeValidity.getName(),
                membershipTypeValidity.getDuration(),
                membershipTypeValidity.getDescription()
        );
    }

    public MembershipTypeValidity toMembershipTypeValidity(@Valid MembershipTypeValidityRequest membershipTypeValidityRequest) {
        return MembershipTypeValidity.builder()
                .membershipTypeValidityId(membershipTypeValidityRequest.membershipTypeValidityId())
                .name(membershipTypeValidityRequest.name())
                .duration(membershipTypeValidityRequest.duration())
                .description(membershipTypeValidityRequest.description())
                .createdAt(membershipTypeValidityRequest.createdAt())
                .updatedAt(membershipTypeValidityRequest.updatedAt())
                .build();
    }

}
