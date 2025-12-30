package com.flykraft.livemenu.util;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.model.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static AuthUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        } else if (authentication.getPrincipal() instanceof AuthUser authUser) {
            return authUser;
        } else {
            throw new SecurityException("AuthUser not found in security context");
        }
    }

    public static boolean isAdminLogin() {
        try {
            return getLoggedInUser().getAuthority().equals(Authority.ADMIN);
        } catch (Exception e) {
            return false;
        }
    }
}
