package com.fintrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A monthly spending limit for a given category.
 * Example: "Food budget for 2026-07 is 5000".
 */
@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Format: "YYYY-MM", e.g. "2026-07".
     * Mapped to "budget_month" because MONTH is a reserved keyword in some
     * databases (e.g. H2), which would break table creation.
     */
    @Column(name = "budget_month", nullable = false)
    private String month;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal limitAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
