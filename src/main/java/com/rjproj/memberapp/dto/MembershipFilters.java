package com.rjproj.memberapp.dto;

import java.util.Date;
import java.util.List;

public record MembershipFilters(
        String memberFirstName,
        String memberEmail,
        String memberMemberAddressCity,
        String memberMemberAddressCountry,
        List<String> membershipStatusNames,
        List<String> membershipTypeNames,
        List<String> roleNames,
        List<Date> startDates,
        List<Date> endDates
) {
}
