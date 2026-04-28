package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class AnalyticsSummaryResponse {

    private int year;
    private int month;
    private BigDecimal totalSpent;
    private BigDecimal budgetLimit;
    private boolean budgetSet;
    private boolean overBudget;
    private BigDecimal remaining;
    private List<CategoryTotal> byCategory;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public boolean isBudgetSet() {
        return budgetSet;
    }

    public void setBudgetSet(boolean budgetSet) {
        this.budgetSet = budgetSet;
    }

    public boolean isOverBudget() {
        return overBudget;
    }

    public void setOverBudget(boolean overBudget) {
        this.overBudget = overBudget;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public void setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
    }

    public List<CategoryTotal> getByCategory() {
        return byCategory;
    }

    public void setByCategory(List<CategoryTotal> byCategory) {
        this.byCategory = byCategory;
    }

    public static class CategoryTotal {
        private String categoryName;
        private BigDecimal total;

        public CategoryTotal(String categoryName, BigDecimal total) {
            this.categoryName = categoryName;
            this.total = total;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }
}
