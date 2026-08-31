package drl.desafio.application.port;

import java.time.YearMonth;
import java.util.List;

public interface DashboardQuery {

    List<ExpenseLine> expenseLinesIn(YearMonth period);

    List<ExpenseLine> committedInstallmentsFrom(YearMonth from, int monthCount);
}
