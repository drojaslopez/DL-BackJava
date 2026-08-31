package drl.desafio.application.port;

import drl.desafio.domain.entity.ExpenseScope;
import drl.desafio.domain.entity.PaymentMethod;
import drl.desafio.domain.entity.ExpenseType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterPurchaseCommand(
        @NotNull UUID userId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal totalAmount,
        @NotNull LocalDate purchaseDate,
        @NotNull PaymentMethod paymentMethod,
        @NotBlank String financialInstitution,
        @Min(1) int installmentCount,
        @NotNull ExpenseType expenseType,
        @NotNull ExpenseScope scope,
        @NotBlank String category) {
}
