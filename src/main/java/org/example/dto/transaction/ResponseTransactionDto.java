package org.example.dto.transaction;

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
public class ResponseTransactionDto {
    private Long id;
    private Long accountFrom;
    private Long accountTo;
    private Currency currencyShortname;
    private BigDecimal sum;
    private ExpenseCategory expenseCategory;
    private ZonedDateTime datetime;
    private BigDecimal limitSum;
    private ZonedDateTime limitDatetime;
    private Currency limitCurrencyShortname;
}
