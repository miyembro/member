package com.rjproj.memberapp.model;

public enum ImageType {
    PROFILE_IMAGE("profile-image"),
    BACKGROUND_IMAGE("background-image");

    private final String value;

    ImageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
