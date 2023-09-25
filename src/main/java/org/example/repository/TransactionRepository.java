package org.example.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.transaction.ResponseTransactionDto;
import org.example.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
    private final TransactionJpaRepository transactionJpaRepository;

    private final JdbcTemplate jdbcTemplate;

    public List<ResponseTransactionDto> findTransactionsExceedingLimits(Long accountNumber) {
        String sql = """
                SELECT t.id as id,
                t.account_from as accountFrom,
                t.account_to as accountTo,
                t.currency_shortname as currencyShortname,
                t.sum as sum,
                t.expense_category as expenseCategory,
                CAST(t.datetime AT TIME ZONE 'UTC' AS timestamp with time zone) AS datetime,
                l.limit_sum  AS limitSum,
                CAST(l.limit_datetime AT TIME ZONE 'UTC' AS timestamp with time zone) AS limitDatetime,
                l.limit_currency_shortname  AS limitCurrencyShortname
                FROM transactions t
                INNER JOIN limits l ON t.account_from  = l.account_number
                AND (SELECT l2.limit_datetime FROM limits l2
                WHERE l2.account_number = ? AND l2.limit_datetime <= t.datetime
                ORDER BY l2.limit_datetime DESC LIMIT 1) = l.limit_datetime
                WHERE l.limit_exceeded = true""";

        return jdbcTemplate.query(sql, resultSet -> {
            List<ResponseTransactionDto> responseTransactionDtos = new ArrayList<>();
            while (resultSet.next()) {
                ResponseTransactionDto responseTransactionDto = new ResponseTransactionDto();
                responseTransactionDto.setId(resultSet.getLong("id"));
                responseTransactionDto.setAccountFrom(resultSet.getLong("accountFrom"));
                responseTransactionDto.setAccountTo(resultSet.getLong("accountTo"));
                responseTransactionDto.setCurrencyShortname(resultSet.getString("currencyShortname"));
                responseTransactionDto.setSum(resultSet.getBigDecimal("sum"));
                responseTransactionDto.setExpenseCategory(resultSet.getString("expenseCategory"));
                responseTransactionDto.setDatetime(resultSet.getTimestamp("datetime").toInstant().atZone(ZoneId.systemDefault()));
                responseTransactionDto.setLimitSum(resultSet.getBigDecimal("limitSum"));
                responseTransactionDto.setLimitDatetime(resultSet.getTimestamp("limitDatetime").toInstant().atZone(ZoneId.systemDefault()));
                responseTransactionDto.setLimitCurrencyShortname(resultSet.getString("limitCurrencyShortname"));
                responseTransactionDtos.add(responseTransactionDto);
            }
            return responseTransactionDtos;
        }, accountNumber);
    }

    public Transaction save(Transaction transaction) {
        return transactionJpaRepository.save(transaction);
    }

    public Transaction findById(Long id) {
        return transactionJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found, id: " + id));
    }
}

