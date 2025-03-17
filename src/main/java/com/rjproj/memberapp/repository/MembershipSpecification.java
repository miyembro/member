package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;

public class MembershipSpecification {

    public static Specification<Membership> filterByCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city != null && !city.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("memberAddress").get("city")), "%" + city.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Membership> filterByCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country != null && !country.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("memberAddress").get("country")), "%" + country.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Membership> filterByFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName != null && !firstName.isEmpty()) {
                // Filter by first name OR last name (you can choose to combine them differently)
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("firstName")),
                                "%" + firstName.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("lastName")),
                                "%" + firstName.toLowerCase() + "%")
                );
            }
            return null;
        };
    }

    public static Specification<Membership> filterByEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email != null && !email.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("email")), "%" + email.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Membership> filterByEndDateRange(Date endDateFrom, Date endDateTo) {
        return (root, query, criteriaBuilder) -> {
            if (endDateFrom != null && endDateTo != null) {
                return criteriaBuilder.between(root.get("endDate"), endDateFrom, endDateTo);
            }
            return null;
        };
    }

    public static Specification<Membership> filterByMembershipStatus(List<String> membershipStatusNames) {
        return (root, query, criteriaBuilder) -> {
            if (membershipStatusNames != null && !membershipStatusNames.isEmpty()) {
                return root.get("membershipStatus").get("name").in(membershipStatusNames);
            }
            return null;
        };
    }

    public static Specification<Membership> filterByMembershipTypes(List<String> membershipTypeNames) {
        return (root, query, criteriaBuilder) -> {
            if (membershipTypeNames != null && !membershipTypeNames.isEmpty()) {
                return root.get("membershipType").get("name").in(membershipTypeNames);
            }
            return null;
        };
    }

    public static Specification<Membership> filterByRoleNames(List<String> roleNames, UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            if (roleNames != null && !roleNames.isEmpty()) {
                // Create a subquery to filter Memberships based on role names and organizationId
                Subquery<UUID> subquery = query.subquery(UUID.class); // Use UUID instead of Long
                Root<Membership> subRoot = subquery.from(Membership.class);
                Join<Membership, Member> subMemberJoin = subRoot.join("member");
                Join<Member, MemberRole> subRoleJoin = subMemberJoin.join("roles");

                // Select the Membership IDs that match the role names and organizationId
                subquery.select(subRoot.get("membershipId")) // Ensure this is UUID
                        .where(
                                criteriaBuilder.and(
                                        subRoleJoin.get("name").in(roleNames),
                                        criteriaBuilder.equal(subRoot.get("organizationId"), organizationId)
                                )
                        );

                // Filter the main query using the subquery
                return root.get("membershipId").in(subquery);
            }
            return null;
        };
    }

    public static Specification<Membership> filterByStartDateRange(Date startDateFrom, Date startDateTo) {
        return (root, query, criteriaBuilder) -> {
            if (startDateFrom != null && startDateTo != null) {
                return criteriaBuilder.between(root.get("startDate"), startDateFrom, startDateTo);
            }
            return null;
        };
    }

    public static Specification<Membership> hadMembershipTypePending() {
        return (root, query, criteriaBuilder) -> {

            Predicate membershipStatusPredicatePending = criteriaBuilder.equal(root.get("membershipStatus").get("name"), "Pending");

            // Combine the two predicates
            return criteriaBuilder.or(membershipStatusPredicatePending);
        };
    }

    public static Specification<Membership> hadMembershipTypeDenied() {
        return (root, query, criteriaBuilder) -> {

            Predicate membershipStatusPredicatePending = criteriaBuilder.equal(root.get("membershipStatus").get("name"), "Denied");

            // Combine the two predicates
            return criteriaBuilder.or(membershipStatusPredicatePending);
        };
    }

    public static Specification<Membership> hasOrganizationId(UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            // Filter by organizationId
            Predicate organizationPredicate = criteriaBuilder.equal(root.get("organizationId"), organizationId);


            // Combine the two predicates
            return criteriaBuilder.and(organizationPredicate);
        };
    }

    public static Specification<Membership> hasOrganizationIdAndMembershipTypeNotNull(UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            // Filter by organizationId
            Predicate organizationPredicate = criteriaBuilder.equal(root.get("organizationId"), organizationId);

            // Ensure membershipType is not null
            Predicate membershipTypePredicate = criteriaBuilder.isNotNull(root.get("membershipType"));

            Predicate membershipStatusPredicatePending = criteriaBuilder.notEqual(root.get("membershipStatus").get("name"), "Pending");


            // Combine the two predicates
            return criteriaBuilder.and(organizationPredicate, membershipTypePredicate, membershipStatusPredicatePending);
        };
    }

    public static Specification<Membership> applySorting(Sort sort, UUID organizationId) {
        return (root, query, criteriaBuilder) -> {
            if (sort == null || sort.isEmpty()) {
                return criteriaBuilder.conjunction(); // No sorting if not specified
            }

            // Order list for sorting
            List<Order> orders = new ArrayList<>();

            // Loop through the sort fields and add corresponding orders
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                if ("role.name".equals(property)) {
                    Join<Membership, Member> memberJoin = root.join("member");

                    // Join Member -> MemberRole (this is the junction table connecting Member and Role)
                    Join<Member, Role> memberRoleJoin = memberJoin.join("roles", JoinType.INNER); // Using memberRoles directly

                    Join<Role, MemberRole> roleJoin = memberRoleJoin.join("memberRoles", JoinType.INNER);

                    query.distinct(true);


                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberRoleJoin.get("name")) :
                            criteriaBuilder.desc(memberRoleJoin.get("name")));
                } else if ("member.firstName".equals(property)) {
                    // Sorting by member's first name, then by last name
                    Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);

                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberJoin.get("firstName") ) :
                            criteriaBuilder.desc(memberJoin.get("firstName")));

                } else if ("member.lastName".equals(property)) {
                    // Sorting by member's first name, then by last name
                    Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);

                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberJoin.get("lastName") ) :
                            criteriaBuilder.desc(memberJoin.get("lastName")));

                } else if ("member.email".equals(property)) {
                    // Sorting by member's first name
                    Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberJoin.get("email")) :
                            criteriaBuilder.desc(memberJoin.get("email")));
                }  else if ("member.phoneNumber".equals(property)) {
                    // Sorting by member's first name
                    Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberJoin.get("phoneNumber")) :
                            criteriaBuilder.desc(memberJoin.get("phoneNumber")));
                } else if ("membershipStatus.name".equals(property)) {
                    // Sorting by member's first name
                    Join<Membership, MembershipStatus> membershipStatusJoin = root.join("membershipStatus", JoinType.LEFT);
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(membershipStatusJoin.get("name")) :
                            criteriaBuilder.desc(membershipStatusJoin.get("name")));
                } else if ("membershipType.name".equals(property)) {
                    // Sorting by member's first name
                    Join<Membership, MembershipType> membershipTypeJoin = root.join("membershipType", JoinType.LEFT);
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(membershipTypeJoin.get("name")) :
                            criteriaBuilder.desc(membershipTypeJoin.get("name")));
                } else if ("member.memberAddress.city".equals(property)) {
                    // Sorting by member's address city
                    Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);
                    Join<Member, MemberAddress> memberAddressJoin = memberJoin.join("memberAddress", JoinType.LEFT); // Fix: Use memberJoin

                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(memberAddressJoin.get("city")) :
                            criteriaBuilder.desc(memberAddressJoin.get("city")));
                } else {
                    // Sorting by other Membership attributes
                    orders.add(order.isAscending() ?
                            criteriaBuilder.asc(root.get(property)) :
                            criteriaBuilder.desc(root.get(property)));
                }
            }

            // Apply the sorting
            query.orderBy(orders);

            // Return the final specification
            return criteriaBuilder.conjunction();
        };
    }

