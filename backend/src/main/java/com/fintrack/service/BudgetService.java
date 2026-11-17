package com.fintrack.service;

import com.fintrack.dto.BudgetDtos.*;
import com.fintrack.entity.Budget;
import com.fintrack.entity.Category;
import com.fintrack.entity.User;
import com.fintrack.exception.ApiException;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // read methods stay in a session; writes override below
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;
    private final CurrentUser currentUser;

    public List<BudgetResponse> listMine() {
        return budgetRepository.findByUserId(currentUser.get().getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public BudgetResponse create(BudgetRequest req) {
        User user = currentUser.get();
        Category category = categoryService.getOwnedOrThrow(req.categoryId(), user.getId());

        Budget budget = Budget.builder()
                .month(req.month())
                .limitAmount(req.limitAmount())
                .category(category)
                .user(user)
                .build();

        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public BudgetResponse update(Long id, BudgetRequest req) {
        User user = currentUser.get();
        Budget budget = findOwned(id);
        Category category = categoryService.getOwnedOrThrow(req.categoryId(), user.getId());

        budget.setMonth(req.month());
        budget.setLimitAmount(req.limitAmount());
        budget.setCategory(category);

        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(Long id) {
        budgetRepository.delete(findOwned(id));
    }

    private Budget findOwned(Long id) {
        return budgetRepository.findByIdAndUserId(id, currentUser.get().getId())
                .orElseThrow(() -> ApiException.notFound("Budget not found"));
    }

    private BudgetResponse toResponse(Budget b) {
        return new BudgetResponse(b.getId(), b.getMonth(), b.getLimitAmount(),
                b.getCategory().getId(), b.getCategory().getName());
    }
}
