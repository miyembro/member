package com.rjproj.memberapp.dto;

import java.time.LocalDate;
import java.util.Date;

public record GoogleInfo(
        String email,
        String firstName,
        String lastName,
        LocalDate birthdate,
        String photoUrl
) {
}
