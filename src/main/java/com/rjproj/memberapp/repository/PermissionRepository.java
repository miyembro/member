package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
