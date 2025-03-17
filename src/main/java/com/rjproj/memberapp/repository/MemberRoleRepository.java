package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.MemberRoleId;
import com.rjproj.memberapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface MemberRoleRepository extends JpaRepository<MemberRole, MemberRoleId> {

    @Query("SELECT mr FROM MemberRole mr WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId")
    Optional<MemberRole> findByMemberIdAndOrganizationId(
            @Param("memberId") UUID memberId,
            @Param("organizationId") UUID organizationId
    );

    // Query to find MemberRole based on MemberId, OrganizationId, and RoleId
    @Query("SELECT mr FROM MemberRole mr WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId AND mr.id.roleId = :roleId")
    Optional<MemberRole> findByMemberIdOrganizationIdAndRoleId(
            @Param("memberId") UUID memberId,
            @Param("organizationId") UUID organizationId,
            @Param("roleId") UUID roleId
    );
    
    @Query("SELECT mr.role FROM MemberRole mr WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId")
    Role findRoleByMemberAndOrganization(@Param("memberId") UUID memberId, @Param("organizationId") UUID organizationId);

    // manually update the member_role since Role is a part of the composite key of MemberRole. Java consider composite key as immutable
    @Modifying
    @Transactional
    @Query("UPDATE MemberRole mr SET mr.id.roleId = :newRoleId WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId")
    void updateMemberRole(
            @Param("memberId") UUID memberId,
            @Param("organizationId") UUID organizationId,
            @Param("newRoleId") UUID newRoleId
    );

}
