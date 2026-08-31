package drl.desafio.application.port;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateUserCommand(
        @NotBlank String name,
        @NotBlank @Email String email) {
}
