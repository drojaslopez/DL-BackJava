package drl.desafio.domain.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {

    private UUID id;
    private String name;
    private String email;
    private boolean active;

    public User(UUID id, String name, String email) {
        this(id, name, email, true);
    }

    public User(UUID id, String name, String email, boolean active) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
