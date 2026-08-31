package drl.desafio.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "installments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstallmentJpa {

    @Id
    private UUID id;

    @Column(nullable = false)
    private int installmentNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private YearMonth duePeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false)
    private PurchaseJpa purchase;

    public enum Status {
        PENDING, PAID
    }

    public InstallmentJpa(UUID id, int installmentNumber, BigDecimal amount,
                          YearMonth duePeriod, Status status, PurchaseJpa purchase) {
        this.id = id;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.duePeriod = duePeriod;
        this.status = status;
        this.purchase = purchase;
    }
}
