package com.flykraft.livemenu.util;

import com.flykraft.livemenu.config.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantFilterAspect {
    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.flykraft.livemenu.service..*(..))")
    public void enableFilter() {
        Long kitchenId = TenantContext.getKitchenId();
        if (kitchenId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("kitchenFilter").setParameter("kitchenId", kitchenId);
        }
    }
}
