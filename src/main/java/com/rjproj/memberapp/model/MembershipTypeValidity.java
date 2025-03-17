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
public class MembershipTypeValidity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID membershipTypeValidityId;

    private String name;

    private Integer duration; // Nullable duration

    private String description;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}

