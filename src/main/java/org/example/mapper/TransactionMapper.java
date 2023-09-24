package org.example.mapper;

import org.example.dto.transaction.ReceiveTransactionDto;
import org.example.dto.transaction.ResponseTransactionDto;
import org.example.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    ResponseTransactionDto toResponseDto(Transaction entity);

    Transaction receiveTransactionDtoToEntity(ReceiveTransactionDto dto);
}
