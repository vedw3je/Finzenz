package com.ved.finzenz.finzenz.LoanService.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UpcomingEmiDto {


    private Long loanId;

    @NotNull(message = "Lender name cannot be null")
    private String lender;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;


    private LocalDate dueDate;
}
