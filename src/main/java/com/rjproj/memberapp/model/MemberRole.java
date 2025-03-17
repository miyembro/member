package com.rjproj.memberapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class MemberRole {

    @EmbeddedId
    private MemberRoleId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    @JsonIgnore
    private Member member;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @JsonIgnore
    private Role role;
}
