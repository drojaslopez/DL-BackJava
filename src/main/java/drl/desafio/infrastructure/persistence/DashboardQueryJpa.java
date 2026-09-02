package drl.desafio.infrastructure.persistence;

import drl.desafio.application.port.DashboardQuery;
import drl.desafio.application.port.ExpenseLine;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class DashboardQueryJpa implements DashboardQuery {

    private final PurchaseJpaRepository jpaRepository;

    public DashboardQueryJpa(PurchaseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ExpenseLine> expenseLinesIn(YearMonth period) {
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        List<ExpenseLine> result = new ArrayList<>();

        // One-time purchases recorded in the month (1 installment or direct payment of the month)
        for (PurchaseJpa purchase : jpaRepository.findWithInstallmentsBetween(start, end)) {
            if (purchase.getInstallmentCount() == 1) {
                result.add(new ExpenseLine(
                        period, purchase.getExpenseType().name(),
                        purchase.getScope().name(), purchase.getCategory(),
                        purchase.getTotalAmount()));
            }
        }

        // Installments (due) of the month - only for purchases with multiple installments
        for (InstallmentJpa installment : jpaRepository.findInstallmentsIn(period)) {
            PurchaseJpa purchase = installment.getPurchase();
            // Skip if this is a single installment purchase (already counted above)
            if (purchase.getInstallmentCount() > 1) {
                result.add(new ExpenseLine(
                        period, purchase.getExpenseType().name(),
                        purchase.getScope().name(), purchase.getCategory(),
                        installment.getAmount()));
            }
        }

        return result;
    }

    @Override
    public List<ExpenseLine> committedInstallmentsFrom(YearMonth from, int months) {
        YearMonth to = from.plusMonths(months);
        List<ExpenseLine> result = new ArrayList<>();

        for (InstallmentJpa installment : jpaRepository.findInstallmentsInRange(from, to)) {
            if (installment.getStatus() == InstallmentJpa.Status.PENDING) {
                PurchaseJpa purchase = installment.getPurchase();
                result.add(new ExpenseLine(
                        installment.getDuePeriod(), purchase.getExpenseType().name(),
                        purchase.getScope().name(), purchase.getCategory(),
                        installment.getAmount()));
            }
        }

        return result;
    }
}
