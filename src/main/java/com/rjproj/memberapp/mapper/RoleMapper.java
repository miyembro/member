package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.RoleRequest;
import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.model.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper {

    public RoleResponse fromRole(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getName()
        );
    }

    public Role toRole(RoleRequest roleRequest) {
        return Role.builder()
                .roleId(roleRequest.roleId())
                .name(roleRequest.name())
                .build();
    }
}
