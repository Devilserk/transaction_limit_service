package org.example.dto.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiveTransactionDto {
    @NotNull
    private Long accountFrom;
    @NotNull
    private Long accountTo;
    @NotNull
    private Currency currencyShortname;
    @NotNull
    private BigDecimal sum;
    @NotNull
    private ExpenseCategory expenseCategory;
    @NotNull
    private ZonedDateTime datetime;
}
