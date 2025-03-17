package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PutMapping(path = "/organizations/{organizationId}/memberships/{membershipId}/approve")
    public ResponseEntity<MembershipResponse> approveMembershipRequest(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("membershipId") UUID membershipId,
            @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.approveMembershipRequest(organizationId, membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/organizations/{organizationId}/memberships/approve")
    public ResponseEntity<List<MembershipResponse>> approveMembershipRequests(
            @PathVariable("organizationId") UUID organizationId,
            @RequestBody @Valid ApproveMembershipsRequest approveMembershipsRequest){
        return new ResponseEntity<>(
                membershipService.approveMembershipRequests(organizationId, approveMembershipsRequest),
                HttpStatus.ACCEPTED);
    }

    /* Call from other service */
    @PostMapping("/current")
    public ResponseEntity<MembershipResponse> createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated User: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());

        return ResponseEntity.ok(membershipService.createMembershipForCurrentMember(createMembershipRequest));
    }

    @DeleteMapping(path = "/organizations/{organizationId}/memberships/{membershipId}")
    public ResponseEntity<UUID> deleteMembershipFromOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("membershipId") UUID membershipId
    ){
        return new ResponseEntity<>(
                membershipService.deleteMembershipFromOrganization(organizationId, membershipId),
                HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/organizations/{organizationId}/memberships/delete")
    public ResponseEntity<List<UUID>> deleteMembershipsFromOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @RequestBody @Valid List<MembershipRequest> membershipRequests
    ){
        return new ResponseEntity<>(
                membershipService.deleteMembershipsFromOrganization(organizationId, membershipRequests),
                HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/organizations/{organizationId}/memberships/delete-requests")
    public ResponseEntity<List<UUID>> deleteMembershipRequests(
            @PathVariable("organizationId") UUID organizationId,
            @RequestBody @Valid List<MembershipRequest> membershipRequests
    ){
        return new ResponseEntity<>(
                membershipService.deleteMembershipRequests(organizationId, membershipRequests),
                HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/organizations/{organizationId}/memberships/{membershipId}/deny")
    public ResponseEntity<MembershipResponse> denyMembershipRequest(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("membershipId") UUID membershipId,
            @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.denyMembershipRequest(organizationId, membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/organizations/{organizationId}/memberships/deny")
    public ResponseEntity<List<UUID>> denyMembershipRequests(
            @PathVariable("organizationId") UUID organizationId,
            @RequestBody @Valid DenyMembershipsRequest denyMembershipsRequest){
        return new ResponseEntity<>(
                membershipService.denyMembershipRequests(organizationId, denyMembershipsRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByMemberId( @PathVariable("memberId") UUID memberId) {
        return ResponseEntity.ok(membershipService.getMembershipsByMemberId(memberId));
    }

    @GetMapping("/organizations/{organizationId}/members/{memberId}")
    public ResponseEntity<MembershipResponse> getMembershipByMemberIdAndOrganizationId(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("memberId") UUID memberId
    ) {
        return ResponseEntity.ok(membershipService.getMembershipByMemberIdAndOrganizationId(memberId,organizationId));
    }

    @PostMapping("/organizations/{organizationId}/members")
    public ResponseEntity<Page<MembershipResponse>> getMembershipsByOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder,
            @RequestBody(required = false) MembershipFilters membershipFilters) {
        return ResponseEntity.ok(membershipService.getMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder, membershipFilters));
    }

    @PostMapping("/organizations/{organizationId}/members/requests")
    public ResponseEntity<Page<MembershipResponse>> getRequestsMembershipsByOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder,
            @RequestBody(required = false) MembershipFilters membershipFilters) {
        return ResponseEntity.ok(membershipService.getRequestsMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder, membershipFilters));
    }

    @PostMapping(path = "/request")
    public ResponseEntity<MembershipResponse> requestMembership(@RequestBody @Valid JoinOrganizationRequest organizationRequest) {
        return ResponseEntity.ok(membershipService.requestMembership(organizationRequest));
    }

    @PutMapping(path = "/organizations/{organizationId}/memberships/{membershipId}")
    public ResponseEntity<MembershipResponse> updateMembershipFromOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("membershipId") UUID membershipId,
            @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.updateMembershipFromOrganization(organizationId, membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/organizations/{organizationId}/memberships/update")
    public ResponseEntity<List<MembershipResponse>> updateMembershipsFromOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @RequestBody @Valid UpdateMembershipRequests updateMembershipRequests){
        return new ResponseEntity<>(
                membershipService.updateMembershipsFromOrganization(organizationId, updateMembershipRequests),
                HttpStatus.ACCEPTED);
    }

}
