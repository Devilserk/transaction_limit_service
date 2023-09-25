package org.example.service;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.model.ExchangeRate;
import org.example.model.Transaction;
import org.example.testconfig.CassandraTestContainerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = CassandraTestContainerConfig.class)
public class ExchangeRateServiceTest {
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private CassandraContainer<?> cassandraContainer;
    @Autowired
    private CqlSession cqlSession;

    @AfterEach
    void tearDown() {
        cassandraContainer.stop();
    }

    @Test
    void testUpdateExchangeRates() {
        exchangeRateService.updateExchangeRates();
        ExchangeRate savedExchangeRate = exchangeRateService.getExchangeRate("USD/RUB");
        assertEquals("USD/RUB", savedExchangeRate.getCurrencyPair());
        assertNotNull(savedExchangeRate.getCloseRate());
        assertNotNull(savedExchangeRate.getDate());
    }

    @ParameterizedTest()
    @MethodSource("currencyProvider")
    void testConvertTransactionSumToUSD(String currencyShortName, BigDecimal exceptedSum) {
        insertTestData();
        BigDecimal convertedSum = exchangeRateService.convertTransactionSumToUSD(createTransaction(currencyShortName));
        assertEquals(convertedSum, exceptedSum);
    }

    private Transaction createTransaction(String currencyShortName) {
        return Transaction.builder()
                .sum(BigDecimal.valueOf(100))
                .currencyShortname(Currency.getInstance(currencyShortName))
                .build();
    }

    private void insertTestData() {
        cqlSession.execute("INSERT INTO spring_cassandra.exchange_rates (currencypair, closeRate, date) " +
                "VALUES ('USD/RUB', 3.0, '2023-09-10')");
        cqlSession.execute("INSERT INTO spring_cassandra.exchange_rates (currencypair, closeRate, date) " +
                "VALUES ('USD/KZT', 2.0, '2023-09-10')");
    }

    private static Stream<Arguments> currencyProvider() {
        return Stream.of(
                Arguments.of("RUB", BigDecimal.valueOf(300.00)),
                Arguments.of("KZT", BigDecimal.valueOf(200.00))
        );
    }
}
