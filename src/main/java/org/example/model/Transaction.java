package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_from", nullable = false)
    private Long accountFrom;

    @Column(name = "account_to", nullable = false)
    private Long accountTo;

    @Column(name = "currency_shortname", nullable = false)
    private Currency currencyShortname;

    @Column(name = "sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal sum;

    @Column(name = "expense_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Column(name = "datetime", nullable = false)
    private ZonedDateTime datetime;

}

