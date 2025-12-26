package com.flykraft.livemenu.util;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.model.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static AuthUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not logged in");
        }
        return (AuthUser) authentication.getPrincipal();
    }

    public static boolean isAdminLogin() {
        return getLoggedInUser().getAuthority().equals(Authority.ADMIN);
    }
}
