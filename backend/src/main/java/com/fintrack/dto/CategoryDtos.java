package com.fintrack.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDtos {

    public record CategoryRequest(
            @NotBlank(message = "Category name is required")
            String name
    ) {}

    public record CategoryResponse(
            Long id,
            String name
    ) {}
}
