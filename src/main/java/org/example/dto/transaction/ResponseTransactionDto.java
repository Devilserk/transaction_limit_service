package org.example.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTransactionDto {
    private Long id;
    private Long accountFrom;
    private Long accountTo;
    private String currencyShortname;
    private BigDecimal sum;
    private String expenseCategory;
    private ZonedDateTime datetime;
    private BigDecimal limitSum;
    private ZonedDateTime limitDatetime;
    private String limitCurrencyShortname;
}
