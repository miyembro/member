package com.rjproj.memberapp.dto;

import java.util.UUID;

public record RoleRequest(
        UUID roleId,
        String name
) {
}
