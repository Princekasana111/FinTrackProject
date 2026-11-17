package com.fintrack.dto;

import java.math.BigDecimal;

public class AnalyticsDtos {

    /** One slice of the "spending by category" pie chart. */
    public record CategorySummary(
            String category,
            BigDecimal total
    ) {}

    /** One point on the "monthly trend" line chart. */
    public record MonthlySummary(
            String month,      // "YYYY-MM"
            BigDecimal income,
            BigDecimal expense
    ) {}

    /** The headline numbers shown on the dashboard cards. */
    public record DashboardSummary(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal balance,
            long transactionCount
    ) {}
}
