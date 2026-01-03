package com.flykraft.livemenu.util;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.service.JwtService;
import com.flykraft.livemenu.service.KitchenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final KitchenService kitchenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        Long resolvedKitchenId = null;

        if (origin != null) {
            String cleanOrigin = origin.replaceFirst("^https?://", "");
            String subdomain = cleanOrigin.split("\\.")[0];

            try {
                Kitchen kitchen = kitchenService.loadKitchenBySubdomain(subdomain);
                resolvedKitchenId = kitchen.getId();
            } catch (ResourceNotFoundException e) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getWriter().write("Not Found: Kitchen does not exist");
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (resolvedKitchenId != null) {
                TenantContext.setKitchenId(resolvedKitchenId);
            }
            try {
                filterChain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }
            return;
        }

        try {
            String jwtToken = authHeader.substring(7);
            String username = jwtService.extractUsername(jwtToken);
            Long kitchenIdFromJwt = jwtService.extractKitchenId(jwtToken);

            if (kitchenIdFromJwt != null && resolvedKitchenId != null && !kitchenIdFromJwt.equals(resolvedKitchenId)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Access Denied: You are not authorized for this kitchen.");
                return;
            }

            if (resolvedKitchenId != null) {
                TenantContext.setKitchenId(resolvedKitchenId);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Authentication failed.");
            throw e;
        } finally {
            TenantContext.clear();
        }
    }
}
