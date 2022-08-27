package com.bossware.jwtdemoapp.core.enums;

import com.bossware.jwtdemoapp.core.constants.AuthorityConstants;

public enum Role {
    ROLE_USER(AuthorityConstants.USER_AUTHORITIES),
    ROLE_HR(AuthorityConstants.HR_AUTHORITIES),
    ROLE_MANAGER(AuthorityConstants.MANAGER_AUTHORITIES),
    ROLE_ADMIN(AuthorityConstants.ADMIN_AUTHORITIES),
    ROLE_SUPER_USER(AuthorityConstants.SUPER_USER_AUTHORITIES);

    private String[] authorities;

    Role(String ...authorities){
        this.authorities=authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }


}
