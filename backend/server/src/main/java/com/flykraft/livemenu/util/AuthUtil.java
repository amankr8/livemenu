package com.flykraft.livemenu.util;

import com.flykraft.livemenu.entity.AuthUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthUtil {
    public static AuthUser getLoggedInUser() {
        return (AuthUser) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}
