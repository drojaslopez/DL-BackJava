package drl.desafio.domain.entity;

import drl.desafio.domain.exception.InvalidAmountException;
import drl.desafio.domain.exception.InsufficientBalanceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BankAccount {
    private final String accountNumber;
    private final AccountHolder accountHolder;
    private final AccountType accountType;
    private double balance;
    private final List<Transaction> transactionHistory;
    private boolean active;

    public BankAccount(AccountHolder accountHolder, AccountType accountType, double initialBalance) {
        if (accountHolder == null) {
            throw new IllegalArgumentException("The account holder cannot be null");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("The account type cannot be null");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("The initial balance cannot be negative");
        }
        this.accountNumber = generateAccountNumber();
        this.accountHolder = accountHolder;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.active = true;
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public void deposit(double amount, String description) {
        if (!active) {
            throw new IllegalStateException("The account is not active");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("The deposit amount must be greater than zero");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("The description cannot be null or empty");
        }

        balance += amount;
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        transactionHistory.add(new Transaction(transactionId, TransactionType.DEPOSIT, amount, description));
    }

    public void withdraw(double amount, String description) {
        if (!active) {
            throw new IllegalStateException("The account is not active");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("The withdrawal amount must be greater than zero");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("The description cannot be null or empty");
        }
        if (balance < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance: " + balance + ", Required amount: " + amount);
        }

        balance -= amount;
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        transactionHistory.add(new Transaction(transactionId, TransactionType.WITHDRAWAL, amount, description));
    }

    public void transfer(BankAccount destinationAccount, double amount, String description) {
        if (!active) {
            throw new IllegalStateException("The source account is not active");
        }
        if (destinationAccount == null) {
            throw new IllegalArgumentException("The destination account cannot be null");
        }
        if (!destinationAccount.isActive()) {
            throw new IllegalStateException("The destination account is not active");
        }
        if (this.accountNumber.equals(destinationAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        this.withdraw(amount, "Transfer to " + destinationAccount.getAccountNumber() + ": " + description);
        destinationAccount.deposit(amount, "Transfer from " + this.accountNumber + ": " + description);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", accountHolder=" + accountHolder.getFullName() +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", active=" + active +
                '}';
    }
}
