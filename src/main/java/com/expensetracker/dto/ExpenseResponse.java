package com.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponse {

    private Long id;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private String categoryName;
    private LocalDate expenseDate;

    public static ExpenseResponse of(Long id, BigDecimal amount, String description,
                                     Long categoryId, String categoryName, LocalDate expenseDate) {
        ExpenseResponse r = new ExpenseResponse();
        r.id = id;
        r.amount = amount;
        r.description = description;
        r.categoryId = categoryId;
        r.categoryName = categoryName;
        r.expenseDate = expenseDate;
        return r;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }
}
