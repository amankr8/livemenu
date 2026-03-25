package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.entity.KitchenOwner;
import com.flykraft.livemenu.model.Authority;
import com.flykraft.livemenu.repository.KitchenOwnerRepository;
import com.flykraft.livemenu.service.JwtService;
import com.flykraft.livemenu.util.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
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

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Long extractClaim(String token, String claim) {
        return extractAllClaims(token).get(claim, Long.class);
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
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtConstants.ROLE_CLAIM, authUser.getAuthority());
        if (authUser.getAuthority().equals(Authority.KITCHEN_OWNER)) {
            KitchenOwner kitchenOwner = kitchenOwnerRepository.findByAuthUser(authUser).orElse(null);
            if (kitchenOwner != null) {
                Long currentKitchenId = TenantContext.getKitchenId();
                if (!kitchenOwner.getKitchen().getId().equals(currentKitchenId)) {
                    throw new SecurityException("Invalid Kitchen Owner");
                }
                extraClaims.put(JwtConstants.KITCHEN_ID_CLAIM, currentKitchenId);
            }
        }
        return generateToken(extraClaims, authUser.getUsername());
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return buildToken(extraClaims, username, JWT_EXPIRATION);
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
