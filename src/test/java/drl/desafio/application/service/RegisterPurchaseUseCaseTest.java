package drl.desafio.application.service;

import drl.desafio.application.port.PurchaseResponse;
import drl.desafio.application.port.RegisterPurchaseCommand;
import drl.desafio.domain.entity.ExpenseScope;
import drl.desafio.domain.entity.PaymentMethod;
import drl.desafio.domain.entity.ExpenseType;
import drl.desafio.domain.entity.User;
import drl.desafio.domain.exception.InvalidPurchaseException;
import drl.desafio.domain.repository.PurchaseRepository;
import drl.desafio.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterPurchaseUseCaseTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserRepository userRepository;

    private RegisterPurchaseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterPurchaseUseCase(purchaseRepository, userRepository);
    }

    @Test
    void registersPurchaseAndGeneratesInstallments() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserStub()));
        when(purchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegisterPurchaseCommand command = new RegisterPurchaseCommand(
                userId, new BigDecimal("150000"), LocalDate.of(2026, 3, 1),
                PaymentMethod.CREDIT_CARD, "BANCO_DE_CHILE", 3,
                ExpenseType.VARIABLE, ExpenseScope.HOME, "ELECTRODOMESTICOS");

        PurchaseResponse response = useCase.execute(command);

        assertEquals(new BigDecimal("150000"), response.totalAmount());
        assertEquals(3, response.installmentCount());
        assertEquals(3, response.installments().size());
        assertEquals(new BigDecimal("50000"), response.installments().get(0).amount());
        assertEquals(new BigDecimal("50000"), response.installments().get(1).amount());
        assertEquals(new BigDecimal("50000"), response.installments().get(2).amount());
        verify(purchaseRepository).save(any());
    }

    @Test
    void rejectsPurchaseFromNonexistentUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RegisterPurchaseCommand command = new RegisterPurchaseCommand(
                userId, new BigDecimal("100"), LocalDate.of(2026, 1, 1),
                PaymentMethod.CASH, "CASH", 1,
                ExpenseType.VARIABLE, ExpenseScope.HOME, "X");

        assertThrows(InvalidPurchaseException.class, () -> useCase.execute(command));
        verify(purchaseRepository, never()).save(any());
    }

    private static class UserStub extends User {
        UserStub() {
            super(UUID.randomUUID(), "Stub", "stub@x.cl");
        }
    }
}
