package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.example.dto.limit.CreateLimitDto;
import org.example.dto.transaction.ReceiveTransactionDto;
import org.example.dto.transaction.ResponseTransactionDto;
import org.example.enums.ExpenseCategory;
import org.example.model.Transaction;
import org.example.repository.LimitRepository;
import org.example.repository.TransactionRepository;
import org.example.service.LimitService;
import org.example.service.TransactionService;
import org.example.testconfig.TestContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(classes = TestContainerConfig.class)
class TransactionControllerTest {
    @Autowired
    private PostgreSQLContainer<?> postgreSQLContainer;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitService limitService;

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        limitService.createLimit(createLimitDto());
    }

    @Test
    void testReceiveTransactionAndVerifyDatabase() throws Exception {
        ReceiveTransactionDto requestDto = createReceiveTransactionDto();

        MvcResult result = mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ResponseTransactionDto responseDto = objectMapper.readValue(responseContent, ResponseTransactionDto.class);

        Transaction savedTransaction = transactionRepository.findById(responseDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        assertEquals(requestDto.getAccountFrom(), savedTransaction.getAccountFrom());
        assertEquals(requestDto.getAccountTo(), savedTransaction.getAccountTo());

    }

    private ReceiveTransactionDto createReceiveTransactionDto() {
        return ReceiveTransactionDto.builder()
                .accountFrom(1234567890L)
                .accountTo(2234567890L)
                .sum(BigDecimal.valueOf(100.00))
                .currencyShortname(Currency.getInstance("USD"))
                .expenseCategory(ExpenseCategory.PRODUCT)
                .datetime(ZonedDateTime.now())
                .build();
    }

    private CreateLimitDto createLimitDto(){
        return CreateLimitDto.builder()
                .limitSum(BigDecimal.valueOf(100.00))
                .accountNumber(1234567890L)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .build();
    }

    private String asJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
