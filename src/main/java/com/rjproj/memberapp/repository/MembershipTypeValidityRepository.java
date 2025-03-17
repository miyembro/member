package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.MembershipTypeValidity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipTypeValidityRepository extends JpaRepository<MembershipTypeValidity, UUID> {

}

