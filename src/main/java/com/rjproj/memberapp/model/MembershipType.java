package com.rjproj.memberapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class MembershipType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID membershipTypeId;

    UUID organizationId;

    @ManyToOne
    @JoinColumn(name = "membership_type_validity_id")
    private MembershipTypeValidity membershipTypeValidity;

    String name;

    String description;

    Boolean isDefault;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
