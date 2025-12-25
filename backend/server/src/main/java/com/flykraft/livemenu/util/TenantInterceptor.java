package com.flykraft.livemenu.util;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.repository.KitchenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {
    @Autowired
    private KitchenRepository kitchenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String host = request.getHeader("Host");
        if (host != null) {
            String subdomain = host.split("\\.")[0];
            kitchenRepository.findBySubdomain(subdomain).ifPresent(k ->
                    TenantContext.setKitchenId(k.getId())
            );
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        TenantContext.clear();
    }
}
