package drl.desafio.infrastructure.persistence;

import drl.desafio.domain.entity.Category;
import drl.desafio.domain.entity.Purchase;
import drl.desafio.domain.entity.Installment;
import drl.desafio.domain.repository.PurchaseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PurchaseRepositoryAdapter implements PurchaseRepository {

    private final PurchaseJpaRepository jpaRepository;

    public PurchaseRepositoryAdapter(PurchaseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Purchase save(Purchase purchase) {
        PurchaseJpa jpa = new PurchaseJpa(
                purchase.getId(),
                purchase.getUserId(),
                purchase.getTotalAmount(),
                purchase.getPurchaseDate(),
                JpaMappings.toPaymentMethodJpa(purchase.getPaymentMethod()),
                purchase.getFinancialInstitution(),
                purchase.getInstallmentCount(),
                JpaMappings.toExpenseTypeJpa(purchase.getExpenseType()),
                JpaMappings.toScopeJpa(purchase.getScope()),
                purchase.getCategory().getName());

        for (Installment installment : purchase.getInstallments()) {
            InstallmentJpa installmentJpa = new InstallmentJpa(
                    installment.getId(),
                    installment.getInstallmentNumber(),
                    installment.getAmount(),
                    installment.getDuePeriod(),
                    JpaMappings.toStatusJpa(installment.getStatus()),
                    jpa);
            jpa.addInstallment(installmentJpa);
        }

        PurchaseJpa saved = jpaRepository.save(jpa);
        return toDomain(saved);
    }

    @Override
    public Optional<Purchase> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Purchase> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Purchase> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Purchase> findByPurchaseDateBetween(LocalDate start, LocalDate end) {
        return jpaRepository.findByPurchaseDateBetween(start, end).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Purchase> findPurchasesWithInstallmentsIn(YearMonth period) {
        return jpaRepository.findInstallmentsIn(period).stream()
                .map(InstallmentJpa::getPurchase)
                .distinct()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Purchase toDomain(PurchaseJpa jpa) {
        return new Purchase(
                jpa.getId(),
                jpa.getUserId(),
                jpa.getTotalAmount(),
                jpa.getPurchaseDate(),
                JpaMappings.toPaymentMethod(jpa.getPaymentMethod()),
                jpa.getFinancialInstitution(),
                jpa.getInstallmentCount(),
                JpaMappings.toExpenseType(jpa.getExpenseType()),
                JpaMappings.toScope(jpa.getScope()),
                new Category(jpa.getCategory()),
                jpa.getInstallments().stream()
                        .map(i -> new Installment(i.getId(), i.getInstallmentNumber(), i.getAmount(),
                                i.getDuePeriod()))
                        .toList());
    }
}
