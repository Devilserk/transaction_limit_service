package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
@Entity
@Table(name = "limits")
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "limit_sum", precision = 10, scale = 2, nullable = false)
    private BigDecimal limitSum;

    @Column(name = "limit_datetime")
    private ZonedDateTime limitDatetime;

    @Column(name = "limit_currency_shortname", nullable = false)
    private Currency limitCurrencyShortname;

    @Column(name = "remainder", precision = 10, scale = 2, nullable = false)
    private BigDecimal remainder;

    @Column(name = "limit_exceeded", nullable = false)
    private Boolean limitExceeded;

    @Column(name = "expense_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Version
    private Long version;
}
