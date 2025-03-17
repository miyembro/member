package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    List<Role> findByNameIn(List<String> memberRoleNames); // Fetch multiple roles by names

}