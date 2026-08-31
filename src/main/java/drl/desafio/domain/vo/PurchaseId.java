package drl.desafio.domain.vo;

import drl.desafio.domain.exception.InvalidPurchaseException;
import lombok.Getter;

import java.util.UUID;

@Getter
public final class PurchaseId {

    private final UUID value;

    public PurchaseId(UUID value) {
        if (value == null) {
            throw new InvalidPurchaseException("Purchase ID cannot be null");
        }
        this.value = value;
    }
}
