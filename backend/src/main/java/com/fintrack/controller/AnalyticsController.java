package com.fintrack.controller;

import com.fintrack.dto.AnalyticsDtos.*;
import com.fintrack.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return analyticsService.dashboard();
    }

    @GetMapping("/by-category")
    public List<CategorySummary> byCategory() {
        return analyticsService.expenseByCategory();
    }

    @GetMapping("/monthly-trend")
    public List<MonthlySummary> monthlyTrend() {
        return analyticsService.monthlyTrend();
    }
}
