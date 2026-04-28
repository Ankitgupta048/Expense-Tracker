package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(Long userId, int year, int month, Long categoryId) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return expenseRepository.findByDateRangeWithCategory(start, end, userId, categoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getById(Long userId, Long id) {
        Expense e = expenseRepository.findByIdWithCategory(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        Category c = e.getCategory();
        return ExpenseResponse.of(
                e.getId(), e.getAmount(), e.getDescription(),
                c.getId(), c.getName(), e.getExpenseDate());
    }

    @Transactional
    public ExpenseResponse create(Long userId, ExpenseRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Expense e = new Expense();
        e.setAmount(req.getAmount());
        e.setDescription(req.getDescription());
        e.setCategory(category);
        e.setUser(user);
        e.setExpenseDate(req.getExpenseDate());
        expenseRepository.save(e);
        return toResponse(e);
    }

    @Transactional
    public ExpenseResponse update(Long userId, Long id, ExpenseRequest req) {
        Expense e = expenseRepository.findByIdWithCategory(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
        e.setAmount(req.getAmount());
        e.setDescription(req.getDescription());
        e.setCategory(category);
        e.setExpenseDate(req.getExpenseDate());
        return toResponse(e);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Expense e = expenseRepository.findByIdWithCategory(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        expenseRepository.delete(e);
    }

    private ExpenseResponse toResponse(Expense e) {
        Category c = e.getCategory();
        return ExpenseResponse.of(
                e.getId(), e.getAmount(), e.getDescription(),
                c.getId(), c.getName(), e.getExpenseDate());
    }
}
