package drl.desafio.application.service;

import drl.desafio.application.port.DashboardQuery;
import drl.desafio.application.port.DashboardResponse;
import drl.desafio.application.port.ExpenseLine;
import drl.desafio.application.port.ProjectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardQuery dashboardQuery;

    private DashboardService service;

    @BeforeEach
    void setUp() {
        service = new DashboardService(dashboardQuery);
    }

    @Test
    void consolidatesMonthlyMetrics() {
        YearMonth period = YearMonth.of(2026, 3);
        when(dashboardQuery.expenseLinesIn(period)).thenReturn(List.of(
                new ExpenseLine(period, "FIXED", "HOME", "SUPERMERCADO", new BigDecimal("120000")),
                new ExpenseLine(period, "VARIABLE", "HOME", "ELECTRODOMESTICOS", new BigDecimal("50000")),
                new ExpenseLine(period, "VARIABLE", "OUTING", "ENTRETENIMIENTO", new BigDecimal("50000"))));

        DashboardResponse response = service.generateDashboard(3, 2026);

        assertEquals("2026-03", response.period());
        assertEquals(0, new BigDecimal("220000").compareTo(response.monthTotal()));
        assertEquals(0, new BigDecimal("120000").compareTo(response.byExpenseType().get("FIXED")));
        assertEquals(0, new BigDecimal("100000").compareTo(response.byExpenseType().get("VARIABLE")));
        assertEquals(0, new BigDecimal("170000").compareTo(response.byScope().get("HOME")));
        assertEquals(3, response.byCategory().size());
    }

    @Test
    void generatesInstallmentProjection() {
        YearMonth from = YearMonth.now().plusMonths(1);
        when(dashboardQuery.committedInstallmentsFrom(eq(from), anyInt())).thenReturn(List.of(
                new ExpenseLine(from, "FIXED", "HOME", "SUPERMERCADO", new BigDecimal("100000")),
                new ExpenseLine(from.plusMonths(1), "VARIABLE", "HOME", "TECNO", new BigDecimal("20000"))));

        ProjectionResponse response = service.generateProjection(6);

        assertEquals(2, response.projections().size());
        assertEquals(0, new BigDecimal("100000").compareTo(
                response.projections().get(0).committedTotal()));
    }
}
