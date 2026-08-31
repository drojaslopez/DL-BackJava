package drl.desafio.domain.entity;

import drl.desafio.domain.exception.InvalidPurchaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PurchaseTest {

    @Test
    void generatesExactInstallmentsWithoutRounding() {
        Purchase purchase = Purchase.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100"),
                LocalDate.of(2026, 3, 1),
                PaymentMethod.CREDIT_CARD,
                "BANCO_DE_CHILE",
                3,
                ExpenseType.VARIABLE,
                ExpenseScope.HOME,
                new Category("ELECTRODOMESTICOS"));

        assertEquals(3, purchase.getInstallments().size());
        assertEquals(YearMonth.of(2026, 3), purchase.getInstallments().get(0).getDuePeriod());
        assertEquals(YearMonth.of(2026, 4), purchase.getInstallments().get(1).getDuePeriod());
        assertEquals(YearMonth.of(2026, 5), purchase.getInstallments().get(2).getDuePeriod());
        assertEquals(new BigDecimal("34"), purchase.getInstallments().get(0).getAmount());
        assertEquals(new BigDecimal("33"), purchase.getInstallments().get(1).getAmount());
        assertEquals(new BigDecimal("33"), purchase.getInstallments().get(2).getAmount());

        BigDecimal sum = purchase.getInstallments().stream()
                .map(Installment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, sum.compareTo(new BigDecimal("100")));
    }

    @Test
    void assignsRemainderOfCentsToFirstInstallment() {
        Purchase purchase = Purchase.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.55"),
                LocalDate.of(2026, 1, 1),
                PaymentMethod.TRANSFER,
                "BCI",
                3,
                ExpenseType.FIXED,
                ExpenseScope.PERSONAL,
                new Category("SALUD"));

        assertEquals(3, purchase.getInstallments().size());
        assertEquals(0, new BigDecimal("34.55").compareTo(purchase.getInstallments().get(0).getAmount()));
        assertEquals(0, new BigDecimal("33.00").compareTo(purchase.getInstallments().get(1).getAmount()));
        assertEquals(0, new BigDecimal("33.00").compareTo(purchase.getInstallments().get(2).getAmount()));

        BigDecimal sum = purchase.getInstallments().stream()
                .map(Installment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, sum.compareTo(new BigDecimal("100.55")));
    }

    @Test
    void singleInstallmentGeneratesSingleRecordInPurchaseMonth() {
        Purchase purchase = Purchase.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("5000"),
                LocalDate.of(2026, 6, 15),
                PaymentMethod.CASH,
                "CASH",
                1,
                ExpenseType.VARIABLE,
                ExpenseScope.OUTING,
                new Category("ENTRETENIMIENTO"));

        assertEquals(1, purchase.getInstallments().size());
        assertEquals(new BigDecimal("5000"), purchase.getInstallments().get(0).getAmount());
        assertEquals(YearMonth.of(2026, 6), purchase.getInstallments().get(0).getDuePeriod());
        assertEquals(InstallmentStatus.PENDING, purchase.getInstallments().get(0).getStatus());
    }

    @Test
    void rejectsAmountLessThanOrEqualToZero() {
        assertThrows(InvalidPurchaseException.class,
                () -> buildPurchase(new BigDecimal("0")));
        assertThrows(InvalidPurchaseException.class,
                () -> buildPurchase(new BigDecimal("-100")));
    }

    @Test
    void rejectsInstallmentCountLessThanOne() {
        assertThrows(InvalidPurchaseException.class,
                () -> Purchase.create(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100"),
                        LocalDate.of(2026, 1, 1), PaymentMethod.CASH, "CASH",
                        0, ExpenseType.VARIABLE, ExpenseScope.HOME, new Category("X")));
    }

    private Purchase buildPurchase(BigDecimal totalAmount) {
        return Purchase.create(UUID.randomUUID(), UUID.randomUUID(), totalAmount,
                LocalDate.of(2026, 1, 1), PaymentMethod.CASH, "CASH",
                1, ExpenseType.VARIABLE, ExpenseScope.HOME, new Category("X"));
    }
}
