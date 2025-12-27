package com.flykraft.livemenu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public enum Authority implements GrantedAuthority {
    ADMIN(0, "ADMIN"),
    KITCHEN_OWNER(1, "KITCHEN_OWNER"),
    USER(2, "USER");

    @Getter
    private final Integer id;
    private final String authority;

    @Override
    public @NonNull String getAuthority() {
        return authority;
    }
}
