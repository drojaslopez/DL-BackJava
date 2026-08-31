package drl.desafio.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface PurchaseJpaRepository extends JpaRepository<PurchaseJpa, UUID> {

    List<PurchaseJpa> findByUserId(UUID userId);

    List<PurchaseJpa> findByPurchaseDateBetween(LocalDate start, LocalDate end);

    @Query("select p from PurchaseJpa p left join fetch p.installments where p.purchaseDate between :start and :end")
    List<PurchaseJpa> findWithInstallmentsBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select i from InstallmentJpa i join fetch i.purchase where i.duePeriod = :period")
    List<InstallmentJpa> findInstallmentsIn(@Param("period") YearMonth period);

    @Query("select i from InstallmentJpa i join fetch i.purchase " +
            "where i.duePeriod >= :from and i.duePeriod < :to")
    List<InstallmentJpa> findInstallmentsInRange(@Param("from") YearMonth from, @Param("to") YearMonth to);
}
