package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.mapper.MemberMapper;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.mapper.RoleMapper;
import com.rjproj.memberapp.model.*;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.repository.*;
import com.rjproj.memberapp.security.JWTUtil;
import com.rjproj.memberapp.security.MemberDetails;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.exception.MemberErrorMessage.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private FileService fileService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    JWTUtil jwtUtil;

    private final MemberMapper memberMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    private final MembershipMapper membershipMapper;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RoleMapper roleMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private final OrganizationClient organizationClient;

    public Session getLoginSession(String token) {
        if(jwtUtil.validateToken(token)) {

            UUID selectedOrganizationId = jwtUtil.extractSelectedOrganizationId(token);
            UUID memberId = jwtUtil.extractMemberId(token);

            Role activeRole = null;
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Cannot update member with id %s", memberId)
                    ));

            List<String> activeAuthorities = Optional.ofNullable(activeRole)
                    .map(role -> role.getPermissions().stream().map(p -> p.getName()).toList())
                    .orElse(Collections.emptyList());
            List<SimpleGrantedAuthority> updatedAuthorities = activeAuthorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if(selectedOrganizationId != null) {
                activeRole = memberRoleRepository.findRoleByMemberAndOrganization(memberId, selectedOrganizationId);
            }

            MemberDetails memberDetails = (MemberDetails) userDetailsServiceImpl.loadUserByUsername(member.getEmail());
            memberDetails.setActiveRole(activeRole);


            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberDetails, null, updatedAuthorities);

            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);




            MemberResponse memberResponse = memberMapper.fromMember(member);
            RoleResponse roleResponse = null;

            if(activeRole != null) {
                roleResponse = roleMapper.fromRole(activeRole);
            }


            OrganizationResponse organizationResponse = null;
            MembershipResponse membershipResponse = null;

            if(selectedOrganizationId != null) {
                organizationResponse = this.organizationClient.getMyOrganizationById(selectedOrganizationId);
                membershipResponse = membershipMapper.fromMembership(membershipService.getMembership(member.getMemberId(), selectedOrganizationId));
            }
            List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.getMemberId());


            return new Session(
                    token,
                    "Bearer",
                    memberResponse,
                    roleResponse,
                    activeAuthorities,
                    organizationResponse,
                    organizationIdsOfMember,
                    membershipResponse);
        } else {
            throw new MemberException("Please login again", UNAUTHORIZED.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public Session loginMember(LoginRequest loginRequest) {
        try {
            Optional<Member> member = memberRepository.findByEmail(loginRequest.email());

            if(!member.isPresent()) {
                throw new MemberException("The email address you provided does not exists.", MEMBER_NOT_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
            }

            if(member.get().getLoginType().equals(LoginType.GOOGLE)) {
                throw new MemberException("The email is associated with a google account. Sign in with your google account."
                        , SIGN_IN_WITH_GOOGLE.getMessage(), HttpStatus.BAD_REQUEST);
            }

            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            return createLoginSession(member.get());
        }
        catch (BadCredentialsException e)
        {
            System.out.println(e);
            throw new MemberException("Incorrect password", PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Session loginMemberWithGoogle(String googleCode) {
        GoogleInfo googleInfo = googleService.getGoogleInfo(googleCode);
        Optional<Member> member = memberRepository.findByEmail(googleInfo.email());
        if(!member.isPresent()) {
            throw new MemberException(
                    "The google account you provided does not exists. Sign up first to contiue",
                    MEMBER_NOT_EXISTS.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        Member verifiedMember = member.get();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(verifiedMember.getEmail(), null, new ArrayList<>());
        return createLoginSession(verifiedMember);

    }

    public MemberResponse registerMember(MemberRequest memberRequest) {
        memberRequest = new MemberRequest(
                memberRequest.memberId(),
                memberRequest.firstName(),
                memberRequest.lastName(),
                memberRequest.email(),
                memberRequest.password(),
                memberRequest.phoneNumber(),
                memberRequest.profilePicUrl(),
                memberRequest.birthDate(),
                LoginType.NORMAL,  // Default value for loginType
                memberRequest.memberAddress()
        );
        return addMember(memberRequest);
    }

    public MemberResponse registerMemberWithGoogle(String googleCode) {
        GoogleInfo googleInfo = googleService.getGoogleInfo(googleCode);
        if(googleInfo == null) {
            throw new MemberException(
                    "Error signing up with google",
                    SIGN_UP_WITH_GOOGLE.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        if(googleInfo.email() == null) {
            throw new MemberException(
                    "Error signing up with google",
                    SIGN_UP_WITH_GOOGLE.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        MemberRequest memberRequest = new MemberRequest(
                null,
                googleInfo.firstName(),
                googleInfo.lastName(),
                googleInfo.email(),
                null,
                null,
                googleInfo.photoUrl(),
                googleInfo.birthdate(),
                LoginType.GOOGLE,
                null
        );
        return addMember(memberRequest);
    }

    public Session selectLoginOrganization(@Valid SelectOrganizationLoginRequest selectOrganizationRequest) {

        try {
            Role activeRole = memberRoleRepository.findRoleByMemberAndOrganization(selectOrganizationRequest.memberId(), selectOrganizationRequest.organizationId());
            Member member = memberRepository.findById(selectOrganizationRequest.memberId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Cannot update member with id %s", selectOrganizationRequest.memberId())
                    ));

            List<String> activeAuthorities = Optional.ofNullable(activeRole)
                    .map(role -> role.getPermissions().stream().map(p -> p.getName()).toList())
                    .orElse(Collections.emptyList());

            //update token again
            String jwt = jwtUtil.generateToken(member.getEmail(), activeRole, activeAuthorities, selectOrganizationRequest.organizationId(), member.getMemberId());

            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            // Extract the current user details
            MemberDetails currentUser = (MemberDetails) currentAuth.getPrincipal();
            currentUser.setActiveRole(activeRole);

            // Extract the current user details
            List<SimpleGrantedAuthority> updatedAuthorities = activeAuthorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Create a new Authentication object with the updated authorities
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    currentUser, currentUser.getPassword(), updatedAuthorities);

            // Update the SecurityContext with the new authentication object
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            MemberResponse memberResponse = memberMapper.fromMember(member);
            RoleResponse roleResponse  = null;
            if(activeRole != null) {
                roleResponse = roleMapper.fromRole(activeRole);
            }
            OrganizationResponse organizationResponse = this.organizationClient.getMyOrganizationById(selectOrganizationRequest.organizationId());
            List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.getMemberId());
            MembershipResponse membershipResponse =  membershipMapper.fromMembership(membershipService.getMembership(member.getMemberId(), selectOrganizationRequest.organizationId()));

            return new Session(
                    jwt,
                    "Bearer",
                    memberResponse,
                    roleResponse,
                    activeAuthorities,
                    organizationResponse,
                    organizationIdsOfMember,
                    membershipResponse
            );
        }
        catch (BadCredentialsException e)
        {
            throw new MemberException("Incorrect password for " + selectOrganizationRequest.memberId(), PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    public MemberResponse updateMemberAfterRegistration(
            UUID memberId,
            MultipartFile profilePicImage,
            @Valid AdditionalInfoRequest additionalInfoRequest) {
        return updateMemberDetails(memberId, profilePicImage, additionalInfoRequest);
    }

    public MemberResponse updateMemberDetails(
            UUID memberId,
            MultipartFile profilePicImage,
            @Valid AdditionalInfoRequest additionalInfoRequest
    ) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new MemberException("The email address you provided does not exist.",
                    MEMBER_NOT_EXISTS.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        Member existingMember = member.get();
        String imageUrl = additionalInfoRequest.memberRequest().profilePicUrl(); // Default to existing URL

        if (profilePicImage != null && !profilePicImage.isEmpty()) {
            try {
                imageUrl = fileService.uploadImage("member",
                        existingMember.getMemberId(),
                        ImageType.PROFILE_IMAGE,
                        profilePicImage);
            } catch (IOException e) {
                throw new MemberException("Failed to upload profile image",
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        MemberRequest updatedRequest = new MemberRequest(
                additionalInfoRequest.memberRequest().memberId(),
                additionalInfoRequest.memberRequest().firstName(),
                additionalInfoRequest.memberRequest().lastName(),
                additionalInfoRequest.memberRequest().email(),
                additionalInfoRequest.memberRequest().password(),
                additionalInfoRequest.memberRequest().phoneNumber(),
                imageUrl,
                additionalInfoRequest.memberRequest().birthDate(),
                additionalInfoRequest.memberRequest().loginType(),
                additionalInfoRequest.memberRequest().memberAddress()
        );

        return this.updateMember(existingMember.getMemberId(), updatedRequest);
    }

    private MemberResponse addMember(MemberRequest memberRequest) {
        Optional<Member> retrievedMember = memberRepository.findByEmail(memberRequest.email());
        Member member = memberMapper.toMember(memberRequest);
        Optional<Role> defaultRole = roleRepository.findByName("Non-Member");


        if (retrievedMember.isPresent()){
            throw new MemberException("Member with email address " + memberRequest.email() + " already exists. Sign in to continue", MEMBER_EXISTS.getMessage(), HttpStatus.CONFLICT);
        }
        if(memberRequest.loginType() == LoginType.NORMAL) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }

        //create a default role in a default organization
        UUID defaultOrganizationId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Member savedMember = memberRepository.save(member);
        MemberRoleId memberRoleId = new MemberRoleId();
        memberRoleId.setMemberId(member.getMemberId());
        memberRoleId.setRoleId(defaultRole.get().getRoleId());
        memberRoleId.setOrganizationId(defaultOrganizationId);

        MemberRole memberRole = MemberRole.builder().id(memberRoleId).member(savedMember).role(defaultRole.get()).build();
        memberRoleRepository.save(memberRole);

        return memberMapper.fromMember(savedMember);
    }

    private Session createLoginSession(Member member) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(member.getEmail());

        MemberResponse memberResponse = memberMapper.fromMember(member);
        Role activeRole = null;
        RoleResponse roleResponse = null;
        List<String> preLogInPermissions = new ArrayList<>();
        OrganizationResponse activeOrganization = null;
        List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.getMemberId());
        MembershipResponse activeMembership = null;

        UUID activeOrganizationId = null;

        String jwt = null;

        if(organizationIdsOfMember.size() == 1) {
            activeOrganizationId = organizationIdsOfMember.getFirst();

            activeRole = memberRoleRepository.findRoleByMemberAndOrganization(member.getMemberId(), activeOrganizationId);
            roleResponse = roleMapper.fromRole(activeRole);
            preLogInPermissions = activeRole.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList());
            activeMembership =  membershipMapper.fromMembership(membershipService.getMembership(member.getMemberId(), activeOrganizationId));
            jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions, activeOrganizationId, member.getMemberId());

            activeOrganization = this.organizationClient.getMyOrganizationById(activeOrganizationId);

        } else {
            Optional<MemberRole> memberRole = memberRoleRepository.findByMemberIdAndOrganizationId(member.getMemberId(), UUID.fromString("00000000-0000-0000-0000-000000000000"));

            if(memberRole.isPresent()) {
                memberRole.get().getRole().getPermissions().stream().map(Permission::getName).forEach(preLogInPermissions::add);
            } else {
                preLogInPermissions.add("com.rjproj.memberapp.permission.organization.viewAll");
            }
        }


        jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions, activeOrganizationId, member.getMemberId());

        return new Session(
                jwt,
                "Bearer",
                memberResponse,
                roleResponse,
                preLogInPermissions,
                activeOrganization,
                organizationIdsOfMember.size() == 0 ? null : organizationIdsOfMember,
                activeMembership
        );
    }

    private void mergeMember(Member member, @Valid MemberRequest memberRequest) {
        if(StringUtils.isNotBlank(memberRequest.firstName())) {
            member.setFirstName(memberRequest.firstName());
        }
        if(StringUtils.isNotBlank(memberRequest.lastName())) {
            member.setLastName(memberRequest.lastName());
        }
        if(StringUtils.isNotBlank(memberRequest.email())) {
            member.setEmail(memberRequest.email());
        }
        if(StringUtils.isNotBlank(memberRequest.phoneNumber())) {
            member.setPhoneNumber(memberRequest.phoneNumber());
        }
        if(StringUtils.isNotBlank(memberRequest.profilePicUrl())) {
            member.setProfilePicUrl(memberRequest.profilePicUrl());
        }
        if(memberRequest.birthDate() != null) {
            member.setBirthDate(memberRequest.birthDate());
        }
        if(memberRequest.loginType() != null) {
            member.setLoginType(memberRequest.loginType());
        }
        if(memberRequest.memberAddress() != null) {
            member.setMemberAddress(memberRequest.memberAddress());
        }
    }

    private MemberResponse updateMember(UUID memberId, @Valid MemberRequest memberRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update member with id %s", memberId.toString())
                ));
        mergeMember(member, memberRequest);
        return memberMapper.fromMember(memberRepository.save(member));
    }

}