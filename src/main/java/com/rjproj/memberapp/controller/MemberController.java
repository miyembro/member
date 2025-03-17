package com.rjproj.memberapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping(path = "/{memberId}/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponse> updateMemberAfterRegistration(
            @PathVariable("memberId") UUID memberId,
            @RequestPart(value = "profilePicImage", required = false) MultipartFile profilePicImage,
            @RequestPart(value = "additionalInfoRequest") String additionalInfoRequest
    ) {

        AdditionalInfoRequest request;
        try {
            request = objectMapper.readValue(additionalInfoRequest, AdditionalInfoRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(memberService.updateMemberAfterRegistration(memberId, profilePicImage, request));
    }

    @PutMapping(path = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponse> updateMemberDetails(
            @PathVariable("memberId") UUID memberId,
            @RequestPart(value = "profilePicImage", required = false) MultipartFile profilePicImage,
            @RequestPart(value = "additionalInfoRequest") String additionalInfoRequest
    ) {

        AdditionalInfoRequest request;
        try {
            request = objectMapper.readValue(additionalInfoRequest, AdditionalInfoRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(memberService.updateMemberDetails(memberId, profilePicImage, request));
    }

}