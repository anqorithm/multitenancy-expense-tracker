package com.demo.multitenancy.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOrderBySpentOnAsc();

    @Query("select coalesce(sum(e.amount), 0) from Expense e")
    BigDecimal total();
}
