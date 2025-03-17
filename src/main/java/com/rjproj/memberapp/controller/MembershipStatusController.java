package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.MembershipStatusResponse;
import com.rjproj.memberapp.service.MembershipStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership-statuses")
@RequiredArgsConstructor
public class MembershipStatusController {

    private final MembershipStatusService membershipStatusService;

    @GetMapping("/approved")
    public ResponseEntity<List<MembershipStatusResponse>> getApprovedMembershipStatuses() {
        return ResponseEntity.ok(membershipStatusService.getApprovedMembershipStatuses());
    }

    @GetMapping("/join-requests")
    public ResponseEntity<List<MembershipStatusResponse>> getJoinRequestsMembershipStatuses() {
        return ResponseEntity.ok(membershipStatusService.getJoinRequestsMembershipStatuses());
    }
}
