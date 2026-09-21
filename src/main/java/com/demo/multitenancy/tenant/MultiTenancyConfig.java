package com.demo.multitenancy.tenant;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenancyConfig {

    @Bean
    HibernatePropertiesCustomizer multiTenancyCustomizer(TenantIdentifierResolver resolver,
                                                         TenantConnectionProvider connectionProvider) {
        return props -> {
            props.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
            props.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        };
    }
}
