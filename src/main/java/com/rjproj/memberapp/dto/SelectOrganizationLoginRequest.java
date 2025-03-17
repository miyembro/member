package com.rjproj.memberapp.dto;

import java.util.UUID;

public record SelectOrganizationLoginRequest(
        UUID organizationId,
        UUID memberId
) {
}
