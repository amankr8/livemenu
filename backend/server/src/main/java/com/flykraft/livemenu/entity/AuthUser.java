package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.model.Auditable;
import com.flykraft.livemenu.model.Authority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "auth_users")
public class AuthUser extends Auditable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "au_id")
    private Long id;

    @Column(name = "au_username", unique = true, nullable = false)
    private String username;

    @Column(name = "au_password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "au_authority", nullable = false)
    private Authority authority;

    @Override
    public @NonNull Set<Authority> getAuthorities() {
        return Set.of(authority);
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }
}
