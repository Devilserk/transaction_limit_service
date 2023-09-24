package org.example.dto.limit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseLimitDto {
    private Long id;
    private Long accountNumber;
    private BigDecimal limitSum;
    private ZonedDateTime limitDatetime;
    private String limitCurrencyShortname;
}
