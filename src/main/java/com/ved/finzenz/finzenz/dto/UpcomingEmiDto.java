package com.ved.finzenz.finzenz.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UpcomingEmiDto {
    private Long loanId;
    private String lender;
    private BigDecimal amount;
    private LocalDate dueDate;
}
