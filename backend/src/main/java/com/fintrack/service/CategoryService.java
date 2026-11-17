package com.fintrack.service;

import com.fintrack.dto.CategoryDtos.*;
import com.fintrack.entity.Category;
import com.fintrack.entity.User;
import com.fintrack.exception.ApiException;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CurrentUser currentUser;

    public List<CategoryResponse> listMine() {
        return categoryRepository.findByUserId(currentUser.get().getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        User user = currentUser.get();
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(req.name(), user.getId())) {
            throw ApiException.conflict("Category '" + req.name() + "' already exists");
        }
        Category category = Category.builder().name(req.name().trim()).user(user).build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndUserId(id, currentUser.get().getId())
                .orElseThrow(() -> ApiException.notFound("Category not found"));
        categoryRepository.delete(category);
    }

    /** Used by other services to fetch a category the current user owns. */
    public Category getOwnedOrThrow(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ApiException.badRequest("Invalid category"));
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName());
    }
}
