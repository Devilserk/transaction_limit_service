package org.example.dto.limit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.ExpenseCategory;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLimitDto {
    @NotNull
    @Positive
    private Long accountNumber;
    @NotNull
    @Positive
    private BigDecimal limitSum;
    @NotNull
    private ExpenseCategory expenseCategory;
}
