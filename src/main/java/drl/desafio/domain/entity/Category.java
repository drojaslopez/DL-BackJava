package drl.desafio.domain.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Category {

    private UUID id;
    private String name;

    public Category(UUID id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name.trim().toUpperCase();
    }

    public Category(String name) {
        this(UUID.randomUUID(), name);
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.name = name.trim().toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return Objects.equals(id, ((Category) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
