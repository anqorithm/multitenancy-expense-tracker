package com.demo.multitenancy.expense;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.TenantId;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "spent_on", nullable = false)
    private LocalDate spentOn;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    protected Expense() {
    }

    public Expense(String description, BigDecimal amount, LocalDate spentOn) {
        this.description = description;
        this.amount = amount;
        this.spentOn = spentOn;
    }

    public Long getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getSpentOn() {
        return spentOn;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
