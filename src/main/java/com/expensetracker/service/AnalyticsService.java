package com.expensetracker.service;

import com.expensetracker.dto.AnalyticsSummaryResponse;
import com.expensetracker.dto.DailySpendPoint;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;

    public AnalyticsService(ExpenseRepository expenseRepository, BudgetService budgetService) {
        this.expenseRepository = expenseRepository;
        this.budgetService = budgetService;
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse summary(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal total = expenseRepository.sumAmountBetween(userId, start, end);
        if (total == null) {
            total = BigDecimal.ZERO;
        }

        BigDecimal budgetLimit = budgetService.getLimitOrNull(userId, year, month);
        boolean budgetSet = budgetLimit != null;

        AnalyticsSummaryResponse resp = new AnalyticsSummaryResponse();
        resp.setYear(year);
        resp.setMonth(month);
        resp.setTotalSpent(total.setScale(2, RoundingMode.HALF_UP));
        resp.setBudgetSet(budgetSet);
        resp.setBudgetLimit(budgetLimit != null ? budgetLimit.setScale(2, RoundingMode.HALF_UP) : null);
        if (budgetSet) {
            BigDecimal remaining = budgetLimit.subtract(total).setScale(2, RoundingMode.HALF_UP);
            resp.setRemaining(remaining);
            resp.setOverBudget(total.compareTo(budgetLimit) > 0);
        } else {
            resp.setRemaining(null);
            resp.setOverBudget(false);
        }

        List<AnalyticsSummaryResponse.CategoryTotal> byCat = new ArrayList<>();
        for (Object[] row : expenseRepository.sumByCategoryBetween(userId, start, end)) {
            String name = (String) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            byCat.add(new AnalyticsSummaryResponse.CategoryTotal(name, amt.setScale(2, RoundingMode.HALF_UP)));
        }
        resp.setByCategory(byCat);
        return resp;
    }

    @Transactional(readOnly = true)
    public List<DailySpendPoint> dailySeries(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<DailySpendPoint> points = new ArrayList<>();
        for (Object[] row : expenseRepository.sumByDayBetween(userId, start, end)) {
            LocalDate d = (LocalDate) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            points.add(new DailySpendPoint(d, amt.setScale(2, RoundingMode.HALF_UP)));
        }
        return points;
    }
}
