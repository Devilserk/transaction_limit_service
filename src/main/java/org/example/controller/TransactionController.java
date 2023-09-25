package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.transaction.ReceiveTransactionDto;
import org.example.dto.transaction.ResponseTransactionDto;
import org.example.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseTransactionDto receiveTransaction(@RequestBody @Valid ReceiveTransactionDto receiveTransactionDto) {
        return transactionService.receiveTransaction(receiveTransactionDto);
    }

    @GetMapping("/{accountNumber}/exceeded")
    public List<ResponseTransactionDto> getTransactionsLimitExceeded(@PathVariable Long accountNumber) {
        return transactionService.getTransactionsLimitExceeded(accountNumber);
    }
}
