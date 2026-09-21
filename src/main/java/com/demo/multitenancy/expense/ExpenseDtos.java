package com.demo.multitenancy.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

final class ExpenseDtos {

    private ExpenseDtos() {
    }

    record CreateExpenseRequest(String description, BigDecimal amount, LocalDate spentOn) {
    }

    record ExpenseResponse(Long id, String tenantId, String description, BigDecimal amount, LocalDate spentOn) {
        static ExpenseResponse from(Expense e) {
            return new ExpenseResponse(e.getId(), e.getTenantId(), e.getDescription(), e.getAmount(), e.getSpentOn());
        }
    }

    record TotalResponse(String tenantId, long count, BigDecimal total) {
    }

    record MeResponse(String tenantId, String name) {
    }
}