//    public static Specification<Membership> combineFilters(Specification<Membership>... specs) {
//        Specification<Membership> combined = Specification.where(specs[0]);
//        for (int i = 1; i < specs.length; i++) {
//            combined = combined.and(specs[i]);
//        }
//        return combined;
//    }
//
//    public static Specification<Membership> combineFiltersWithOr(Specification<Membership>... specs) {
//        Specification<Membership> combined = Specification.where(specs[0]);
//        for (int i = 1; i < specs.length; i++) {
//            combined = combined.or(specs[i]);
//        }
//        return combined;
//    }
//
//    public static Specification<Membership> sortByRoleName(String sortOrder, UUID organizationId) {
//        return (Root<Membership> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
//            // Join Membership -> Member
//            Join<Membership, Member> memberJoin = root.join("member");
//
//            // Join Member -> MemberRole (this is the junction table connecting Member and Role)
//            Join<Member, Role> memberRoleJoin = memberJoin.join("roles", JoinType.INNER); // Using memberRoles directly
//
//
//
//
//            Join<Role, MemberRole> roleJoin = memberRoleJoin.join("memberRoles", JoinType.INNER); // MemberRole -> Role
//
//            Predicate organizationPredicate = cb.equal(roleJoin.get("id").get("organizationId"), organizationId);
//            Predicate organizationMembershipPredicate = cb.equal(root.get("organizationId"), organizationId);
//
//            if ("asc".equalsIgnoreCase(sortOrder)) {
//                query.orderBy(cb.asc(memberRoleJoin.get("name")));
//            } else {
//                query.orderBy(cb.desc(memberRoleJoin.get("name")));
//            }
//
//            // Ensure DISTINCT to prevent duplicates due to the many-to-many join
//            query.distinct(true);
//
//            // Combine the organizationId filter with the role name sort
//            query.where(organizationPredicate, organizationMembershipPredicate);
//
//            return cb.conjunction();
//        };
//    }

}
