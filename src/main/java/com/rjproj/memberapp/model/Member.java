package com.rjproj.memberapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rjproj.memberapp.dto.LoginType;
import com.rjproj.memberapp.util.CollectionUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID memberId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private String profilePicUrl;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)  // Store as a String in DB
    @NotNull  // Ensure this field cannot be null when saving the entity
    private LoginType loginType; // N

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_address_id", referencedColumnName = "memberAddressId", nullable = true, unique = true)
    private MemberAddress memberAddress;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public Set<String> getPermissionNames() {
        if (CollectionUtil.isEmpty(this.roles)) {
            return Collections.emptySet();
        }
        Set<Permission> emptyPermissionsSet = Collections.emptySet();
        return this.getRoles().stream()
                .map(r -> CollectionUtil.isEmpty(r.getPermissions()) ? emptyPermissionsSet : r.getPermissions())
                .flatMap(p1 -> p1.stream().map(p2 -> p2.getName())).collect(Collectors.toSet());
    }

}
