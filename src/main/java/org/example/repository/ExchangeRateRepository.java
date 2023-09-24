package org.example.repository;

import org.example.model.ExchangeRate;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateRepository extends CassandraRepository<ExchangeRate, UUID> {
    Optional<ExchangeRate> findFirstByCurrencyPair(String currencyPair);
}
