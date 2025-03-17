package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import com.rjproj.memberapp.mapper.MembershipTypeMapper;
import com.rjproj.memberapp.model.MembershipType;
import com.rjproj.memberapp.repository.MembershipTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTypeService {

    private final MembershipTypeMapper membershipTypeMapper;

    private final MembershipTypeRepository membershipTypeRepository;

    public List<MembershipTypeResponse> createMembershipTypes(@Valid List<MembershipTypeRequest> membershipTypeRequests) {
        List<MembershipType> membershipTypes = membershipTypeRequests.stream().map(membershipTypeMapper::toMembershipType).collect(Collectors.toList());
        return membershipTypeRepository.saveAll(membershipTypes)
                .stream()
                .map(membershipTypeMapper::fromMembershipType)
                .collect(Collectors.toList());
    }

    public List<MembershipTypeResponse> getMembershipTypesByOrganizationId(UUID organizationId) {
        List<MembershipType> membershipTypes = membershipTypeRepository.findByOrganizationId(organizationId);
        return membershipTypes.stream().map(membershipTypeMapper::fromMembershipType).collect(Collectors.toList());
    }

}
