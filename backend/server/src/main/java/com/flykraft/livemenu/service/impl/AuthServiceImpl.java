package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.dto.auth.AuthRequestDto;
import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.model.Authority;
import com.flykraft.livemenu.repository.AuthUserRepository;
import com.flykraft.livemenu.service.AuthService;
import com.flykraft.livemenu.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthUserRepository authUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    @PostConstruct
    public void init() {
        if (authUserRepository.findByUsername(adminUsername).isEmpty()) {
            AuthUser admin = AuthUser.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .authority(Authority.ADMIN)
                    .build();
            authUserRepository.save(admin);
        }
    }

    @Override
    public AuthUser loadUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public AuthUser signup(AuthRequestDto authRequestDto) {
        try {
            loadUserByUsername(authRequestDto.getUsername());
            throw new IllegalArgumentException("Username already exists");
        } catch (UsernameNotFoundException e) {
            String encodedPassword = passwordEncoder.encode(authRequestDto.getPassword());
            AuthUser authUser = AuthUser.builder()
                    .username(authRequestDto.getUsername())
                    .password(encodedPassword)
                    .authority(Authority.CUSTOMER)
                    .build();
            return authUserRepository.save(authUser);
        }
    }

    @Override
    public String login(AuthRequestDto authRequestDto) {
        try {
            AuthUser authUser = loadUserByUsername(authRequestDto.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDto.getUsername(),
                            authRequestDto.getPassword()
                    )
            );
            return jwtService.generateToken(authUser);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
