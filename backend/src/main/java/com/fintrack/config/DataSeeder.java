package com.fintrack.config;

import com.fintrack.entity.*;
import com.fintrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Seeds a demo account on startup so the app has something to show immediately.
 *
 *   Login with:  email = demo@fintrack.com   password = demo123
 *
 * Runs only if the demo user doesn't already exist, so it's safe to restart.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;

    // Deterministic randomness (fixed seed) so demo data looks the same each run.
    private final Random random = new Random(42);

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("demo@fintrack.com")) {
            return; // already seeded
        }

        User demo = userRepository.save(User.builder()
                .name("Demo User")
                .email("demo@fintrack.com")
                .password(passwordEncoder.encode("demo123"))
                .role(Role.USER)
                .build());

        // Categories
        Category salary = saveCategory("Salary", demo);
        Category food = saveCategory("Food", demo);
        Category rent = saveCategory("Rent", demo);
        Category transport = saveCategory("Transport", demo);
        Category entertainment = saveCategory("Entertainment", demo);
        Category shopping = saveCategory("Shopping", demo);

        // Create ~6 months of transactions
        LocalDate today = LocalDate.now();
        for (int monthsAgo = 5; monthsAgo >= 0; monthsAgo--) {
            LocalDate monthStart = today.minusMonths(monthsAgo).withDayOfMonth(1);

            // Monthly salary (income)
            saveTx(new BigDecimal("60000"), TransactionType.INCOME, monthStart.plusDays(1),
                    salary, demo, "Monthly salary");

            // Recurring rent
            saveTx(new BigDecimal("15000"), TransactionType.EXPENSE, monthStart.plusDays(2),
                    rent, demo, "Apartment rent");

            // A handful of variable expenses
            saveTx(randomAmount(2000, 6000), TransactionType.EXPENSE, monthStart.plusDays(5),
                    food, demo, "Groceries");
            saveTx(randomAmount(500, 2500), TransactionType.EXPENSE, monthStart.plusDays(9),
                    transport, demo, "Cab & fuel");
            saveTx(randomAmount(800, 3000), TransactionType.EXPENSE, monthStart.plusDays(14),
                    entertainment, demo, "Movies & dining");
            saveTx(randomAmount(1000, 5000), TransactionType.EXPENSE, monthStart.plusDays(20),
                    shopping, demo, "Online shopping");
        }

        // A budget or two for the current month
        String currentMonth = today.toString().substring(0, 7); // "YYYY-MM"
        budgetRepository.save(Budget.builder()
                .month(currentMonth).limitAmount(new BigDecimal("5000"))
                .category(food).user(demo).build());
        budgetRepository.save(Budget.builder()
                .month(currentMonth).limitAmount(new BigDecimal("4000"))
                .category(entertainment).user(demo).build());

        System.out.println("""
                =====================================================
                 FinTrack demo data ready.
                 Login:  demo@fintrack.com  /  demo123
                =====================================================""");
    }

    private Category saveCategory(String name, User user) {
        return categoryRepository.save(Category.builder().name(name).user(user).build());
    }

    private void saveTx(BigDecimal amount, TransactionType type, LocalDate date,
                        Category category, User user, String note) {
        transactionRepository.save(Transaction.builder()
                .amount(amount).type(type).date(date)
                .category(category).user(user).note(note).build());
    }

    private BigDecimal randomAmount(int min, int max) {
        int value = min + random.nextInt(max - min);
        return BigDecimal.valueOf(value);
    }
}
