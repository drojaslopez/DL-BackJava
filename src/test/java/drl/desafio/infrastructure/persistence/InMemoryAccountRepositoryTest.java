package drl.desafio.infrastructure.persistence;

import drl.desafio.domain.entity.AccountHolder;
import drl.desafio.domain.entity.AccountType;
import drl.desafio.domain.entity.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAccountRepositoryTest {

    @Test
    @DisplayName("Save and find an account by number")
    void saveAndFindAccountByNumber() {
        // Arrange
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act
        repository.save(account);
        Optional<BankAccount> found = repository.findByNumber(account.getAccountNumber());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(account, found.get());
    }

    @Test
    @DisplayName("Find by number of a non-existent account returns empty")
    void findByNumberOfNonExistentAccountReturnsEmpty() {
        // Arrange
        InMemoryAccountRepository repository = new InMemoryAccountRepository();

        // Act
        Optional<BankAccount> found = repository.findByNumber("ACC-NOT-FOUND");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Find accounts by account holder")
    void findAccountsByAccountHolder() {
        // Arrange
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account1 = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        BankAccount account2 = new BankAccount(accountHolder, AccountType.CHECKING, 500.0);
        repository.save(account1);
        repository.save(account2);

        // Act
        List<BankAccount> accounts = repository.findByAccountHolder("12345678");

        // Assert
        assertEquals(2, accounts.size());
        assertTrue(accounts.contains(account1));
        assertTrue(accounts.contains(account2));
    }

    @Test
    @DisplayName("Find accounts of a non-existent holder returns empty list")
    void findAccountsOfNonExistentHolderReturnsEmptyList() {
        // Arrange
        InMemoryAccountRepository repository = new InMemoryAccountRepository();

        // Act
        List<BankAccount> accounts = repository.findByAccountHolder("99999999");

        // Assert
        assertTrue(accounts.isEmpty());
    }

    @Test
    @DisplayName("Exists returns true for saved account")
    void existsReturnsTrueForSavedAccount() {
        // Arrange
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        repository.save(account);

        // Act
        boolean exists = repository.exists(account.getAccountNumber());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists returns false for non-existent account")
    void existsReturnsFalseForNonExistentAccount() {
        // Arrange
        InMemoryAccountRepository repository = new InMemoryAccountRepository();

        // Act
        boolean exists = repository.exists("ACC-NOT-FOUND");

        // Assert
        assertFalse(exists);
    }
}
