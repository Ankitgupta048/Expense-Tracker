package com.expensetracker.controller;

import com.expensetracker.dto.AnalyticsSummaryResponse;
import com.expensetracker.dto.DailySpendPoint;
import com.expensetracker.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.expensetracker.security.AuthUtil.requireUserId;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public AnalyticsSummaryResponse summary(
            HttpServletRequest request,
            @RequestParam int year,
            @RequestParam int month) {
        return analyticsService.summary(requireUserId(request), year, month);
    }

    @GetMapping("/daily")
    public List<DailySpendPoint> daily(
            HttpServletRequest request,
            @RequestParam int year,
            @RequestParam int month) {
        return analyticsService.dailySeries(requireUserId(request), year, month);
    }
}
