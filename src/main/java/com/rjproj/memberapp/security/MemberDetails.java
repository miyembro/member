package com.rjproj.memberapp.security;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Permission;
import com.rjproj.memberapp.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


public class MemberDetails implements UserDetails {

    private Member member;


    private Role activeRole;

    public MemberDetails(Member member) {
//        super(getAuthorities(), member.getPassword(), true, true, true,
//                true);
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getActivePermissionNames().stream().map(p -> new SimpleGrantedAuthority(p)).collect(Collectors.toList());
    }

    public void setActiveRole(Role role) {
        this.activeRole = role;
    }


    public Set<String> getActivePermissionNames() {
        if (activeRole == null) {
            return Collections.emptySet(); // No permissions if no active role
        }
        return activeRole.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.member.getPassword();
    }

    @Override
    public String getUsername() {
        return this.member.getEmail();
    }

    public Member getMember() {
        return this.member;
    }
}
