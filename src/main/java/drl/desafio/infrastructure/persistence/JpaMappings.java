package drl.desafio.infrastructure.persistence;

import drl.desafio.domain.entity.InstallmentStatus;
import drl.desafio.domain.entity.ExpenseScope;
import drl.desafio.domain.entity.PaymentMethod;
import drl.desafio.domain.entity.ExpenseType;

final class JpaMappings {

    private JpaMappings() {
    }

    static PurchaseJpa.PaymentMethod toPaymentMethodJpa(PaymentMethod paymentMethod) {
        return paymentMethod == null ? null : PurchaseJpa.PaymentMethod.valueOf(paymentMethod.name());
    }

    static PaymentMethod toPaymentMethod(PurchaseJpa.PaymentMethod paymentMethod) {
        return paymentMethod == null ? null : PaymentMethod.valueOf(paymentMethod.name());
    }

    static PurchaseJpa.ExpenseType toExpenseTypeJpa(ExpenseType expenseType) {
        return expenseType == null ? null : PurchaseJpa.ExpenseType.valueOf(expenseType.name());
    }

    static ExpenseType toExpenseType(PurchaseJpa.ExpenseType expenseType) {
        return expenseType == null ? null : ExpenseType.valueOf(expenseType.name());
    }

    static PurchaseJpa.Scope toScopeJpa(ExpenseScope scope) {
        return scope == null ? null : PurchaseJpa.Scope.valueOf(scope.name());
    }

    static ExpenseScope toScope(PurchaseJpa.Scope scope) {
        return scope == null ? null : ExpenseScope.valueOf(scope.name());
    }

    static InstallmentJpa.Status toStatusJpa(InstallmentStatus status) {
        return status == null ? null : InstallmentJpa.Status.valueOf(status.name());
    }

    static InstallmentStatus toStatus(InstallmentJpa.Status status) {
        return status == null ? null : InstallmentStatus.valueOf(status.name());
    }
}
