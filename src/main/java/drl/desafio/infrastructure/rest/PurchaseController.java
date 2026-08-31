package drl.desafio.infrastructure.rest;

import drl.desafio.application.port.PurchaseResponse;
import drl.desafio.application.port.UpdatePurchaseCommand;
import drl.desafio.application.port.RegisterPurchaseCommand;
import drl.desafio.application.service.ManagePurchaseUseCase;
import drl.desafio.application.service.RegisterPurchaseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@Tag(name = "Purchases")
public class PurchaseController {

    private final RegisterPurchaseUseCase registerPurchaseUseCase;
    private final ManagePurchaseUseCase managePurchaseUseCase;

    public PurchaseController(RegisterPurchaseUseCase registerPurchaseUseCase,
                              ManagePurchaseUseCase managePurchaseUseCase) {
        this.registerPurchaseUseCase = registerPurchaseUseCase;
        this.managePurchaseUseCase = managePurchaseUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Registers a purchase and generates its installments",
            description = "Creates a purchase (single or in installments) linked to an existing user. " +
                    "When installmentCount > 1 the domain prorates the total amount across N monthly installments.",
            requestBody = @RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Register purchase",
                            value = "{ \"userId\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                    "\"totalAmount\": 150000, \"purchaseDate\": \"2026-03-01\", " +
                                    "\"paymentMethod\": \"CREDIT_CARD\", \"financialInstitution\": \"BANCO_DE_CHILE\", " +
                                    "\"installmentCount\": 3, \"expenseType\": \"VARIABLE\", " +
                                    "\"scope\": \"HOME\", \"category\": \"ELECTRODOMESTICOS\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Purchase created",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"c71a3674-8b09-42ef-91f8-011111111111\", " +
                                            "\"userId\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                            "\"totalAmount\": 150000, \"installmentCount\": 3, " +
                                            "\"installments\": [ " +
                                            "{ \"number\": 1, \"amount\": 50000, \"period\": \"2026-03\" }, " +
                                            "{ \"number\": 2, \"amount\": 50000, \"period\": \"2026-04\" }, " +
                                            "{ \"number\": 3, \"amount\": 50000, \"period\": \"2026-05\" } ] }"
                            ))),
                    @ApiResponse(responseCode = "400", description = "Invalid purchase or user does not exist")
            })
    public ResponseEntity<PurchaseResponse> register(
            @Valid @org.springframework.web.bind.annotation.RequestBody RegisterPurchaseCommand command) {
        PurchaseResponse response = registerPurchaseUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Lists purchases (optionally filtered by user)",
            description = "Returns all registered purchases. Pass the optional query parameter userId to filter " +
                    "the purchases of a single user.",
            parameters = @Parameter(name = "userId", description = "Filters purchases by user UUID", required = false),
            responses = @ApiResponse(responseCode = "200", description = "List of purchases",
                    content = @Content(examples = @ExampleObject(
                            value = "[ { \"id\": \"c71a3674-8b09-42ef-91f8-011111111111\", " +
                                    "\"userId\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                    "\"totalAmount\": 150000, \"installmentCount\": 3, " +
                                    "\"installments\": [] } ]"
                    )))
    )
    public ResponseEntity<List<PurchaseResponse>> list(
            @RequestParam(required = false) UUID userId) {
        if (userId != null) {
            return ResponseEntity.ok(managePurchaseUseCase.listByUser(userId));
        }
        return ResponseEntity.ok(managePurchaseUseCase.list());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Gets a purchase by id",
            description = "Returns the details of a single purchase and its installments identified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Purchase found",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"c71a3674-8b09-42ef-91f8-011111111111\", " +
                                            "\"userId\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                            "\"totalAmount\": 150000, \"installmentCount\": 3, " +
                                            "\"installments\": [] }"
                            ))),
                    @ApiResponse(responseCode = "404", description = "Purchase not found")
            })
    public ResponseEntity<PurchaseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(managePurchaseUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Updates a purchase and regenerates its installments",
            description = "Updates the fields of an existing purchase and regenerates its installments " +
                    "based on the new total amount and installment count.",
            requestBody = @RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Update purchase",
                            value = "{ \"totalAmount\": 120000, \"purchaseDate\": \"2026-04-10\", " +
                                    "\"paymentMethod\": \"CREDIT_CARD\", \"financialInstitution\": \"BCI\", " +
                                    "\"installmentCount\": 2, \"expenseType\": \"VARIABLE\", " +
                                    "\"scope\": \"HOME\", \"category\": \"ELECTRODOMESTICOS\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Purchase updated",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"id\": \"c71a3674-8b09-42ef-91f8-011111111111\", " +
                                            "\"userId\": \"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
                                            "\"totalAmount\": 120000, \"installmentCount\": 2, " +
                                            "\"installments\": [] }"
                            ))),
                    @ApiResponse(responseCode = "404", description = "Purchase not found")
            })
    public ResponseEntity<PurchaseResponse> update(@PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdatePurchaseCommand command) {
        return ResponseEntity.ok(managePurchaseUseCase.update(id, command));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletes a purchase and its installments",
            description = "Permanently removes a purchase and all of its associated installments.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Purchase deleted"),
                    @ApiResponse(responseCode = "404", description = "Purchase not found")
            })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        managePurchaseUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
