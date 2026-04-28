package com.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailySpendPoint {

    private LocalDate date;
    private BigDecimal amount;

    public DailySpendPoint(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
