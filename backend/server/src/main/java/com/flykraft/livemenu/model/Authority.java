package com.flykraft.livemenu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public enum Authority implements GrantedAuthority {
    ADMIN(0, "Admin"),
    KITCHEN_OWNER(1, "Kitchen Owner"),
    CUSTOMER(2, "Customer");

    @Getter
    private final Integer id;
    private final String authority;

    @Override
    public @NonNull String getAuthority() {
        return authority;
    }
}
