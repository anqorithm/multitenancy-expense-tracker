package com.demo.multitenancy.debug;

import com.demo.multitenancy.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private static final String SQL = "select count(*), coalesce(sum(amount), 0) from expenses";

    private final EntityManager em;

    public DebugController(EntityManager em) {
        this.em = em;
    }

    @GetMapping("/leak-test")
    @Transactional(readOnly = true)
    public Map<String, Object> leakTest() {
        Object[] row = (Object[]) em.createNativeQuery(SQL).getSingleResult();
        String dbTenant = (String) em.createNativeQuery("select current_setting('app.tenant_id', true)").getSingleResult();
        return Map.of(
                "tenantId", TenantContext.get(),
                "sql", SQL,
                "rowsVisible", ((Number) row[0]).longValue(),
                "sumVisible", (BigDecimal) row[1],
                "postgresSessionTenant", dbTenant
        );
    }
}
