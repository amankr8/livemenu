package com.flykraft.livemenu.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.ErrorResponseDto;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.service.KitchenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class TenantResolutionFilter extends OncePerRequestFilter {
    private final KitchenService kitchenService;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String cleanOrigin = origin.replaceFirst("^https?://", "");
            String subdomain = cleanOrigin.split("\\.")[0];
            if (!subdomain.equalsIgnoreCase(appName)) {
                Kitchen kitchen = kitchenService.loadKitchenBySubdomain(subdomain);
                TenantContext.setKitchenId(kitchen.getId());
            }
            filterChain.doFilter(request, response);
        } catch (ResourceNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getWriter(),
                    new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage())
            );
        }
    }
}
