package drl.desafio.application.service;

import drl.desafio.application.port.DashboardQuery;
import drl.desafio.application.port.DashboardResponse;
import drl.desafio.application.port.ExpenseLine;
import drl.desafio.application.port.ProjectionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {

        private final DashboardQuery dashboardQuery;

        public DashboardService(DashboardQuery dashboardQuery) {
                this.dashboardQuery = dashboardQuery;
        }

        public DashboardResponse generateDashboard(int month, int year) {
                YearMonth period = YearMonth.of(year, month);
                List<ExpenseLine> lines = dashboardQuery.expenseLinesIn(period);

                // Agregar este log
                /* System.out.println("=== DEBUG: Lines for " + period + " ===");
                lines.forEach(line -> System.out.println(
                        "Type: " + line.expenseType() +
                        ", Scope: " + line.scope() +
                        ", Category: " + line.category() +
                        ", Amount: " + line.amount()
                ));
                System.out.println("Total lines: " + lines.size()); */

                BigDecimal total = lines.stream()
                                .map(ExpenseLine::amount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                

                Map<String, BigDecimal> byType = accumulate(lines, ExpenseLine::expenseType);
                Map<String, BigDecimal> byScope = accumulate(lines, ExpenseLine::scope);
                List<DashboardResponse.CategoryTotal> byCategory = lines.stream()
                                .collect(Collectors.groupingBy(
                                                ExpenseLine::category,
                                                Collectors.reducing(BigDecimal.ZERO, ExpenseLine::amount,
                                                                BigDecimal::add)))
                                .entrySet().stream()
                                .map(e -> new DashboardResponse.CategoryTotal(e.getKey(), e.getValue()))
                                .sorted((a, b) -> b.total().compareTo(a.total()))
                                .toList();

                return new DashboardResponse(period.toString(), total, byType, byScope, byCategory);
        }

        public ProjectionResponse generateProjection(int months) {
                YearMonth from = YearMonth.now().plusMonths(1);
                List<ExpenseLine> lines = dashboardQuery.committedInstallmentsFrom(from, months);

                Map<YearMonth, BigDecimal> byPeriod = lines.stream()
                                .collect(Collectors.groupingBy(
                                                ExpenseLine::period,
                                                Collectors.reducing(BigDecimal.ZERO, ExpenseLine::amount,
                                                                BigDecimal::add)));

                List<ProjectionResponse.MonthlyProjection> projections = byPeriod.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(e -> new ProjectionResponse.MonthlyProjection(e.getKey(), e.getValue()))
                                .toList();

                return new ProjectionResponse(projections);
        }

        private Map<String, BigDecimal> accumulate(List<ExpenseLine> lines,
                        Function<ExpenseLine, String> extractor) {
                return lines.stream()
                                .collect(Collectors.groupingBy(
                                                extractor,
                                                LinkedHashMap::new,
                                                Collectors.reducing(BigDecimal.ZERO, ExpenseLine::amount,
                                                                BigDecimal::add)));
        }
}
