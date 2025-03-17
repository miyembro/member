package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MembershipTypeService;
import com.rjproj.memberapp.service.MembershipTypeValidityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership-types")
@RequiredArgsConstructor
public class MembershipTypeController {

    private final MembershipTypeService membershipTypeService;

    private final MembershipTypeValidityService membershipTypeValidityService;

    //From other service
    @PostMapping("/bulk")
    public ResponseEntity<List<MembershipTypeResponse>> createMembershipTypes(@RequestBody @Valid List<MembershipTypeRequest> membershipTypeRequests) {
        return ResponseEntity.ok(membershipTypeService.createMembershipTypes(membershipTypeRequests));
    }

    @GetMapping("/validities")
    public ResponseEntity<List<MembershipTypeValidityResponse>> getAllMembershipTypeValidities() {
        return ResponseEntity.ok(membershipTypeValidityService.getAllMembershipTypeValidities());
    }

    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<List<MembershipTypeResponse>> getMembershipTypesByOrganizationId(
            @PathVariable("organizationId") UUID organizationId
    ) {
        return ResponseEntity.ok(membershipTypeService.getMembershipTypesByOrganizationId(organizationId));
    }



}
