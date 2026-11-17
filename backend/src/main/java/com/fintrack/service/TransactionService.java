package com.fintrack.service;

import com.fintrack.dto.TransactionDtos.*;
import com.fintrack.entity.Category;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.User;
import com.fintrack.exception.ApiException;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // read methods stay in a session; writes override below
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final CurrentUser currentUser;

    public List<TransactionResponse> listMine() {
        return transactionRepository.findByUserIdOrderByDateDesc(currentUser.get().getId())
                .stream().map(this::toResponse).toList();
    }

    public TransactionResponse getOne(Long id) {
        return toResponse(findOwned(id));
    }

    @Transactional
    public TransactionResponse create(TransactionRequest req) {
        User user = currentUser.get();
        Category category = categoryService.getOwnedOrThrow(req.categoryId(), user.getId());

        Transaction tx = Transaction.builder()
                .amount(req.amount())
                .type(req.type())
                .date(req.date())
                .note(req.note())
                .category(category)
                .user(user)
                .build();

        return toResponse(transactionRepository.save(tx));
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest req) {
        User user = currentUser.get();
        Transaction tx = findOwned(id);
        Category category = categoryService.getOwnedOrThrow(req.categoryId(), user.getId());

        tx.setAmount(req.amount());
        tx.setType(req.type());
        tx.setDate(req.date());
        tx.setNote(req.note());
        tx.setCategory(category);

        return toResponse(transactionRepository.save(tx));
    }

    @Transactional
    public void delete(Long id) {
        transactionRepository.delete(findOwned(id));
    }

    // ---- helpers ----

    private Transaction findOwned(Long id) {
        return transactionRepository.findByIdAndUserId(id, currentUser.get().getId())
                .orElseThrow(() -> ApiException.notFound("Transaction not found"));
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getAmount(), t.getType(), t.getDate(),
                t.getNote(), t.getCategory().getId(), t.getCategory().getName());
    }
}
