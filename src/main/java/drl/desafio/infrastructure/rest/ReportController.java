package drl.desafio.infrastructure.rest;

import drl.desafio.application.port.DashboardResponse;
import drl.desafio.application.port.ProjectionResponse;
import drl.desafio.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports")
public class ReportController {

    private final DashboardService dashboardService;

    public ReportController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    @Operation(
            summary = "Consolidated metrics for the specified month",
            description = "Returns the total spent on one-time purchases plus the active installments for the " +
                    "requested month, segmented by expense type, scope and category.",
            parameters = {
                    @Parameter(name = "month", description = "Month of the year (1-12)", example = "3", required = true),
                    @Parameter(name = "year", description = "Year (4 digits)", example = "2026", required = true)
            },
            responses = @ApiResponse(responseCode = "200", description = "Monthly consolidated metrics",
                    content = @Content(examples = @ExampleObject(
                            value = "{ \"period\": \"2026-03\", \"monthTotal\": 350000, " +
                                    "\"byExpenseType\": { \"FIXED\": 200000, \"VARIABLE\": 150000 }, " +
                                    "\"byScope\": { \"HOME\": 250000, \"OUTING\": 50000, \"PERSONAL\": 50000 }, " +
                                    "\"byCategory\": [ " +
                                    "{ \"category\": \"SUPERMERCADO\", \"total\": 120000 }, " +
                                    "{ \"category\": \"ELECTRODOMESTICOS\", \"total\": 50000 } ] }"
                    )))
    )
    public ResponseEntity<DashboardResponse> dashboard(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(dashboardService.generateDashboard(month, year));
    }

    @GetMapping("/projection")
    @Operation(
            summary = "Commitment of future installments",
            description = "Returns the projected load of committed future installments for the next N months, " +
                    "starting from the following month.",
            parameters = {
                    @Parameter(name = "months", description = "Number of future months to project (default 6)",
                            example = "6", required = false)
            },
            responses = @ApiResponse(responseCode = "200", description = "Future commitment projection",
                    content = @Content(examples = @ExampleObject(
                            value = "{ \"projections\": [ " +
                                    "{ \"period\": \"2026-04\", \"committedTotal\": 180000 }, " +
                                    "{ \"period\": \"2026-05\", \"committedTotal\": 130000 } ] }"
                    )))
    )
    public ResponseEntity<ProjectionResponse> projection(
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(dashboardService.generateProjection(months));
    }
}
