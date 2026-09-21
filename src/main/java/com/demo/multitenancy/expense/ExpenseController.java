package com.demo.multitenancy.expense;

import com.demo.multitenancy.expense.ExpenseDtos.CreateExpenseRequest;
import com.demo.multitenancy.expense.ExpenseDtos.ExpenseResponse;
import com.demo.multitenancy.expense.ExpenseDtos.MeResponse;
import com.demo.multitenancy.expense.ExpenseDtos.TotalResponse;
import com.demo.multitenancy.tenant.Tenant;
import com.demo.multitenancy.tenant.TenantContext;
import com.demo.multitenancy.tenant.TenantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseRepository expenses;
    private final TenantRepository tenants;

    public ExpenseController(ExpenseRepository expenses, TenantRepository tenants) {
        this.expenses = expenses;
        this.tenants = tenants;
    }

    @GetMapping("/me")
    public MeResponse me() {
        Tenant tenant = tenants.findById(TenantContext.get()).orElseThrow();
        return new MeResponse(tenant.getSlug(), tenant.getName());
    }

    @GetMapping("/expenses")
    public List<ExpenseResponse> list() {
        return expenses.findAllByOrderBySpentOnAsc().stream().map(ExpenseResponse::from).toList();
    }

    @GetMapping("/expenses/total")
    public TotalResponse total() {
        return new TotalResponse(TenantContext.get(), expenses.count(), expenses.total());
    }

    @GetMapping("/expenses/{id}")
    public ExpenseResponse get(@PathVariable Long id) {
        return expenses.findById(id)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> new NoSuchElementException("expense " + id + " not found"));
    }

    @PostMapping("/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@RequestBody CreateExpenseRequest body) {
        if (body.description() == null || body.description().isBlank()
                || body.amount() == null || body.spentOn() == null) {
            throw new IllegalArgumentException("description, amount and spentOn are required");
        }
        Expense saved = expenses.save(new Expense(body.description(), body.amount(), body.spentOn()));
        return ExpenseResponse.from(saved);
    }
}
