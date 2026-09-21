package com.demo.multitenancy.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    protected Tenant() {
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
