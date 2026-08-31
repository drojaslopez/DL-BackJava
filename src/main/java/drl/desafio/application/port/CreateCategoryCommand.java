package drl.desafio.application.port;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryCommand(@NotBlank String name) {
}
