package drl.desafio.application.port;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, boolean active) {
}
