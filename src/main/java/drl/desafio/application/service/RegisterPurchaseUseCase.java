package drl.desafio.application.service;

import drl.desafio.application.port.PurchaseResponse;
import drl.desafio.application.port.RegisterPurchaseCommand;
import drl.desafio.domain.entity.Category;
import drl.desafio.domain.entity.Purchase;
import drl.desafio.domain.exception.InvalidPurchaseException;
import drl.desafio.domain.repository.PurchaseRepository;
import drl.desafio.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegisterPurchaseUseCase {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    public RegisterPurchaseUseCase(PurchaseRepository purchaseRepository, UserRepository userRepository) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PurchaseResponse execute(RegisterPurchaseCommand command) {
        if (userRepository.findById(command.userId()).isEmpty()) {
            throw new InvalidPurchaseException("User does not exist");
        }

        Purchase purchase = Purchase.create(
                UUID.randomUUID(),
                command.userId(),
                command.totalAmount(),
                command.purchaseDate(),
                command.paymentMethod(),
                command.financialInstitution(),
                command.installmentCount(),
                command.expenseType(),
                command.scope(),
                new Category(command.category()));

        Purchase saved = purchaseRepository.save(purchase);
        return ManagePurchaseUseCase.PurchaseResponseMapper.toResponse(saved);
    }
}
