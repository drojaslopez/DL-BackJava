package drl.desafio.domain.vo;

import drl.desafio.domain.exception.InvalidPurchaseException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public final class Money {

    private final BigDecimal value;

    public Money(BigDecimal value) {
        if (value == null) {
            throw new InvalidPurchaseException("Amount cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPurchaseException("Total amount must be strictly greater than zero");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        return value.stripTrailingZeros().compareTo(((Money) o).value.stripTrailingZeros()) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }
}
