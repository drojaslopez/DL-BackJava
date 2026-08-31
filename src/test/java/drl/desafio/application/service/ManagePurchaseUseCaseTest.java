package drl.desafio.application.service;

import drl.desafio.application.port.UpdatePurchaseCommand;
import drl.desafio.application.port.PurchaseResponse;
import drl.desafio.domain.entity.Category;
import drl.desafio.domain.entity.Purchase;
import drl.desafio.domain.entity.ExpenseScope;
import drl.desafio.domain.entity.PaymentMethod;
import drl.desafio.domain.entity.ExpenseType;
import drl.desafio.domain.entity.User;
import drl.desafio.domain.repository.PurchaseRepository;
import drl.desafio.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagePurchaseUseCaseTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserRepository userRepository;

    private ManagePurchaseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ManagePurchaseUseCase(purchaseRepository, userRepository);
    }

    @Test
    void listsPurchases() {
        when(purchaseRepository.findAll()).thenReturn(List.of(buildPurchase()));
        assertEquals(1, useCase.list().size());
    }

    @Test
    void listsPurchasesByUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "A", "a@x.cl")));
        when(purchaseRepository.findByUserId(userId)).thenReturn(List.of(buildPurchase()));

        List<PurchaseResponse> response = useCase.listByUser(userId);
        assertEquals(1, response.size());
    }

    @Test
    void rejectsListingOfNonexistentUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.listByUser(id));
    }

    @Test
    void getsPurchaseById() {
        UUID id = UUID.randomUUID();
        Purchase purchase = buildPurchase(id);
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(purchase));

        PurchaseResponse response = useCase.getById(id);
        assertEquals(id, response.id());
    }

    @Test
    void throwsErrorIfPurchaseNotFound() {
        UUID id = UUID.randomUUID();
        when(purchaseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.getById(id));
    }

    @Test
    void updatesPurchaseAndRegeneratesInstallments() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Purchase original = Purchase.create(userId, userId, new BigDecimal("100"),
                LocalDate.of(2026, 1, 1), PaymentMethod.CASH, "CASH", 1,
                ExpenseType.VARIABLE, ExpenseScope.HOME, new Category("X"));
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(original));
        when(purchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseResponse response = useCase.update(id,
                new UpdatePurchaseCommand(new BigDecimal("300"), LocalDate.of(2026, 3, 1),
                        PaymentMethod.CREDIT_CARD, "BCI", 3, ExpenseType.FIXED,
                        ExpenseScope.PERSONAL, "SALUD"));

        assertEquals(new BigDecimal("300"), response.totalAmount());
        assertEquals(3, response.installmentCount());
        assertEquals(3, response.installments().size());
        verify(purchaseRepository).save(any());
    }

    @Test
    void deletesPurchase() {
        UUID id = UUID.randomUUID();
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(buildPurchase(id)));

        useCase.delete(id);
        verify(purchaseRepository).deleteById(id);
    }

    @Test
    void deletingNonexistentPurchaseThrowsError() {
        UUID id = UUID.randomUUID();
        when(purchaseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.delete(id));
        verify(purchaseRepository, never()).deleteById(any());
    }

    private Purchase buildPurchase() {
        return buildPurchase(UUID.randomUUID());
    }

    private Purchase buildPurchase(UUID id) {
        return new Purchase(id, UUID.randomUUID(), new BigDecimal("100"),
                LocalDate.of(2026, 1, 1), PaymentMethod.CASH, "CASH", 1,
                ExpenseType.VARIABLE, ExpenseScope.HOME, new Category("X"),
                List.of());
    }
}
