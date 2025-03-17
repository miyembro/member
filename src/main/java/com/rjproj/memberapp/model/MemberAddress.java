package com.rjproj.memberapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID memberAddressId;

    private String street;

    private String city;

    private String provinceState;

    private String postalCode;

    private String country;

    @JsonIgnore
    @OneToOne(mappedBy = "memberAddress")
    private Member member;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
