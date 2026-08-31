package drl.desafio.application.port;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record ProjectionResponse(List<MonthlyProjection> projections) {

    public record MonthlyProjection(YearMonth period, BigDecimal committedTotal) {
    }
}
