package org.example.service;

import org.example.dto.limit.CreateLimitDto;
import org.example.enums.ExpenseCategory;
import org.example.model.Limit;
import org.example.model.Transaction;
import org.example.repository.LimitRepository;
import org.example.testconfig.PostgreSQLTestContainerConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = PostgreSQLTestContainerConfig.class)
public class LimitServiceTest {

    @Autowired
    private PostgreSQLContainer<?> postgreSQLContainer;

    @Autowired
    private LimitService limitService;

    @Autowired
    private LimitRepository limitRepository;

    @ParameterizedTest
    @MethodSource("transactionSumProvider")
    public void testHandleTransactionAndReturnLimit(BigDecimal transactionSum, BigDecimal expectedRemainder, boolean expectedExceeded) {
        limitService.createLimit(createLimitDto());

        Limit resultLimit = limitService.handleTransactionAndReturnLimit(createTransaction(transactionSum));

        assertThat(resultLimit.getLimitExceeded()).isEqualTo(expectedExceeded);
        assertThat(resultLimit.getRemainder()).isEqualByComparingTo(expectedRemainder);
    }

    private static Stream<Arguments> transactionSumProvider() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(100.00), BigDecimal.valueOf(0), false),
                Arguments.of(BigDecimal.valueOf(200.00), BigDecimal.valueOf(-100.00), true),
                Arguments.of(BigDecimal.valueOf(50.00), BigDecimal.valueOf(50.00), false)
        );
    }

    private Transaction createTransaction(BigDecimal transactionSum) {
        return Transaction.builder()
                .accountFrom(1234567890L)
                .sum(transactionSum)
                .currencyShortname(Currency.getInstance("USD"))
                .build();
    }

    private CreateLimitDto createLimitDto() {
        return CreateLimitDto.builder()
                .limitSum(BigDecimal.valueOf(100.00))
                .accountNumber(1234567890L)
                .expenseCategory(ExpenseCategory.PRODUCT)
                .build();
    }
}
