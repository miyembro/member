package com.rjproj.memberapp.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID memberId;
    private String id;
    private String name;
    private String fileUrl;

}
