package com.rjproj.memberapp.repository;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.Membership;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;


@Repository
public class MembershipRepositoryCustomImpl implements MembershipRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Membership> findMembershipsByOrganizationIdWithSpecification(UUID organizationId, Specification<Membership> spec, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Membership> query = criteriaBuilder.createQuery(Membership.class);
        Root<Membership> root = query.from(Membership.class);

        // Apply the specification filters
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);
        query.where(predicate);

        // Apply sorting by role name
        Join<Membership, Member> memberJoin = root.join("member", JoinType.LEFT);
        Join<Member, MemberRole> roleJoin = memberJoin.join("roles", JoinType.LEFT);
        query.orderBy(criteriaBuilder.asc(roleJoin.get("name"))); // Sort by role name

        TypedQuery<Membership> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Membership> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, totalRows);
    }
}