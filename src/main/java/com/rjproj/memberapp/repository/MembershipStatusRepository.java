package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipStatusRepository  extends JpaRepository<MembershipStatus, UUID> {

    MembershipStatus findByName(String name);

    List<MembershipStatus> findByNameIn(List<String> membershipStatusNames);

}
