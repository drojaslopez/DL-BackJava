package drl.desafio.infrastructure.rest;

import drl.desafio.application.port.UpdateUserCommand;
import drl.desafio.application.port.CreateUserCommand;
import drl.desafio.application.port.UserResponse;
import drl.desafio.application.service.ManageUserUseCase;
import drl.desafio.application.service.CreateUserUseCase;
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
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ManageUserUseCase manageUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          ManageUserUseCase manageUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.manageUserUseCase = manageUserUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Creates a new user",
            description = "Registers a new household member who becomes eligible to record purchases.",
            requestBody = @RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Create user",
                            value = "{ \"name\": \"Juan Perez\", \"email\": \"juan.perez@example.com\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                            "\"name\": \"Juan Perez\", \"email\": \"juan.perez@example.com\", " +
                                            "\"active\": true }"
                            ))),
                    @ApiResponse(responseCode = "400", description = "Invalid payload (blank name or email)")
            })
    public ResponseEntity<UserResponse> create(
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateUserCommand command) {
        UserResponse response = createUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Lists all users",
            description = "Returns the full list of registered household members.",
            responses = @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(examples = @ExampleObject(
                            value = "[ { \"id\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                    "\"name\": \"Juan Perez\", \"email\": \"juan.perez@example.com\", " +
                                    "\"active\": true } ]"
                    )))
    )
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(manageUserUseCase.list());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Gets a user by id",
            description = "Returns the details of a single user identified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                            "\"name\": \"Juan Perez\", \"email\": \"juan.perez@example.com\", " +
                                            "\"active\": true }"
                            ))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageUserUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Updates a user",
            description = "Updates the name and/or email of an existing user identified by its UUID.",
            requestBody = @RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Update user",
                            value = "{ \"name\": \"Juan Perez R.\", \"email\": \"juan.perez2@example.com\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                            "\"name\": \"Juan Perez R.\", \"email\": \"juan.perez2@example.com\", " +
                                            "\"active\": true }"
                            ))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateUserCommand command) {
        return ResponseEntity.ok(manageUserUseCase.update(id, command));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deactivates a user",
            description = "Soft-deletes a user by marking it as inactive (it is not removed from storage).",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deactivated"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        manageUserUseCase.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
