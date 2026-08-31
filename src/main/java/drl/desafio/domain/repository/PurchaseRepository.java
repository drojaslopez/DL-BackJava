package drl.desafio.domain.repository;

import drl.desafio.domain.entity.Purchase;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository {

    Purchase save(Purchase purchase);

    Optional<Purchase> findById(UUID id);

    List<Purchase> findAll();

    List<Purchase> findByUserId(UUID userId);

    List<Purchase> findByPurchaseDateBetween(java.time.LocalDate start, java.time.LocalDate end);

    List<Purchase> findPurchasesWithInstallmentsIn(YearMonth period);

    void deleteById(UUID id);
}
