package drl.desafio.application.port;

import java.math.BigDecimal;
import java.time.YearMonth;

public record ExpenseLine(
        YearMonth period,
        String expenseType,
        String scope,
        String category,
        BigDecimal amount) {
}
