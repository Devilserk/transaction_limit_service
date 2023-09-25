package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.transaction.ReceiveTransactionDto;
import org.example.dto.transaction.ResponseTransactionDto;
import org.example.enums.ExpenseCategory;
import org.example.model.Limit;
import org.example.model.Transaction;
import org.example.repository.LimitRepository;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.example.testconfig.PostgreSQLTestContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ContextConfiguration(classes = PostgreSQLTestContainerConfig.class)
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
    private LimitRepository limitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private int expectedLength;
    private Long expectedAccountFrom;
    private Long expectedAccountTo;
    private BigDecimal exceptedLimitSum;

    @BeforeEach
    void setUp() {
        expectedLength = 1;
        expectedAccountFrom = 1234567890L;
        expectedAccountTo = 2234567890L;
        exceptedLimitSum = BigDecimal.valueOf(1000.00);
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

        Transaction savedTransaction = transactionRepository.findById(responseDto.getId());

        assertEquals(requestDto.getAccountFrom(), savedTransaction.getAccountFrom());
        assertEquals(requestDto.getAccountTo(), savedTransaction.getAccountTo());
        assertEquals(exceptedLimitSum, responseDto.getLimitSum());

    }

    @Test
    void testGetTransactionsLimitExceeded() throws Exception {
        createTestData();
        mockMvc.perform(get("/api/v1/transactions/{accountNumber}/exceeded", 1234567890L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountFrom").value(expectedAccountFrom))
                .andExpect(jsonPath("$[0].accountTo").value(expectedAccountTo))
                .andExpect(jsonPath("$.length()").value(expectedLength));
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

    private String asJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private void createTestData() {
        limitRepository.save(Limit.builder()
                .accountNumber(1234567890L)
                .limitSum(BigDecimal.valueOf(100.0))
                .remainder(BigDecimal.valueOf(100.0))
                .limitDatetime(ZonedDateTime.of(2020, 9, 15, 14, 30, 0, 0, ZoneId.systemDefault()))
                .limitCurrencyShortname(Currency.getInstance("USD"))
                .limitExceeded(true)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .build());

        transactionRepository.save(Transaction.builder()
                .accountFrom(1234567890L)
                .accountTo(2234567890L)
                .sum(BigDecimal.valueOf(100.00))
                .currencyShortname(Currency.getInstance("USD"))
                .expenseCategory(ExpenseCategory.PRODUCT)
                .datetime(ZonedDateTime.of(2020, 10, 15, 14, 30, 0, 0, ZoneId.systemDefault()))
                .build());
    }
}
