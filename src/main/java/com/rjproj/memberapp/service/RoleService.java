package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.mapper.RoleMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.MemberRoleId;
import com.rjproj.memberapp.model.Role;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.repository.MemberRoleRepository;
import com.rjproj.memberapp.repository.RoleRepository;
import com.rjproj.memberapp.security.JWTUtil;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.exception.MemberErrorMessage.MEMBER_EXISTS;

@Service
@RequiredArgsConstructor
public class RoleService {

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    private final RoleMapper roleMapper;

    @Autowired
    private RoleRepository roleRepository;

    /* Call from other service */
    public String createAdminRoleForOrganizationOwner(@Valid UUID organizationId) {

        UUID memberId = jwtUtil.extractMemberIdInternally();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update member with id %s", memberId)
                ));

        Role role = roleRepository.findByName("Admin").get();
        Optional<MemberRole> existingMemberRole = memberRoleRepository.findByMemberIdOrganizationIdAndRoleId(member.getMemberId(), organizationId, role.getRoleId());

        if (existingMemberRole.isPresent()) {
            throw new MemberException("Role already exists", MEMBER_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
        } else {

            MemberRole memberRole = new MemberRole();

            MemberRoleId memberRoleId = new MemberRoleId();
            memberRoleId.setMemberId(member.getMemberId());
            memberRoleId.setOrganizationId(organizationId);
            memberRoleId.setRoleId(role.getRoleId());

            memberRole.setId(memberRoleId);
            memberRole.setMember(member);
            memberRole.setRole(role);

            memberRoleRepository.save(memberRole);
            return member.getMemberId().toString();
        }

    }

    public Role getRoleByName(String roleName) {
        //set Member role as default
        if(roleName != null) {
            Optional<Role> role = roleRepository.findByName(roleName);

            if(role.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found");
            }
            return role.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role Id cannot be null");
    }

    public List<RoleResponse> getVisibleRoles() {
        List<String> userRoleNames = new ArrayList<>();
        userRoleNames.add("Member");
        userRoleNames.add("Admin");
       List<Role> roles = roleRepository.findByNameIn(userRoleNames);
       return roles.stream().map(roleMapper::fromRole).collect(Collectors.toList()) ;
    }

}
