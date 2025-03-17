package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.GetMenuResponse;
import com.rjproj.memberapp.dto.MenuItemResponse;
import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.Role;
import com.rjproj.memberapp.repository.MemberRoleRepository;
import com.rjproj.memberapp.repository.RoleRepository;
import com.rjproj.memberapp.security.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    @Autowired
    MemberRoleRepository memberRoleRepository;

    public List<MenuItemResponse> getMenu(UUID organizationId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID memberId = ((MemberDetails)principal).getMember().getMemberId();
        List<MenuItemResponse> menuItemResponses = new ArrayList<>();
        menuItemResponses.add(new MenuItemResponse(
                "explore",
                "Explore",
                "/home/explore"
        ));
        menuItemResponses.add(new MenuItemResponse(
                "myOrganization",
                "My Organization",
                "/home/my-organization"
        ));
        menuItemResponses.add(new MenuItemResponse(
                "members",
                "Members",
                "/home/members"
        ));
        if(organizationId == null) {
            return menuItemResponses.stream().filter(menuItemResponse -> menuItemResponse.labelKey().equals("explore")).collect(Collectors.toList());
        }
        Optional<MemberRole> memberRoleOpt = memberRoleRepository.findByMemberIdAndOrganizationId(memberId, organizationId);
        if(memberRoleOpt.isEmpty()) {
            return menuItemResponses.stream().filter(menuItemResponse -> menuItemResponse.labelKey().equals("explore")).collect(Collectors.toList());
        }
        Role role = memberRoleOpt.get().getRole();

        if(role.getName().equals("Member")) {
            return menuItemResponses.stream().filter(menuItemResponse -> !menuItemResponse.labelKey().equals("members")).collect(Collectors.toList());
        } else if (role.getName().equals("Admin") || role.getName().equals("Super Admin")) {
            return menuItemResponses;
        } else {
            return menuItemResponses.stream().filter(menuItemResponse -> menuItemResponse.labelKey().equals("explore")).collect(Collectors.toList());
        }
    }
}
