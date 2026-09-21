package com.demo.multitenancy.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private final TenantRepository tenants;

    public TenantInterceptor(TenantRepository tenants) {
        this.tenants = tenants;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String host = request.getServerName();
        String slug = subdomainOf(host);

        if (slug == null || !tenants.existsById(slug)) {
            throw new UnknownTenantException(host);
        }

        TenantContext.set(slug);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (TenantContext.isSet()) {
            log.info("tenant={} {} {} {}", TenantContext.get(), request.getMethod(),
                    request.getRequestURI(), response.getStatus());
        }
        TenantContext.clear();
    }

    static String subdomainOf(String host) {
        if (host == null) {
            return null;
        }
        int dot = host.indexOf('.');
        return dot <= 0 ? null : host.substring(0, dot).toLowerCase();
    }
}
