package com.ved.finzenz.finzenz.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budgets")
public class Budget implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "User Id cannot be null")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull(message = "Category cannot be null")
    @Column(nullable = false, length = 100)
    private String category;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;
}
