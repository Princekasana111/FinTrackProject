package com.fintrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class BudgetDtos {

    public record BudgetRequest(
            @NotBlank(message = "Month is required, format YYYY-MM")
            String month,

            @NotNull @Positive(message = "Limit must be greater than zero")
            BigDecimal limitAmount,

            @NotNull(message = "Category is required")
            Long categoryId
    ) {}

    public record BudgetResponse(
            Long id,
            String month,
            BigDecimal limitAmount,
            Long categoryId,
            String categoryName
    ) {}
}
