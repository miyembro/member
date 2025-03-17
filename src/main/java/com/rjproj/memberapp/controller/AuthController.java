package com.rjproj.memberapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/getLoginSession")
    public ResponseEntity<Session> getLoginSession(@RequestBody @Valid String token){
        return ResponseEntity.ok(memberService.getLoginSession(token));
    }

    @PostMapping("/login")
    public ResponseEntity<Session> loginMember(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(memberService.loginMember(loginRequest));
    }

    @PostMapping("/login/withGoogle")
    public ResponseEntity<Session> loginWithGoogle(@RequestBody @Valid GoogleRequest googleRequest){
        return ResponseEntity.ok(memberService.loginMemberWithGoogle(googleRequest.googleToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerMember(@RequestBody @Valid MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.registerMember(memberRequest));
    }

    @PostMapping("/register/withGoogle")
    public ResponseEntity<MemberResponse> registerMemberWithGoole(@RequestBody @Valid GoogleRequest googleRequest) {
        return ResponseEntity.ok(memberService.registerMemberWithGoogle(googleRequest.googleToken()));
    }

    @PostMapping("/selectLoginOrganization")
    public ResponseEntity<Session> selectLoginOrganization(@RequestBody @Valid SelectOrganizationLoginRequest selectOrganizationLoginRequest) {
        return ResponseEntity.ok(memberService.selectLoginOrganization(selectOrganizationLoginRequest));
    }



}