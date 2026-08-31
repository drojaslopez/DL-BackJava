package drl.desafio.domain.vo;

import lombok.Getter;

import java.util.UUID;

@Getter
public final class InstallmentId {

    private final UUID value;

    public InstallmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Installment ID cannot be null");
        }
        this.value = value;
    }
}
