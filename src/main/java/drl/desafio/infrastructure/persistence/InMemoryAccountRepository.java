package drl.desafio.infrastructure.persistence;

import drl.desafio.application.port.AccountRepository;
import drl.desafio.domain.entity.BankAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, BankAccount> accountsByNumber = new HashMap<>();
    private final Map<String, List<BankAccount>> accountsByHolder = new HashMap<>();

    @Override
    public void save(BankAccount account) {
        accountsByNumber.put(account.getAccountNumber(), account);
        accountsByHolder.computeIfAbsent(account.getAccountHolder().getIdentification(), k -> new ArrayList<>())
                .add(account);
    }

    @Override
    public Optional<BankAccount> findByNumber(String accountNumber) {
        return Optional.ofNullable(accountsByNumber.get(accountNumber));
    }

    @Override
    public List<BankAccount> findByAccountHolder(String identification) {
        return new ArrayList<>(accountsByHolder.getOrDefault(identification, List.of()));
    }

    @Override
    public boolean exists(String accountNumber) {
        return accountsByNumber.containsKey(accountNumber);
    }
}
