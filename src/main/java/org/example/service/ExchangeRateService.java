package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.ExchangeRate;
import org.example.model.Transaction;
import org.example.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {
    @Value("${twelvedata.api.url.base}")
    private  String baseUrl;
    @Value("${twelvedata.api.url.daily-currency-close}")
    private String dailyCurrencyUrl;

    private final ExchangeRateRepository exchangeRateRepository;
    private final ObjectMapper objectMapper;

    public void updateExchangeRates() {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        List<String> currencyPairs = Arrays.asList("USD/RUB");

        for (String currencyPair : currencyPairs) {
            String url = String.format(dailyCurrencyUrl, currencyPair);

            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            ExchangeRate exchangeRate = new ExchangeRate();

            exchangeRate.setCurrencyPair(jsonNode.at("/meta/symbol").asText());
            exchangeRate.setDate(LocalDate.parse(jsonNode.at("/values/0/datetime").asText()));
            exchangeRate.setCloseRate(BigDecimal.valueOf(jsonNode.at("/values/0/close").asDouble()));

            if (exchangeRate.getCloseRate() != null) {
                log.info("Saved exchange rate: {}", exchangeRate);
            }
        }
    }

    public BigDecimal convertTransactionSumToUSD(Transaction transaction) {
        String currencyPair = "USD/" + transaction.getCurrencyShortname().getCurrencyCode();
        return transaction.getSum().divide(getExchangeRate(currencyPair).getCloseRate(), 2, RoundingMode.HALF_DOWN);
    }

    public ExchangeRate getExchangeRate(String currencyPair) {
        return exchangeRateRepository.findFirstByCurrencyPair(currencyPair)
                .orElseThrow(() -> new IllegalArgumentException("Currency pair not found: " + currencyPair));
    }

    @Scheduled(cron = "0 0 1 * * ?") // Запускать каждый день в 01:00
    public void scheduledUpdate() {
        updateExchangeRates();
    }
}
