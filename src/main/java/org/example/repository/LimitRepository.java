package org.example.repository;

import org.example.model.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    Optional<Limit> findTopByAccountNumberOrderByLimitDatetimeDesc(Long accountNumber);

    List<Limit> findAllByAccountNumber(Long accountNumber);
}
