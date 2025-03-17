package com.rjproj.memberapp.controller;


import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /* Call from other service */
    @PostMapping("/organizations/{organizationId}/admin")
    public ResponseEntity<String> createAdminRoleForOrganizationOwner(
            @PathVariable("organizationId") UUID organizationId
    ) {
        return ResponseEntity.ok(roleService.createAdminRoleForOrganizationOwner(organizationId));
    }

    @GetMapping("/visible")
    public ResponseEntity<List<RoleResponse>> getVisibleRoles() {
        return ResponseEntity.ok(roleService.getVisibleRoles());
    }


}
