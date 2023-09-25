package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.transaction.ReceiveTransactionDto;
import org.example.dto.transaction.ResponseTransactionDto;
import org.example.mapper.TransactionMapper;
import org.example.model.Limit;
import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final LimitService limitService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseTransactionDto receiveTransaction(ReceiveTransactionDto receiveTransactionDto) {
        Transaction transaction = transactionRepository.save
                (transactionMapper.receiveTransactionDtoToEntity(receiveTransactionDto));

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);

        Limit limit = limitService.handleTransactionAndReturnLimit(transaction);
        responseTransactionDto.setLimitSum(limit.getLimitSum());
        responseTransactionDto.setLimitCurrencyShortname(limit.getLimitCurrencyShortname().getCurrencyCode());
        responseTransactionDto.setLimitDatetime(limit.getLimitDatetime());

        log.info("Received transaction: {}", responseTransactionDto);
        return responseTransactionDto;
    }

    @Transactional(readOnly = true)
    public List<ResponseTransactionDto> getTransactionsLimitExceeded(Long accountNumber) {
        List<ResponseTransactionDto> responseTransactionDtos = transactionRepository.findTransactionsExceedingLimits(accountNumber);
        return responseTransactionDtos;
    }
}
