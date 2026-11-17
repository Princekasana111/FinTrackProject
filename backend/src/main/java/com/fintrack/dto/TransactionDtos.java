package com.fintrack.dto;

import com.fintrack.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDtos {

    public record TransactionRequest(
            @NotNull(message = "Amount is required")
            @Positive(message = "Amount must be greater than zero")
            BigDecimal amount,

            @NotNull(message = "Type is required (INCOME or EXPENSE)")
            TransactionType type,

            @NotNull(message = "Date is required")
            LocalDate date,

            @NotNull(message = "Category is required")
            Long categoryId,

            String note
    ) {}

    /** Flattened view sent to the frontend (no nested entities). */
    public record TransactionResponse(
            Long id,
            BigDecimal amount,
            TransactionType type,
            LocalDate date,
            String note,
            Long categoryId,
            String categoryName
    ) {}
}
