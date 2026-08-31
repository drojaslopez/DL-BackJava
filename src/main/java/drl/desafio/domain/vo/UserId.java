package drl.desafio.domain.vo;

import lombok.Getter;

import java.util.UUID;

@Getter
public final class UserId {

    private final UUID value;

    public UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.value = value;
    }
}
