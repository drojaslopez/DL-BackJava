package drl.desafio.domain.entity;

import drl.desafio.domain.exception.InvalidPurchaseException;
import drl.desafio.domain.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    @Test
    void acceptsPositiveValue() {
        Money money = new Money(new BigDecimal("1500.50"));
        assertEquals(0, money.getValue().compareTo(new BigDecimal("1500.50")));
    }

    @Test
    void rejectsNullZeroOrNegativeAmount() {
        assertThrows(InvalidPurchaseException.class, () -> new Money(null));
        assertThrows(InvalidPurchaseException.class, () -> new Money(BigDecimal.ZERO));
        assertThrows(InvalidPurchaseException.class, () -> new Money(new BigDecimal("-1")));
    }

    @Test
    void comparesByValueNotScale() {
        Money a = new Money(new BigDecimal("100"));
        Money b = new Money(new BigDecimal("100.00"));
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(new Money(new BigDecimal("101"))));
    }
}
