package drl.desafio.domain.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Getter
public class Installment {

    private UUID id;
    private int installmentNumber;
    private BigDecimal amount;
    private YearMonth duePeriod;
    private InstallmentStatus status;

    public Installment(UUID id, int installmentNumber, BigDecimal amount, YearMonth duePeriod) {
        this.id = id != null ? id : UUID.randomUUID();
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.duePeriod = duePeriod;
        this.status = InstallmentStatus.PENDING;
    }

    public void markPaid() {
        this.status = InstallmentStatus.PAID;
    }
}
