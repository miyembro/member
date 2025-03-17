package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.mapper.MembershipStatusMapper;
import com.rjproj.memberapp.model.*;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.repository.*;
import com.rjproj.memberapp.security.JWTUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    @Autowired
    JWTUtil jwtUtil;

    private final MemberRepository memberRepository;

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    private final MembershipMapper membershipMapper;

    private final MembershipRepository membershipRepository;

    private final MembershipStatusMapper membershipStatusMapper;

    private final MembershipStatusRepository membershipStatusRepository;

    private final MembershipTypeRepository membershipTypeRepository;

    private final OrganizationClient organizationClient;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    public MembershipResponse approveMembershipRequest(UUID organizationId, UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Optional<Membership> membershipOpt = membershipRepository.findByMembershipIdAndOrganizationId(membershipId, organizationId);

        if(membershipOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching memberships found for the provided organization ID");
        }

        Membership membership = membershipOpt.get();

        membership.setMembershipType(membershipRequest.membershipType());

        setMembershipStatusByStatusName(membership, membershipRequest.membershipType(), "Active");

        Role role = roleService.getRoleByName("Member");

        Member member = getMemberById(membership.getMember().getMemberId());

        Optional<MemberRole> existingMemberRoleOpt = memberRoleRepository.findByMemberIdAndOrganizationId(membership.getMember().getMemberId(), membershipRequest.organizationId());

        if(existingMemberRoleOpt.isEmpty()) {

            MemberRoleId memberRoleId = new MemberRoleId();
            memberRoleId.setMemberId(member.getMemberId());
            memberRoleId.setRoleId(role.getRoleId());
            memberRoleId.setOrganizationId(membershipRequest.organizationId());

            MemberRole newMemberRole = MemberRole.builder().id(memberRoleId).member(member).role(role).build();

            memberRoleRepository.save(newMemberRole);
        } else {

            MemberRole existingMemberRole = existingMemberRoleOpt.get();

            memberRoleRepository.updateMemberRole(
                    existingMemberRole.getId().getMemberId(),
                    existingMemberRole.getId().getOrganizationId(),
                    role.getRoleId()
            );
        }

        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    public List<MembershipResponse> approveMembershipRequests(UUID organizationId, @Valid ApproveMembershipsRequest approveMembershipsRequest) {
        List<MembershipRequest> membershipRequests = approveMembershipsRequest.membershipRequests();

        List<MembershipResponse> approvedMemberships = new ArrayList<>();

        membershipRequests.forEach(membershipRequest -> {

            Membership membership = getMembershipById(membershipRequest.membershipId());

            membership.setMembershipType(approveMembershipsRequest.membershipType());

            setMembershipStatusByStatusName(membership, membership.getMembershipType(), "Active");

            Role role = roleService.getRoleByName("Member");

            Member member = getMemberById(membership.getMember().getMemberId());

            Optional<MemberRole> existingMemberRoleOpt = memberRoleRepository.findByMemberIdAndOrganizationId(
                    member.getMemberId(),
                    organizationId
            );

            existingMemberRoleOpt.ifPresentOrElse(
                    existingMemberRole -> memberRoleRepository.updateMemberRole(
                            existingMemberRole.getId().getMemberId(),
                            existingMemberRole.getId().getOrganizationId(),
                            role.getRoleId()
                    ),
                    () -> {
                        MemberRoleId memberRoleId = new MemberRoleId();
                        memberRoleId.setMemberId(member.getMemberId());
                        memberRoleId.setRoleId(role.getRoleId());
                        memberRoleId.setOrganizationId(organizationId);

                        MemberRole newMemberRole = MemberRole.builder()
                                .id(memberRoleId)
                                .member(member)
                                .role(role)
                                .build();

                        memberRoleRepository.save(newMemberRole);
                    }
            );

            approvedMemberships.add(membershipMapper.fromMembership(membershipRepository.save(membership)));
        });

        return approvedMemberships;
    }


    public MembershipResponse createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest) {
        UUID memberId = jwtUtil.extractMemberIdInternally();
        return createMembershipByOrganizationIdAndMemberId(createMembershipRequest.organizationId(), memberId, createMembershipRequest.membershipTypeId());
    }

    public UUID deleteMembershipFromOrganization(UUID organizationId, UUID membershipId) {

        Optional<Membership> membershipOpt = membershipRepository.findByMembershipIdAndOrganizationId(membershipId, organizationId);

        if(membershipOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching memberships found for the provided organization ID");
        }

        Membership membership = membershipOpt.get();
        Member member = membership.getMember();

        membershipRepository.delete(membership);

        //delete role of the member from organization
        Optional<MemberRole> memberRole = memberRoleRepository.findByMemberIdAndOrganizationId(member.getMemberId(), organizationId);

        if(memberRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role for the member not found");
        }

        memberRoleRepository.delete(memberRole.get());

        return member.getMemberId();
    }

    @Transactional
    public List<UUID> deleteMembershipsFromOrganization(UUID organizationId, @Valid List<MembershipRequest> membershipRequests) {
        List<UUID> membershipIdsToDelete = membershipRequests.stream()
                .map(MembershipRequest::membershipId)
                .collect(Collectors.toList());

        List<Membership> membershipsToDelete = membershipRepository.findByMembershipIdInAndOrganizationId(membershipIdsToDelete, organizationId);

        if (membershipsToDelete.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching memberships found for the provided organization ID");
        }

        membershipRepository.deleteAll(membershipsToDelete);

        return membershipsToDelete.stream()
                .map(Membership::getMembershipId)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UUID> deleteMembershipRequests(UUID organizationId, @Valid List<MembershipRequest> membershipRequests) {
        return deleteMembershipsFromOrganization(organizationId, membershipRequests);
    }

    public MembershipResponse denyMembershipRequest(UUID organizationId, UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Optional<Membership> membershipOpt = membershipRepository.findByMembershipIdAndOrganizationId(membershipId, organizationId);

        if(membershipOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching memberships found for the provided organization ID");
        }

        Membership membership = membershipOpt.get();

        setMembershipStatusByStatusName(membership, membershipRequest.membershipType(), "Denied");

        return membershipMapper.fromMembership(membershipRepository.save(membership));

    }

    public List<UUID> denyMembershipRequests(UUID organizationId, @Valid DenyMembershipsRequest denyMembershipsRequest) {
        List<UUID> deniedMembershipIds = new ArrayList<>();

        denyMembershipsRequest.membershipRequests().forEach(membershipRequest -> {
            try {
                MembershipResponse deniedMembership = denyMembershipRequest(
                        organizationId,
                        membershipRequest.membershipId(),
                        membershipRequest
                );
                deniedMembershipIds.add(deniedMembership.membershipId()); // Add successful denied membership ID
            } catch (ResponseStatusException ex) {
                System.out.println("Failed to deny membership ID: " + membershipRequest.membershipId() + " - " + ex.getReason());
            }
        });
        return deniedMembershipIds;
    }


    public List<UUID> getActiveOrganizationIdsByMemberId(UUID memberId) {
        return membershipRepository.findActiveOrganizationIdsByMemberId(memberId);
    }

    public Membership getMembership(UUID memberId, UUID organizationId) {
        return membershipRepository.findMembershipByMemberIdAndOrganizationId(memberId, organizationId);
    }

    private Membership getMembershipById(UUID membershipId) {
        if(membershipId != null) {
            Optional<Membership> membershipOpt = membershipRepository.findByMembershipId(membershipId);

            if(membershipOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Membership not found with ID: " + membershipId);
            }

            return membershipOpt.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Membership Id cannot be null");
    }

    public List<MembershipResponse> getMembershipsByMemberId(UUID memberId) {
        List<Membership> memberships = membershipRepository.findMembershipsByMemberId(memberId);
        return memberships.stream().map(membershipMapper::fromMembership).collect(Collectors.toList());
    }

    public MembershipResponse getMembershipByMemberIdAndOrganizationId(UUID memberId, UUID organizationId) {
        Membership membership = membershipRepository.findMembershipByMemberIdAndOrganizationId(memberId, organizationId);
        return membershipMapper.fromMembership(membership);
    }

    public Page<MembershipResponse> getMembershipsByOrganization(
            UUID organizationId,
            Integer pageNo,
            Integer pageSize,
            String sortField,
            String sortOrder,
            MembershipFilters membershipFilters) {

        OrganizationResponse organization = organizationClient.getOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort sort = Sort.unsorted();

        // Check if the sortField is 'role.name' and set the appropriate sorting
        if ("role.name".equals(sortField)) {
            sort = Sort.by(Sort.Order.by("role.name").with(Sort.Direction.fromString(sortOrder)));
        } else {
            sort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));
        }

        // Fix for problem: Sorting secondary criteria (to prevent duplicates on pages)
        Sort secondarySort = Sort.by(Sort.Order.by(sortField.equals("member.firstName") ? "member.lastName" : "membershipId").with(Sort.Direction.fromString(sortOrder)));
        Sort combinedSort = sort.and(secondarySort);

        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        Specification<Membership> spec = Specification.where(MembershipSpecification.hasOrganizationIdAndMembershipTypeNotNull(organizationId));

        spec = spec.and(MembershipSpecification.filterByFirstName(membershipFilters.memberFirstName()));
        spec = spec.and(MembershipSpecification.filterByEmail(membershipFilters.memberEmail()));
        spec = spec.and(MembershipSpecification.filterByCity(membershipFilters.memberMemberAddressCity()));
        spec = spec.and(MembershipSpecification.filterByCountry(membershipFilters.memberMemberAddressCountry()));
        spec = spec.and(MembershipSpecification.filterByMembershipStatus(membershipFilters.membershipStatusNames()));
        spec = spec.and(MembershipSpecification.filterByMembershipTypes(membershipFilters.membershipTypeNames()));
        spec = spec.and(MembershipSpecification.filterByRoleNames(membershipFilters.roleNames(), organizationId));

        if (membershipFilters.startDates() != null && !membershipFilters.startDates().isEmpty()) {
            Date startDateFrom = membershipFilters.startDates().get(0);
            Date startDateTo = membershipFilters.startDates().get(1);
            spec = spec.and(MembershipSpecification.filterByStartDateRange(startDateFrom, startDateTo));
        }

        if (membershipFilters.endDates() != null && !membershipFilters.endDates().isEmpty()) {
            Date endDateFrom = membershipFilters.endDates().get(0);
            Date endDateTo = membershipFilters.endDates().get(1);
            spec = spec.and(MembershipSpecification.filterByEndDateRange(endDateFrom, endDateTo));
        }

        spec = spec.and(MembershipSpecification.applySorting(combinedSort, organizationId));

        Page<Membership> membershipPage = membershipRepository.findAll(spec, pageable);


        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membership -> {
                    System.out.println("Organization Id: " + organizationId + " Member Id: " + membership.getMember().getMemberId());
                    Role memberRole = memberRoleRepository.findRoleByMemberAndOrganization(membership.getMember().getMemberId(), organizationId);
                    return  membershipMapper.fromMembershipWithRole(membership, memberRole);
                })
                .collect(Collectors.toList());


        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
    }

    public Page<MembershipResponse> getRequestsMembershipsByOrganization(
            UUID organizationId,
            Integer pageNo,
            Integer pageSize,
            String sortField,
            String sortOrder,
            MembershipFilters membershipFilters) {

        OrganizationResponse organization = organizationClient.getOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort firstSort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));

        // Fix for problem: There are items that shows on different pages. Because of same ex. name.
        Sort secondSort = Sort.by(Sort.Order.by(sortField.equals("member.firstName") ? "member.lastName" : "membershipId").with(Sort.Direction.fromString(sortOrder)));

        Sort combinedSort = firstSort.and(secondSort);

        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        Specification<Membership> spec = Specification.where(MembershipSpecification.hasOrganizationId(organizationId));
        spec = spec.and(MembershipSpecification.hadMembershipTypePending());
        spec = spec.or(MembershipSpecification.hadMembershipTypeDenied());
        spec = spec.and(MembershipSpecification.filterByFirstName(membershipFilters.memberFirstName()));
        spec = spec.and(MembershipSpecification.filterByEmail(membershipFilters.memberEmail()));
        spec = spec.and(MembershipSpecification.filterByCity(membershipFilters.memberMemberAddressCity()));
        spec = spec.and(MembershipSpecification.filterByCountry(membershipFilters.memberMemberAddressCountry()));
        spec = spec.and(MembershipSpecification.filterByMembershipStatus(membershipFilters.membershipStatusNames()));

        Page<Membership> membershipPage = membershipRepository.findAll(spec, pageable);

        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membershipMapper::fromMembership)
                .collect(Collectors.toList());

        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
    }

    public MembershipResponse requestMembership(@Valid JoinOrganizationRequest joinOrganizationRequest) {
        Member member = memberRepository.findById(joinOrganizationRequest.memberId())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with ID:: " + joinOrganizationRequest.memberId()));
        OrganizationResponse organizationResponse = this.organizationClient.getOrganizationById(joinOrganizationRequest.organizationId());
        if(organizationResponse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Organization not found with ID:: " + joinOrganizationRequest.organizationId());
        }
        MembershipStatus membershipStatus = membershipStatusRepository.findByName("Pending");
        MembershipRequest membershipRequest = new MembershipRequest(
                null,
                joinOrganizationRequest.organizationId(),
                member,
                null,
                membershipStatusMapper.fromMembershipStatusToMembershipStatusRequest(membershipStatus),
                null,
                null,
                null
        );
        return createMembership(membershipRequest);
    }

    //because inside is update member role which is transactional
    @Transactional
    public MembershipResponse updateMembershipFromOrganization(UUID organizationId, UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Optional<Membership> membershipOptional = membershipRepository.findByMembershipIdAndOrganizationId(membershipId, organizationId);
        if (membershipOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership " + membershipId + "not found in organization " + organizationId);
        }
        Membership membership = membershipOptional.get();

        Optional<Role> roleOptional = roleRepository.findById(membershipRequest.role().roleId());
        if (roleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with ID: " + membershipRequest.role().roleId());
        }
        Role role = roleOptional.get();

        Optional<MemberRole> memberRoleOptional = memberRoleRepository.findByMemberIdAndOrganizationId(
                membershipRequest.member().getMemberId(), organizationId);

        if (memberRoleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MemberRole not found for memberId: "
                    + membershipRequest.member().getMemberId() + " and organizationId: " + organizationId);
        }

        memberRoleRepository.updateMemberRole(
                memberRoleOptional.get().getId().getMemberId(),
                memberRoleOptional.get().getId().getOrganizationId(),
                role.getRoleId()
        );

        Optional<MemberRole> updatedMemberRole = memberRoleRepository.findByMemberIdAndOrganizationId(
                membershipRequest.member().getMemberId(), membershipRequest.organizationId());

        if (updatedMemberRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update MemberRole.");
        }

        mergeMembership(membership, membershipRequest);
        membershipRepository.save(membership);

        return membershipMapper.fromMembershipWithRole(membership, updatedMemberRole.get().getRole());
    }


    @Transactional
    public List<MembershipResponse> updateMembershipsFromOrganization(UUID organizationId, @Valid UpdateMembershipRequests updateMembershipRequests) {
        List<MembershipResponse> updatedMemberships = new ArrayList<>();

        // Loop through the list of membership requests
        for (MembershipRequest membershipRequest : updateMembershipRequests.membershipRequests()) {
            Optional<Membership> membershipOptional = membershipRepository.findByMembershipIdAndOrganizationId(membershipRequest.membershipId(), organizationId);
            if (membershipOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership " + membershipRequest.membershipId() + "not found in organization " + organizationId);
            }
            Membership membership = membershipOptional.get();

            // Update the membership fields if they are not null
            if (updateMembershipRequests.membershipType() != null) {
                membership.setMembershipType(updateMembershipRequests.membershipType());
            }

            if (updateMembershipRequests.membershipStatus() != null) {
                Optional<MembershipStatus> membershipStatusOptional = membershipStatusRepository.findById(updateMembershipRequests.membershipStatus().membershipStatusId());
                if (membershipStatusOptional.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MembershipStatus not found with ID: " + updateMembershipRequests.membershipStatus().membershipStatusId());
                }
                membership.setMembershipStatus(membershipStatusOptional.get());
            }

            if (updateMembershipRequests.role() != null) {
                Optional<Role> roleOptional = roleRepository.findById(updateMembershipRequests.role().roleId());
                if (roleOptional.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with ID: " + updateMembershipRequests.role().roleId());
                }
                Role role = roleOptional.get();

                Optional<MemberRole> memberRoleOptional = memberRoleRepository.findByMemberIdAndOrganizationId(
                        membershipRequest.member().getMemberId(), updateMembershipRequests.organizationId());

                if (memberRoleOptional.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MemberRole not found for memberId: "
                            + membershipRequest.member().getMemberId() + " and organizationId: " + updateMembershipRequests.organizationId());
                }

                memberRoleRepository.updateMemberRole(
                        memberRoleOptional.get().getId().getMemberId(),
                        memberRoleOptional.get().getId().getOrganizationId(),
                        role.getRoleId()
                );
            }

            if (updateMembershipRequests.startDate() != null) {
                membership.setStartDate(updateMembershipRequests.startDate());
            }

            if (updateMembershipRequests.endDate() != null) {
                membership.setEndDate(updateMembershipRequests.endDate());
            }

            // Save the updated membership
            membershipRepository.save(membership);

            // After updating, map the membership entity to a response
            Optional<MemberRole> updatedMemberRole = memberRoleRepository.findByMemberIdAndOrganizationId(
                    membershipRequest.member().getMemberId(), updateMembershipRequests.organizationId());

            if (updatedMemberRole.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update MemberRole.");
            }

            updatedMemberships.add(membershipMapper.fromMembershipWithRole(membership, updatedMemberRole.get().getRole()));
        }

        return updatedMemberships;
    }



    private MembershipResponse createMembership(@Valid MembershipRequest membershipRequest) {
        Membership membership = membershipMapper.toMembership(membershipRequest);
        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    private MembershipResponse createMembershipByOrganizationIdAndMemberId(UUID organizationId, UUID memberId, UUID membershipTypeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Cannot update member with id %s", memberId)
                ));

        Optional<MembershipType> membershipType = membershipTypeRepository.findById(membershipTypeId);

        MembershipStatus membershipStatus = membershipStatusRepository.findByName("Active");

        Membership membership = Membership.builder()
                .member(member)
                .organizationId(organizationId)
                .membershipType(membershipType.get())
                .membershipStatus(membershipStatus)
                .startDate(new Timestamp(System.currentTimeMillis()))
                .endDate(null)
                .build();

        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    private Timestamp getEndDate(MembershipType membershipType) {
        Timestamp endDate = null;
        if(membershipType.getMembershipTypeValidity().getName().equals("Ends after 1 year")) {
            LocalDateTime oneYearLater = LocalDateTime.now().plusYears(1);
            endDate = Timestamp.valueOf(oneYearLater);
        } else if(membershipType.getMembershipTypeValidity().getName().equals("Ends on January 1")) {
            LocalDateTime januaryFirstNextYearLocal = LocalDateTime.now()
                    .plusYears(1) // Move to next year
                    .withMonth(Month.JANUARY.getValue()) // Set the month to January
                    .withDayOfMonth(1) // Set the day to 1
                    .withHour(0) // Set hour to 00:00
                    .withMinute(0) // Set minute to 00
                    .withSecond(0) // Set second to 00
                    .withNano(0); // Set nanosecond to 0

            endDate = Timestamp.valueOf(januaryFirstNextYearLocal);
        } else {
            endDate = null;
        }
        return endDate;
    }

    private Member getMemberById(UUID memberId) {
        if(memberId != null) {
            Optional<Member> member = memberRepository.findById(memberId);
            if(member.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Member not found with ID: " + memberId);
            }
            return member.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Member Id cannot be null");
    }

    private void mergeMembership(Membership membership, @Valid MembershipRequest membershipRequest) {
        if(membershipRequest.organizationId() != null) {
            membership.setOrganizationId(membershipRequest.organizationId());
        }
        if(membershipRequest.member() != null) {
            membership.setMember(membershipRequest.member());
        }
        if(membershipRequest.membershipType() != null) {
            membership.setMembershipType(membershipRequest.membershipType());
        }

        if(membershipRequest.membershipStatus() != null) {
            membership.setMembershipStatus(membershipStatusMapper.toMembershipStatus(membershipRequest.membershipStatus()));
        }
        if(membershipRequest.startDate() != null) {
            membership.setStartDate(membershipRequest.startDate());
        }
        if(membershipRequest.endDate() != null) {
            membership.setEndDate(membershipRequest.endDate());
        }

    }

    private void setMembershipStatusByStatusName(Membership membership, MembershipType membershipType, String membershipStatusName) {

        if(membershipStatusName != null) {
            MembershipStatus membershipStatus = membershipStatusRepository.findByName(membershipStatusName);

            membership.setMembershipStatus(membershipStatus);
            if(!membershipStatusName.equals("Denied")) {
                membership.setStartDate(Timestamp.from(Instant.now()));
                membership.setEndDate(getEndDate(membershipType));
            }
        } else {
            membership.setMembershipStatus(null);
        }
    }



}
