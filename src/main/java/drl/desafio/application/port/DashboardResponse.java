package drl.desafio.application.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        String period,
        BigDecimal monthTotal,
        Map<String, BigDecimal> byExpenseType,
        Map<String, BigDecimal> byScope,
        List<CategoryTotal> byCategory) {

    public record CategoryTotal(String category, BigDecimal total) {
    }
}
