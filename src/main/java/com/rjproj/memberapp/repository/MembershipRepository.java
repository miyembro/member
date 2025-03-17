package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID>, JpaSpecificationExecutor<Membership>, MembershipRepositoryCustom {

    @Query("SELECT m.organizationId FROM Membership m WHERE m.member.memberId = :memberId AND m.membershipType IS NOT NULL")
    List<UUID> findActiveOrganizationIdsByMemberId(@Param("memberId") UUID memberId);

    Page<Membership> findAll(Specification<Membership> spec, Pageable pageable);

    Optional<Membership> findByMembershipId(UUID membershipId);

    @Query("SELECT m FROM Membership m WHERE m.member.memberId = :memberId")
    List<Membership> findMembershipsByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT m FROM Membership m WHERE m.member.memberId = :memberId AND m.organizationId = :organizationId")
    Membership findMembershipByMemberIdAndOrganizationId(@Param("memberId") UUID memberId, @Param("organizationId") UUID organizationId);

    @Query("SELECT m FROM Membership m WHERE m.membershipId = :membershipId AND m.organizationId = :organizationId")
    Optional<Membership> findByMembershipIdAndOrganizationId(@Param("membershipId") UUID membershipId, @Param("organizationId") UUID organizationId);

    // Custom query to fetch memberships by a list of membership IDs and the organization ID
    @Query("SELECT m FROM Membership m WHERE m.membershipId IN :membershipIds AND m.organizationId = :organizationId")
    List<Membership> findByMembershipIdInAndOrganizationId(@Param("membershipIds") List<UUID> membershipIds, @Param("organizationId") UUID organizationId);


//    /*Unused Methods*/
//    List<Membership> findByOrganizationId(UUID organizationId);
//
//    @Query("SELECT m FROM Membership m " +
//            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
//            "JOIN Role r ON mr.role.roleId = r.roleId " +
//            "WHERE m.organizationId = :organizationId AND mr.id.organizationId = :organizationId AND m.membershipType IS NOT NULL " +
//            "ORDER BY r.name ASC")
//    Page<Membership> findMembershipsByOrganizationIdSortedByRoleName(UUID organizationId, Pageable pageable);
//
//    @Query("SELECT m FROM Membership m " +
//            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
//            "JOIN Role r ON mr.role.roleId = r.roleId " +
//            "WHERE m.organizationId = :organizationId " +
//            "AND (:firstName IS NULL OR :firstName = '' OR LOWER(m.member.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
//            "AND (:email IS NULL OR :email = '' OR LOWER(m.member.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
//            "AND (:city IS NULL OR :city = '' OR LOWER(m.member.memberAddress.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
//            "AND (:country IS NULL OR :country = '' OR LOWER(m.member.memberAddress.country) LIKE LOWER(CONCAT('%', :country, '%'))) " +
//            "AND (:membershipStatusNames IS NULL OR m.membershipStatus.name IN :membershipStatusNames) " +
//            "AND (:membershipTypeNames IS NULL OR m.membershipType.name IN :membershipTypeNames) " +
//            "AND (:roleNames IS NULL OR r.name IN :roleNames) " +
//            "AND (:startDateFrom IS NULL OR m.startDate >= :startDateFrom) " +
//            "AND (:startDateTo IS NULL OR m.startDate <= :startDateTo) " +
//            "AND (:endDateFrom IS NULL OR m.endDate >= :endDateFrom) " +
//            "AND (:endDateTo IS NULL OR m.endDate <= :endDateTo) " +
//            "ORDER BY r.name ASC") // Sorting by role.name
//    Page<Membership> findMembershipsByOrganizationIdWithFiltersSortedByRoleNameWithFilters(
//            @Param("organizationId") UUID organizationId,
//            @Param("firstName") String firstName,
//            @Param("email") String email,
//            @Param("city") String city,
//            @Param("country") String country,
//            @Param("membershipStatusNames") List<String> membershipStatusNames,
//            @Param("membershipTypeNames") List<String> membershipTypeNames,
//            @Param("roleNames") List<String> roleNames,
//            @Param("startDateFrom") Date startDateFrom,
//            @Param("startDateTo") Date startDateTo,
//            @Param("endDateFrom") Date endDateFrom,
//            @Param("endDateTo") Date endDateTo,
//            Pageable pageable);
//
//    @Query("SELECT m FROM Membership m " +
//            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
//            "JOIN Role r ON mr.role.roleId = r.roleId " +
//            "WHERE m.organizationId = :organizationId AND mr.id.organizationId = :organizationId AND m.membershipType IS NOT NULL AND m.membershipStatus.name != 'Pending' ")
//    Page<Membership> findMembershipsByOrganizationIdWithSpecification(
//            @Param("organizationId") UUID organizationId,
//            Specification<Membership> specification,
//            Pageable pageable);
//
//    @Query("SELECT m.organizationId FROM Membership m WHERE m.member.memberId = :memberId")
//    List<UUID> findOrganizationIdsByMemberId(@Param("memberId") UUID memberId);
//
//
//    // Paginated query with a filter to only include memberships where membershipType is not null
//    @Query("SELECT m FROM Membership m WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL")
//    Page<Membership> findMembershipsByOrganizationId(UUID organizationId, Pageable pageable);
//
//    @Query("SELECT m FROM Membership m " +
//            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
//            "JOIN Role r ON mr.role.roleId = r.roleId " +
//            "WHERE m.organizationId = :organizationId " +
//            "AND m.membershipType IS NOT NULL " +
//            "AND ((:firstName IS NULL OR :firstName = '' OR LOWER(m.member.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) OR  (:firstName IS NULL OR :firstName = '' OR LOWER(m.member.lastName) LIKE LOWER(CONCAT('%', :firstName, '%'))))" +
//            "AND (:email IS NULL OR :email = '' OR LOWER(m.member.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
//            "AND (:city IS NULL OR :city = '' OR LOWER(m.member.memberAddress.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
//            "AND (:country IS NULL OR :country = '' OR LOWER(m.member.memberAddress.country) LIKE LOWER(CONCAT('%', :country, '%'))) " +
//            "AND (:membershipStatusNames IS NULL OR m.membershipStatus.name IN :membershipStatusNames) " +
//            "AND (:membershipTypeNames IS NULL OR m.membershipType.name IN :membershipTypeNames) " +
//            "AND (:roleNames IS NULL OR r.name IN :roleNames) " +  // Filtering by list of role names
//            "AND  (cast(:startDateFrom as timestamp) is null or m.startDate  >= :startDateFrom ) " +
//            "AND  (cast(:startDateTo as timestamp)   is null or  m.startDate <= :startDateTo   ) " +
//            "AND  (cast(:endDateFrom as timestamp) is null or m.endDate  >= :endDateFrom ) " +
//            "AND  (cast(:endDateTo as timestamp)   is null or  m.endDate <= :endDateTo   ) "
//    )
//    Page<Membership> findMembershipsByOrganizationIdWithFilters(
//            @Param("organizationId") UUID organizationId,
//            @Param("firstName") String firstName,
//            @Param("email") String email,
//            @Param("city") String city,
//            @Param("country") String country,
//            @Param("membershipStatusNames") List<String> membershipStatusNames,
//            @Param("membershipTypeNames") List<String> membershipTypeNames,
//            @Param("roleNames") List<String> roleNames,  // Now accepting a list of role names
//            @Param("startDateFrom") Date startDateFrom,
//            @Param("startDateTo") Date startDateTo,
//            @Param("endDateFrom") Date endDateFrom,
//            @Param("endDateTo") Date endDateTo,
//            Pageable pageable
//    );
//
//    // Paginated query with a filter to only include memberships where membershipType is not null
//    @Query("SELECT m FROM Membership m WHERE m.organizationId = :organizationId AND m.membershipType IS NULL")
//    Page<Membership> findPendingMembershipsByOrganizationId(UUID organizationId, Pageable pageable);
//
////    @Query("SELECT m FROM Membership m " +
////            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
////            "JOIN Role r ON mr.role.roleId = r.roleId " +
////            "WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL")
////    Page<Membership> findMembershipsByOrganizationId(UUID organizationId, Pageable pageable);
//
//
////    @Query("SELECT m FROM Membership m JOIN m.memberRoles r WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL ORDER BY r.role.name")
////    Page<Membership> findMembershipsByOrganizationIdSortedByRoleName(UUID organizationId, Pageable pageable);
//
}
