package com.expensetracker.controller;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.expensetracker.security.AuthUtil.requireUserId;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public Map<String, Object> get(
            HttpServletRequest request,
            @RequestParam int year,
            @RequestParam int month) {
        return budgetService.getBudget(requireUserId(request), year, month);
    }

    @PutMapping
    public Map<String, Object> save(HttpServletRequest httpRequest, @Valid @RequestBody BudgetRequest request) {
        return budgetService.upsert(requireUserId(httpRequest), request);
    }
}
