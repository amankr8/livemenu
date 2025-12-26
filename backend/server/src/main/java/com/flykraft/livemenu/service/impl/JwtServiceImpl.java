package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.entity.KitchenOwner;
import com.flykraft.livemenu.model.Authority;
import com.flykraft.livemenu.repository.KitchenOwnerRepository;
import com.flykraft.livemenu.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {
    private final KitchenOwnerRepository kitchenOwnerRepository;

    @Value("${spring.security.jwt.secret}")
    private String JWT_SECRET_KEY;

    @Value("${spring.security.jwt.expiration}")
    private Long JWT_EXPIRATION;

    private final String KITCHEN_ID_CLAIM = "kitchenId";

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Long extractKitchenId(String token) {
        return extractAllClaims(token).get(KITCHEN_ID_CLAIM, Long.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateToken(AuthUser authUser) {
        Map<String, Object> claims = new HashMap<>();

        if (authUser.getAuthorities().contains(Authority.KITCHEN_OWNER)) {
            KitchenOwner kitchenOwner = kitchenOwnerRepository.findByAuthUser(authUser)
                    .orElseThrow(() -> new IllegalStateException("Kitchen owner not found for user: " + authUser.getUsername()));
            claims.put(KITCHEN_ID_CLAIM, kitchenOwner.getKitchen().getId());
        }

        return generateToken(claims, authUser.getUsername());
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return buildToken(extraClaims, username, JWT_EXPIRATION);
    }

    @Override
    public Long getExpirationTime() {
        return JWT_EXPIRATION;
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}
