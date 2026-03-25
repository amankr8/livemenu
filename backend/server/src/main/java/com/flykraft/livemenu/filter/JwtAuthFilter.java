package com.flykraft.livemenu.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.ErrorResponseDto;
import com.flykraft.livemenu.service.JwtService;
import com.flykraft.livemenu.util.JwtConstants;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    private static final List<RequestMatcher> PUBLIC_MATCHERS = List.of(
            PathPatternRequestMatcher.withDefaults().matcher("/api/v1/auth/**"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/v1/menu/**"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/v1/kitchens/**"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/v1/categories/**")
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return PUBLIC_MATCHERS
                .stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwtToken = authHeader.substring(7);
            String username = jwtService.extractUsername(jwtToken);
            Long kitchenIdFromJwt = jwtService.extractClaim(jwtToken, JwtConstants.KITCHEN_ID_CLAIM);

            if (kitchenIdFromJwt != null) {
                TenantContext.setKitchenId(kitchenIdFromJwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getWriter(),
                    new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage())
            );
        } finally {
            TenantContext.clear();
        }
    }
}

