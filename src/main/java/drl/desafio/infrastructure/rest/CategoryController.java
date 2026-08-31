package drl.desafio.infrastructure.rest;

import drl.desafio.application.port.UpdateCategoryCommand;
import drl.desafio.application.port.CategoryResponse;
import drl.desafio.application.port.CreateCategoryCommand;
import drl.desafio.application.service.ManageCategoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories")
public class CategoryController {

    private final ManageCategoryUseCase manageCategoryUseCase;

    public CategoryController(ManageCategoryUseCase manageCategoryUseCase) {
        this.manageCategoryUseCase = manageCategoryUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Creates a category",
            description = "Creates a new spending category. The name is stored in uppercase.",
            requestBody = @RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Create category",
                            value = "{ \"name\": \"Supermercado\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"b2eebc99-1a2b-3c4d-5e6f-7a8b9c0d1e2f\", " +
                                            "\"name\": \"SUPERMERCADO\" }"
                            ))),
                    @ApiResponse(responseCode = "400", description = "Invalid payload (blank name)")
            })
    public ResponseEntity<CategoryResponse> create(
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateCategoryCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manageCategoryUseCase.create(command));
    }

    @GetMapping
    @Operation(
            summary = "Lists all categories",
            description = "Returns the full list of registered spending categories.",
            responses = @ApiResponse(responseCode = "200", description = "List of categories",
                    content = @Content(examples = @ExampleObject(
                            value = "[ { \"id\": \"b2eebc99-1a2b-3c4d-5e6f-7a8b9c0d1e2f\", " +
                                    "\"name\": \"SUPERMERCADO\" } ]"
                    )))
    )
    public ResponseEntity<List<CategoryResponse>> list() {
        return ResponseEntity.ok(manageCategoryUseCase.list());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Gets a category by id",
            description = "Returns the details of a single category identified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category found",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"b2eebc99-1a2b-3c4d-5e6f-7a8b9c0d1e2f\", " +
                                            "\"name\": \"SUPERMERCADO\" }"
                            ))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageCategoryUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Updates a category",
            description = "Renames an existing category identified by its UUID.",
            requestBody = @RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Update category",
                            value = "{ \"name\": \"Alquiler\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"b2eebc99-1a2b-3c4d-5e6f-7a8b9c0d1e2f\", " +
                                            "\"name\": \"ALQUILER\" }"
                            ))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateCategoryCommand command) {
        return ResponseEntity.ok(manageCategoryUseCase.update(id, command));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletes a category",
            description = "Permanently removes a category identified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Category deleted"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        manageCategoryUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
