package com.rjproj.memberapp.security;

import com.rjproj.memberapp.model.Member;

public interface ContextHelper {

    /**
     * @return the current user for the request or null.
     */
    Member currentMember();

    default boolean currentUserHasPermission(String permissionName) {
        Member member = this.currentMember();
        if (this.currentMember() == null) {
            return false;
        }
        return member.getPermissionNames().contains(permissionName);
    }

}
