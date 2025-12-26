package com.flykraft.livemenu.service;

import com.flykraft.livemenu.entity.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String extractUsername(String token);

    Long extractKitchenId(String token);

    String generateToken(AuthUser authUser);

    String generateToken(Map<String, Object> extraClaims, String username);

    Long getExpirationTime();

    Boolean isTokenValid(String token, UserDetails userDetails);
}
