package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static com.expensetracker.security.AuthUtil.requireUserId;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponse> list(
            HttpServletRequest request,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Long categoryId) {
        return expenseService.list(requireUserId(request), year, month, categoryId);
    }

    @GetMapping("/{id}")
    public ExpenseResponse get(HttpServletRequest request, @PathVariable Long id) {
        return expenseService.getById(requireUserId(request), id);
    }

    @PostMapping
    public ExpenseResponse create(HttpServletRequest httpRequest, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.create(requireUserId(httpRequest), request);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(HttpServletRequest httpRequest, @PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(requireUserId(httpRequest), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(HttpServletRequest request, @PathVariable Long id) {
        expenseService.delete(requireUserId(request), id);
    }
}
