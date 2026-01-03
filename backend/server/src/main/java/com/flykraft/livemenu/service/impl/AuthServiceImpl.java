package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.model.Authority;
import com.flykraft.livemenu.repository.AuthUserRepository;
import com.flykraft.livemenu.service.AuthService;
import com.flykraft.livemenu.service.JwtService;
import com.flykraft.livemenu.util.AuthUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Value("${spring.security.username}")
    private String adminUsername;

    @Value("${spring.security.password}")
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

    private AuthUser loadUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public AuthUser register(String username, String password, Authority authority) {
        try {
            loadUserByUsername(username);
            throw new IllegalArgumentException("Username already exists");
        } catch (UsernameNotFoundException e) {
            String encodedPassword = passwordEncoder.encode(password);
            AuthUser authUser = AuthUser.builder()
                .username(username)
                .password(encodedPassword)
                .authority(authority)
                .build();
            return authUserRepository.save(authUser);
        }
    }

    @Override
    public String firebaseLogin(String firebaseToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(decodedToken.getUid());
            String phoneNumber = userRecord.getPhoneNumber();
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                throw new IllegalArgumentException("Phone number not found in Firebase user record");
            }

            AuthUser authUser = authUserRepository.findByUsername(phoneNumber)
                .orElseGet(() -> {
                    AuthUser newUser = AuthUser.builder()
                        .username(phoneNumber)
                        .authority(Authority.USER)
                        .build();
                    return authUserRepository.save(newUser);
                });
            return jwtService.generateToken(authUser);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("Invalid Firebase token");
        }
    }

    @Override
    public String login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    username,
                    password
                )
            );
            AuthUser authUser = AuthUtil.getAuthUser(authentication);
            return jwtService.generateToken(authUser);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
