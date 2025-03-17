package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MemberAddressResponse;
import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MemberAddress;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MemberMapper {

    public MemberResponse fromMember(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getProfilePicUrl(),
                member.getBirthDate(),
                member.getLoginType(),
                member.getMemberAddress() == null ? null : fromMemberAddress(member.getMemberAddress()),
                member.getCreatedAt()
        );
    }

    public MemberAddressResponse fromMemberAddress(MemberAddress memberAddress) {
        return new MemberAddressResponse(
                memberAddress.getStreet(),
                memberAddress.getCity(),
                memberAddress.getProvinceState(),
                memberAddress.getPostalCode(),
                memberAddress.getCountry()
        );
    }

    public Member toMember(@Valid MemberRequest memberRequest) {
        return Member.builder()
                .memberId(memberRequest.memberId())
                .firstName(memberRequest.firstName())
                .lastName(memberRequest.lastName())
                .email(memberRequest.email())
                .password(memberRequest.password())
                .phoneNumber(memberRequest.phoneNumber())
                .profilePicUrl(memberRequest.profilePicUrl())
                .birthDate(memberRequest.birthDate())
                .loginType(memberRequest.loginType())
                .memberAddress(memberRequest.memberAddress())
                .build();
    }

}