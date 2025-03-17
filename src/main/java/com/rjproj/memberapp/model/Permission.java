package com.rjproj.memberapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID permissionId;

    private String name;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

}
