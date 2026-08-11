package drl.desafio.application.service;

import drl.desafio.application.port.AccountRepository;
import drl.desafio.domain.entity.AccountHolder;
import drl.desafio.domain.entity.AccountType;
import drl.desafio.domain.entity.BankAccount;
import drl.desafio.domain.exception.InvalidAmountException;

import java.util.List;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        if (accountRepository == null) {
            throw new IllegalArgumentException("The account repository cannot be null");
        }
        this.accountRepository = accountRepository;
    }

    public BankAccount createAccount(AccountHolder accountHolder, AccountType accountType, double initialBalance) {
        if (accountHolder == null) {
            throw new IllegalArgumentException("The account holder cannot be null");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("The account type cannot be null");
        }
        if (initialBalance < 0) {
            throw new InvalidAmountException("The initial balance cannot be negative");
        }

        BankAccount account = new BankAccount(accountHolder, accountType, initialBalance);
        accountRepository.save(account);
        return account;
    }

    public void deposit(String accountNumber, double amount, String description) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The account number cannot be null or empty");
        }

        BankAccount account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        account.deposit(amount, description);
    }

    public void withdraw(String accountNumber, double amount, String description) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The account number cannot be null or empty");
        }

        BankAccount account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        account.withdraw(amount, description);
    }

    public void transfer(String sourceAccountNumber, String destinationAccountNumber, double amount, String description) {
        if (sourceAccountNumber == null || sourceAccountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The source account number cannot be null or empty");
        }
        if (destinationAccountNumber == null || destinationAccountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The destination account number cannot be null or empty");
        }
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        BankAccount source = accountRepository.findByNumber(sourceAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + sourceAccountNumber));

        BankAccount destination = accountRepository.findByNumber(destinationAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + destinationAccountNumber));

        source.transfer(destination, amount, description);
    }

    public double checkBalance(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The account number cannot be null or empty");
        }

        return accountRepository.findByNumber(accountNumber)
                .map(BankAccount::getBalance)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }

    public List<BankAccount> findAccountsByAccountHolder(String identification) {
        if (identification == null || identification.trim().isEmpty()) {
            throw new IllegalArgumentException("The identification cannot be null or empty");
        }

        return accountRepository.findByAccountHolder(identification);
    }

    public void deactivateAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The account number cannot be null or empty");
        }

        BankAccount account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        account.deactivate();
    }

    public void activateAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("The account number cannot be null or empty");
        }

        BankAccount account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        account.activate();
    }
}
