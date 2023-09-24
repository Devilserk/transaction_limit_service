package org.example.mapper;

import org.example.dto.limit.CreateLimitDto;
import org.example.dto.limit.ResponseLimitDto;
import org.example.model.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LimitMapper {
    ResponseLimitDto toResponseDto(Limit entity);

    Limit createLimitDtoToEntity(CreateLimitDto dto);

    List<ResponseLimitDto> toResponseDtoList(List<Limit> limits);
}
