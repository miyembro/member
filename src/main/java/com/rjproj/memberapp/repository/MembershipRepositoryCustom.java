package com.rjproj.memberapp.repository;


import com.rjproj.memberapp.model.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface MembershipRepositoryCustom {
    Page<Membership> findMembershipsByOrganizationIdWithSpecification(UUID organizationId, Specification<Membership> spec, Pageable pageable);
}

