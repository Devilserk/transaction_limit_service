package org.example.repository;

import org.example.dto.transaction.ResponseTransactionDto;
import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {
//    #TODO
//    Разобраться почему не работает
    @Query(nativeQuery = true, value = """
            SELECT t.id as id,
            t.account_from as accountFrom,
            t.account_to as accountTo,
            t.currency_shortname as currencyShortname,
            t.sum as sum,
            t.expense_category as expenseCategory,
            t.datetime as datetime,
            l.limit_sum as limitSum,
            l.limit_datetime as limitDatetime,
            l.limit_currency_shortname as limitCurrencyShortname
            FROM transactions t
            INNER JOIN limits l ON t.account_from  = l.account_number
            AND (SELECT l2.limit_datetime FROM limits l2
            WHERE l2.account_number = ? AND l2.limit_datetime <= t.datetime
            ORDER BY l2.limit_datetime DESC LIMIT 1) = l.limit_datetime
            WHERE l.limit_exceeded = true
            """)
    List<ResponseTransactionDto> findTransactionsExceedingLimits(Long accountNumber);
}
