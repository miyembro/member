package com.rjproj.memberapp.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class MemberRoleId implements Serializable {
    private UUID organizationId;
    private UUID memberId;
    private UUID roleId;

}