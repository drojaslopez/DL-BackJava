package drl.desafio.application.port;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryCommand(@NotBlank String name) {
}
