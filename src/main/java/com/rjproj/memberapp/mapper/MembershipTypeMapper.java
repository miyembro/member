package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import com.rjproj.memberapp.model.MembershipType;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MembershipTypeMapper {

    public MembershipTypeResponse fromMembershipType(MembershipType membershipType) {
        return new MembershipTypeResponse(
                membershipType.getMembershipTypeId(),
                membershipType.getOrganizationId(),
                membershipType.getMembershipTypeValidity(),
                membershipType.getName(),
                membershipType.getDescription(),
                membershipType.getIsDefault()
        );
    }

    public MembershipType toMembershipType(@Valid MembershipTypeRequest membershipTypeRequest) {
        return MembershipType.builder()
                .membershipTypeId(membershipTypeRequest.membershipTypeId())
                .organizationId(membershipTypeRequest.organizationId())
                .membershipTypeValidity(membershipTypeRequest.membershipTypeValidity())
                .name(membershipTypeRequest.name())
                .description(membershipTypeRequest.description())
                .isDefault(membershipTypeRequest.isDefault())
                .build();
    }
}
