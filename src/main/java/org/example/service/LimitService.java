package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.limit.CreateLimitDto;
import org.example.dto.limit.ResponseLimitDto;
import org.example.mapper.LimitMapper;
import org.example.model.Limit;
import org.example.model.Transaction;
import org.example.repository.LimitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {
    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;
    private final ExchangeRateService exchangeRateService;
    private static final String USD_CURRENCY_CODE = "USD";

    @Transactional
    public ResponseLimitDto createLimit(CreateLimitDto createLimitDto) {
        Limit limit = limitMapper.createLimitDtoToEntity(createLimitDto);
        limit.setRemainder(limit.getLimitSum());
        limit.setLimitDatetime(ZonedDateTime.now());
        limit.setLimitExceeded(false);
        limit.setLimitCurrencyShortname(Currency.getInstance("USD"));
        limitRepository.save(limit);
        log.info("Created limit: {}", limit);
        return limitMapper.toResponseDto(limit);
    }

    @Transactional
    public Limit handleTransactionAndReturnLimit(Transaction transaction) {
        Limit limit = getActualLimitByAccountNumber(transaction.getAccountFrom())
                .orElseGet(() -> createDefaultLimit(transaction));

        BigDecimal transactionSum;
        transactionSum = exchangeRateService.convertTransactionSumToUSD(transaction);
        if (transactionSum.compareTo(limit.getRemainder()) > 0) {
            limit.setLimitExceeded(true);
        }
        limit.setRemainder(limit.getRemainder().subtract(transactionSum));

        log.info("Updated limit, id: {}", limit.getId());
        return limitRepository.save(limit);
    }

    @Transactional(readOnly = true)
    public Optional<Limit> getActualLimitByAccountNumber(Long accountNumber) {
        return limitRepository.findTopByAccountNumberOrderByLimitDatetimeDesc(accountNumber);
    }

    @Transactional
    public Limit createDefaultLimit(Transaction transaction) {
        Limit defaultLimit = Limit.builder()
                .accountNumber(transaction.getAccountFrom())
                .expenseCategory(transaction.getExpenseCategory())
                .limitSum(BigDecimal.valueOf(1000.00))
                .limitCurrencyShortname(Currency.getInstance(USD_CURRENCY_CODE))
                .limitDatetime(ZonedDateTime.now())
                .limitExceeded(false)
                .build();
        return limitRepository.save(defaultLimit);
    }

    @Transactional(readOnly = true)
    public List<ResponseLimitDto> getLimitsByAccount(Long accountNumber) {
        return limitMapper.toResponseDtoList(limitRepository.findAllByAccountNumber(accountNumber));
    }
}
