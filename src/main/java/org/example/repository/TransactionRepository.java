package org.example.repository;

import org.example.dto.transaction.ResponseTransactionDto;
import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            SELECT t.accountFrom, t.accountTo, t.currencyShortname, t.sum, t.expenseCategory, t.datetime,
            l.limitSum AS limitSum, l.limitDatetime AS limitDatetime, l.limitCurrencyShortname AS limitCurrencyShortname
            FROM Transaction t
            INNER JOIN Limit l ON t.accountFrom = l.accountNumber
            AND t.datetime BETWEEN l.limitDatetime AND COALESCE(l.limitDatetime, CURRENT_TIMESTAMP)
            WHERE l.limitExceeded = true AND t.accountFrom = :accountNumber""")
    List<ResponseTransactionDto> findTransactionsExceedingLimits(Long accountNumber);
}
