package drl.desafio.application.port;

import drl.desafio.domain.entity.BankAccount;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    void save(BankAccount account);
    Optional<BankAccount> findByNumber(String accountNumber);
    List<BankAccount> findByAccountHolder(String identification);
    boolean exists(String accountNumber);
}
