package drl.desafio.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseJpa {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String financialInstitution;

    @Column(nullable = false)
    private int installmentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseType expenseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Scope scope;

    @Column(nullable = false)
    private String category;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstallmentJpa> installments = new ArrayList<>();

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, CASH, TRANSFER
    }

    public enum ExpenseType {
        FIXED, VARIABLE
    }

    public enum Scope {
        HOME, OUTING, PERSONAL
    }

    public PurchaseJpa(UUID id, UUID userId, BigDecimal totalAmount, LocalDate purchaseDate,
                       PaymentMethod paymentMethod, String financialInstitution, int installmentCount,
                       ExpenseType expenseType, Scope scope, String category) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
        this.paymentMethod = paymentMethod;
        this.financialInstitution = financialInstitution;
        this.installmentCount = installmentCount;
        this.expenseType = expenseType;
        this.scope = scope;
        this.category = category;
    }

    public void addInstallment(InstallmentJpa installment) {
        installments.add(installment);
    }
}
