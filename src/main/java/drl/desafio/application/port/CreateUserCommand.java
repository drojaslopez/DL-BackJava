package drl.desafio.application.port;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserCommand(
        @NotBlank String name,
        @NotBlank @Email String email) {
}
