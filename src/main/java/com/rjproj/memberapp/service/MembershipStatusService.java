package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MembershipStatusResponse;
import com.rjproj.memberapp.mapper.MembershipStatusMapper;
import com.rjproj.memberapp.model.MembershipStatus;
import com.rjproj.memberapp.repository.MembershipStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipStatusService {

    private final MembershipStatusRepository membershipStatusRepository;

    private final MembershipStatusMapper membershipStatusMapper;

    public List<MembershipStatusResponse> getApprovedMembershipStatuses() {
        List<String> memberMembershipStatuses = new ArrayList<>();
        memberMembershipStatuses.add("Active");
        memberMembershipStatuses.add("Expired");
        memberMembershipStatuses.add("Cancelled");
        memberMembershipStatuses.add("Owner");
        List<MembershipStatus> membershipStatuses = membershipStatusRepository.findByNameIn(memberMembershipStatuses);
        return membershipStatuses.stream().map(membershipStatusMapper::fromMembershipStatus).collect(Collectors.toList()) ;
    }

    public List<MembershipStatusResponse> getJoinRequestsMembershipStatuses() {
        List<String> memberMembershipStatuses = new ArrayList<>();
        memberMembershipStatuses.add("Pending");
        memberMembershipStatuses.add("Denied");
        List<MembershipStatus> membershipStatuses = membershipStatusRepository.findByNameIn(memberMembershipStatuses);
        return membershipStatuses.stream().map(membershipStatusMapper::fromMembershipStatus).collect(Collectors.toList()) ;
    }

}
