package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    /**
     * Analytics query: total EXPENSE amount grouped by category name.
     * Returns rows of [categoryName, totalAmount].
     */
    @Query("""
           SELECT t.category.name, SUM(t.amount)
           FROM Transaction t
           WHERE t.user.id = :userId AND t.type = com.fintrack.entity.TransactionType.EXPENSE
           GROUP BY t.category.name
           ORDER BY SUM(t.amount) DESC
           """)
    List<Object[]> sumExpensesByCategory(@Param("userId") Long userId);

    /**
     * Analytics query: total amount per month for a given type.
     * We format the date to "YYYY-MM" in Java after fetching, so this stays
     * database-agnostic (works on both H2 and MySQL).
     */
    List<Transaction> findByUserIdAndTypeOrderByDateAsc(Long userId, TransactionType type);
}
