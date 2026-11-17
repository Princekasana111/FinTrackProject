package com.fintrack.service;

import com.fintrack.dto.AnalyticsDtos.*;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Turns raw transactions into the summary numbers and chart-ready data the
 * dashboard needs. We compute in Java (not DB-specific SQL) so the same code
 * works on both H2 and MySQL.
 *
 * {@code @Transactional(readOnly = true)} keeps the DB session open for the
 * whole method, so accessing lazy-loaded associations (like a transaction's
 * category) works even though open-in-view is disabled.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final CurrentUser currentUser;

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    /** Headline cards: total income, total expense, balance, count. */
    public DashboardSummary dashboard() {
        List<Transaction> all = myTransactions();

        BigDecimal income = sumOf(all, TransactionType.INCOME);
        BigDecimal expense = sumOf(all, TransactionType.EXPENSE);

        return new DashboardSummary(
                income,
                expense,
                income.subtract(expense),
                all.size());
    }

    /** Pie chart: total expense grouped by category, biggest first. */
    public List<CategorySummary> expenseByCategory() {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (Transaction t : myTransactions()) {
            if (t.getType() == TransactionType.EXPENSE) {
                totals.merge(t.getCategory().getName(), t.getAmount(), BigDecimal::add);
            }
        }
        return totals.entrySet().stream()
                .map(e -> new CategorySummary(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(CategorySummary::total).reversed())
                .toList();
    }

    /** Line chart: income vs expense per month, chronological. */
    public List<MonthlySummary> monthlyTrend() {
        Map<String, BigDecimal> income = new TreeMap<>();
        Map<String, BigDecimal> expense = new TreeMap<>();

        for (Transaction t : myTransactions()) {
            String month = t.getDate().format(MONTH_FMT);
            if (t.getType() == TransactionType.INCOME) {
                income.merge(month, t.getAmount(), BigDecimal::add);
            } else {
                expense.merge(month, t.getAmount(), BigDecimal::add);
            }
        }

        // Union of all months that appear in either map, sorted.
        Set<String> months = new TreeSet<>();
        months.addAll(income.keySet());
        months.addAll(expense.keySet());

        return months.stream()
                .map(m -> new MonthlySummary(
                        m,
                        income.getOrDefault(m, BigDecimal.ZERO),
                        expense.getOrDefault(m, BigDecimal.ZERO)))
                .toList();
    }

    // ---- helpers ----

    private List<Transaction> myTransactions() {
        return transactionRepository.findByUserIdOrderByDateDesc(currentUser.get().getId());
    }

    private BigDecimal sumOf(List<Transaction> txs, TransactionType type) {
        return txs.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
