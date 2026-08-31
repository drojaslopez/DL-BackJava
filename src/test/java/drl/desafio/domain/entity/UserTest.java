package drl.desafio.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void assignsIdAndActiveByDefault() {
        User user = new User(UUID.randomUUID(), "Juan Perez", "juan@example.com");
        assertTrue(user.isActive());
        assertEquals("Juan Perez", user.getName());
        assertEquals("juan@example.com", user.getEmail());
    }

    @Test
    void generatesIdIfNotProvided() {
        User user = new User(null, "Ana", "ana@example.com");
        java.util.UUID id = user.getId();
        assertEquals(id, user.getId());
    }

    @Test
    void deactivatesUser() {
        User user = new User(UUID.randomUUID(), "Luis", "luis@example.com");
        user.deactivate();
        assertFalse(user.isActive());
    }

    @Test
    void categoryCannotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Category(null));
        assertThrows(IllegalArgumentException.class, () -> new Category("  "));
    }
}
