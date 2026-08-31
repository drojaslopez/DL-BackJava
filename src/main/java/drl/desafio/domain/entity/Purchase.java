package drl.desafio.domain.entity;

import drl.desafio.domain.exception.InvalidPurchaseException;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Purchase {

    private final UUID id;
    private final UUID userId;
    private final BigDecimal totalAmount;
    private final LocalDate purchaseDate;
    private final PaymentMethod paymentMethod;
    private final String financialInstitution;
    private final int installmentCount;
    private final ExpenseType expenseType;
    private final ExpenseScope scope;
    private final Category category;
    @Getter(AccessLevel.NONE)
    private final List<Installment> installments;

    public Purchase(UUID id,
                    UUID userId,
                    BigDecimal totalAmount,
                    LocalDate purchaseDate,
                    PaymentMethod paymentMethod,
                    String financialInstitution,
                    int installmentCount,
                    ExpenseType expenseType,
                    ExpenseScope scope,
                    Category category,
                    List<Installment> installments) {
        if (userId == null) {
            throw new InvalidPurchaseException("Purchase must be linked to a user");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPurchaseException("Total amount must be strictly greater than zero");
        }
        if (installmentCount < 1) {
            throw new InvalidPurchaseException("Number of installments must be at least 1");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
        this.paymentMethod = paymentMethod;
        this.financialInstitution = financialInstitution;
        this.installmentCount = installmentCount;
        this.expenseType = expenseType;
        this.scope = scope;
        this.category = category;
        this.installments = installments != null ? installments : new ArrayList<>();
    }

    public static Purchase create(UUID id,
                                  UUID userId,
                                  BigDecimal totalAmount,
                                  LocalDate purchaseDate,
                                  PaymentMethod paymentMethod,
                                  String financialInstitution,
                                  int installmentCount,
                                  ExpenseType expenseType,
                                  ExpenseScope scope,
                                  Category category) {
        Purchase purchase = new Purchase(id, userId, totalAmount, purchaseDate, paymentMethod,
                financialInstitution, installmentCount, expenseType, scope, category, null);
        purchase.calculateInstallments();
        return purchase;
    }

    public void calculateInstallments() {
        this.installments.clear();
        BigDecimal baseInstallment = totalAmount.divide(BigDecimal.valueOf(installmentCount), 0, RoundingMode.DOWN);
        BigDecimal remainder = totalAmount.subtract(baseInstallment.multiply(BigDecimal.valueOf(installmentCount)));

        for (int i = 0; i < installmentCount; i++) {
            BigDecimal installmentAmount = (i == 0) ? baseInstallment.add(remainder) : baseInstallment;
            YearMonth period = YearMonth.from(purchaseDate).plusMonths(i);
            installments.add(new Installment(UUID.randomUUID(), i + 1, installmentAmount, period));
        }
    }

    public List<Installment> getInstallments() {
        return Collections.unmodifiableList(installments);
    }
}
