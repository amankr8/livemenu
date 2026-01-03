package com.flykraft.livemenu.util;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.model.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static boolean isAdminLogin() {
        try {
            return getLoggedInUser().getAuthority().equals(Authority.ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public static AuthUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return getAuthUser(authentication);
        } catch (IllegalStateException e) {
            throw new SecurityException("User is not logged in.");
        }
    }

    public static AuthUser getAuthUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Empty Auth Context.");
        } else if (authentication.getPrincipal() instanceof AuthUser authUser) {
            return authUser;
        } else {
            throw new IllegalStateException("Invalid Auth Context.");
        }
    }
}
