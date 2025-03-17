package com.rjproj.memberapp.dto;

import java.util.UUID;

public record RoleResponse(
        UUID roleId,
        String name
) {
}
