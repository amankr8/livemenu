package com.flykraft.livemenu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public enum Authority implements GrantedAuthority {
    ADMIN(0, "ADMIN"),
    KITCHEN_OWNER(1, "KITCHEN_OWNER"),
    CUSTOMER(2, "CUSTOMER");

    @Getter
    private final Integer id;
    private final String authority;

    @Override
    public @NonNull String getAuthority() {
        return authority;
    }
}
