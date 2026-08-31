package drl.desafio.application.service;

import drl.desafio.application.port.PurchaseResponse;
import drl.desafio.application.port.UpdatePurchaseCommand;
import drl.desafio.domain.entity.Category;
import drl.desafio.domain.entity.Purchase;
import drl.desafio.domain.repository.PurchaseRepository;
import drl.desafio.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ManagePurchaseUseCase {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    public ManagePurchaseUseCase(PurchaseRepository purchaseRepository, UserRepository userRepository) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> list() {
        return purchaseRepository.findAll().stream().map(PurchaseResponseMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> listByUser(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return purchaseRepository.findByUserId(userId).stream()
                .map(PurchaseResponseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchaseResponse getById(UUID id) {
        return PurchaseResponseMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public PurchaseResponse update(UUID id, UpdatePurchaseCommand command) {
        Purchase existing = findOrThrow(id);
        Purchase updated = Purchase.create(
                existing.getId(),
                existing.getUserId(),
                command.totalAmount(),
                command.purchaseDate(),
                command.paymentMethod(),
                command.financialInstitution(),
                command.installmentCount(),
                command.expenseType(),
                command.scope(),
                new Category(command.category()));
        Purchase saved = purchaseRepository.save(updated);
        return PurchaseResponseMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        Purchase purchase = findOrThrow(id);
        purchaseRepository.deleteById(purchase.getId());
    }

    private Purchase findOrThrow(UUID id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found: " + id));
    }

    static final class PurchaseResponseMapper {
        private PurchaseResponseMapper() {
        }

        static PurchaseResponse toResponse(Purchase purchase) {
            List<PurchaseResponse.InstallmentResponse> installments = purchase.getInstallments().stream()
                    .map(i -> new PurchaseResponse.InstallmentResponse(
                            i.getInstallmentNumber(), i.getAmount(), i.getDuePeriod()))
                    .toList();
            return new PurchaseResponse(purchase.getId(), purchase.getUserId(), purchase.getTotalAmount(),
                    purchase.getInstallmentCount(), installments);
        }
    }
}
