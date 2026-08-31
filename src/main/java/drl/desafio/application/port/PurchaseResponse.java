package drl.desafio.application.port;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public record PurchaseResponse(
        UUID id,
        UUID userId,
        BigDecimal totalAmount,
        int installmentCount,
        List<InstallmentResponse> installments) {

    public record InstallmentResponse(int number, BigDecimal amount, YearMonth period) {
    }
}
