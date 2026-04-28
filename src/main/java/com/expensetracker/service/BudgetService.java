package com.expensetracker.service;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.entity.MonthlyBudget;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.MonthlyBudgetRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class BudgetService {

    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final UserRepository userRepository;

    public BudgetService(MonthlyBudgetRepository monthlyBudgetRepository, UserRepository userRepository) {
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBudget(Long userId, int year, int month) {
        return monthlyBudgetRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .map(b -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("year", b.getYear());
                    m.put("month", b.getMonth());
                    m.put("amountLimit", b.getAmountLimit());
                    m.put("set", true);
                    return m;
                })
                .orElseGet(() -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("year", year);
                    m.put("month", month);
                    m.put("amountLimit", null);
                    m.put("set", false);
                    return m;
                });
    }

    @Transactional
    public Map<String, Object> upsert(Long userId, BudgetRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        MonthlyBudget b = monthlyBudgetRepository
                .findByUserIdAndYearAndMonth(userId, req.getYear(), req.getMonth())
                .orElseGet(MonthlyBudget::new);
        b.setYear(req.getYear());
        b.setMonth(req.getMonth());
        b.setUser(user);
        b.setAmountLimit(req.getAmountLimit());
        monthlyBudgetRepository.save(b);
        Map<String, Object> m = new HashMap<>();
        m.put("year", b.getYear());
        m.put("month", b.getMonth());
        m.put("amountLimit", b.getAmountLimit());
        m.put("set", true);
        return m;
    }

    @Transactional(readOnly = true)
    public BigDecimal getLimitOrNull(Long userId, int year, int month) {
        return monthlyBudgetRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .map(MonthlyBudget::getAmountLimit)
                .orElse(null);
    }
}
