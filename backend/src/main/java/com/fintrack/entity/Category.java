package com.fintrack.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A spending/income category (e.g. "Food", "Salary", "Rent").
 * Each category belongs to one user — categories are private per account.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * The owner. FetchType.LAZY means the user isn't loaded from the DB
     * unless we actually access it (a performance best-practice).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
