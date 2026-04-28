package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id AND e.user.id = :userId")
    Optional<Expense> findByIdWithCategory(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            SELECT e FROM Expense e JOIN FETCH e.category c
            WHERE e.expenseDate BETWEEN :start AND :end
            AND e.user.id = :userId
            AND (:categoryId IS NULL OR c.id = :categoryId)
            ORDER BY e.expenseDate DESC, e.id DESC
            """)
    List<Expense> findByDateRangeWithCategory(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumAmountBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT c.name, COALESCE(SUM(e.amount), 0) FROM Expense e JOIN e.category c WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end GROUP BY c.id, c.name ORDER BY c.name")
    List<Object[]> sumByCategoryBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.expenseDate, COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end GROUP BY e.expenseDate ORDER BY e.expenseDate")
    List<Object[]> sumByDayBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
