package drl.desafio.application.service;

import drl.desafio.application.port.AccountRepository;
import drl.desafio.domain.entity.AccountHolder;
import drl.desafio.domain.entity.AccountType;
import drl.desafio.domain.entity.BankAccount;
import drl.desafio.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Create account with valid data")
    void createAccountWithValidData() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = 1000.0;

        // Act
        BankAccount account = accountService.createAccount(accountHolder, accountType, initialBalance);

        // Assert
        assertNotNull(account);
        assertEquals(accountHolder, account.getAccountHolder());
        assertEquals(accountType, account.getAccountType());
        assertEquals(initialBalance, account.getBalance());
        verify(accountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("Create account with null holder must throw exception")
    void createAccountWithNullHolderMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        AccountHolder accountHolder = null;
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.createAccount(accountHolder, accountType, initialBalance)
        );
        assertEquals("The account holder cannot be null", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create account with null type must throw exception")
    void createAccountWithNullTypeMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = null;
        double initialBalance = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.createAccount(accountHolder, accountType, initialBalance)
        );
        assertEquals("The account type cannot be null", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create account with negative initial balance must throw exception")
    void createAccountWithNegativeInitialBalanceMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = -100.0;

        // Act & Assert
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> accountService.createAccount(accountHolder, accountType, initialBalance)
        );
        assertEquals("The initial balance cannot be negative", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create service with null repository must throw exception")
    void createServiceWithNullRepositoryMustThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountService(null)
        );
        assertEquals("The account repository cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Deposit in existing account")
    void depositInExistingAccount() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";
        double amount = 500.0;
        String description = "Payroll deposit";

        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        accountService.deposit(accountNumber, amount, description);

        // Assert
        assertEquals(1500.0, account.getBalance());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Deposit in non-existent account must throw exception")
    void depositInNonExistentAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";
        double amount = 500.0;
        String description = "Deposit";

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deposit(accountNumber, amount, description)
        );
        assertEquals("Account not found: " + accountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Deposit with null account number must throw exception")
    void depositWithNullAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deposit(null, 500.0, "Deposit")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Deposit with blank account number must throw exception")
    void depositWithBlankAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deposit("  ", 500.0, "Deposit")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Withdraw from existing account with sufficient balance")
    void withdrawFromExistingAccountWithSufficientBalance() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";
        double amount = 500.0;
        String description = "Cash withdrawal";

        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        accountService.withdraw(accountNumber, amount, description);

        // Assert
        assertEquals(500.0, account.getBalance());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Withdraw from non-existent account must throw exception")
    void withdrawFromNonExistentAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";
        double amount = 500.0;
        String description = "Withdrawal";

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(accountNumber, amount, description)
        );
        assertEquals("Account not found: " + accountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Withdraw with null account number must throw exception")
    void withdrawWithNullAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(null, 500.0, "Withdrawal")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Withdraw with blank account number must throw exception")
    void withdrawWithBlankAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw("   ", 500.0, "Withdrawal")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Transfer between existing accounts")
    void transferBetweenExistingAccounts() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String sourceAccountNumber = "ACC-12345678";
        String destinationAccountNumber = "ACC-87654321";
        double amount = 300.0;
        String description = "Transfer";

        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("87654321", "Maria", "Gomez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);
        BankAccount destinationAccount = new BankAccount(holder2, AccountType.CHECKING, 500.0);

        when(accountRepository.findByNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByNumber(destinationAccountNumber)).thenReturn(Optional.of(destinationAccount));

        // Act
        accountService.transfer(sourceAccountNumber, destinationAccountNumber, amount, description);

        // Assert
        assertEquals(700.0, sourceAccount.getBalance());
        assertEquals(800.0, destinationAccount.getBalance());
        verify(accountRepository, times(1)).findByNumber(sourceAccountNumber);
        verify(accountRepository, times(1)).findByNumber(destinationAccountNumber);
    }

    @Test
    @DisplayName("Transfer with null source account must throw exception")
    void transferWithNullSourceAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(null, "ACC-87654321", 300.0, "Transfer")
        );
        assertEquals("The source account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Transfer with blank source account must throw exception")
    void transferWithBlankSourceAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer("   ", "ACC-87654321", 300.0, "Transfer")
        );
        assertEquals("The source account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Transfer with null destination account must throw exception")
    void transferWithNullDestinationAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer("ACC-12345678", null, 300.0, "Transfer")
        );
        assertEquals("The destination account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Transfer with blank destination account must throw exception")
    void transferWithBlankDestinationAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer("ACC-12345678", "   ", 300.0, "Transfer")
        );
        assertEquals("The destination account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Transfer to the same account must throw exception")
    void transferToSameAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(accountNumber, accountNumber, 300.0, "Transfer")
        );
        assertEquals("Cannot transfer to the same account", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Transfer with non-existent source account must throw exception")
    void transferWithNonExistentSourceAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String sourceAccountNumber = "ACC-12345678";
        String destinationAccountNumber = "ACC-87654321";

        when(accountRepository.findByNumber(sourceAccountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(sourceAccountNumber, destinationAccountNumber, 300.0, "Transfer")
        );
        assertEquals("Source account not found: " + sourceAccountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(sourceAccountNumber);
        verify(accountRepository, never()).findByNumber(destinationAccountNumber);
    }

    @Test
    @DisplayName("Transfer with non-existent destination account must throw exception")
    void transferWithNonExistentDestinationAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String sourceAccountNumber = "ACC-12345678";
        String destinationAccountNumber = "ACC-87654321";

        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);

        when(accountRepository.findByNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByNumber(destinationAccountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(sourceAccountNumber, destinationAccountNumber, 300.0, "Transfer")
        );
        assertEquals("Destination account not found: " + destinationAccountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(sourceAccountNumber);
        verify(accountRepository, times(1)).findByNumber(destinationAccountNumber);
    }

    @Test
    @DisplayName("Check balance of existing account")
    void checkBalanceOfExistingAccount() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        double balance = accountService.checkBalance(accountNumber);

        // Assert
        assertEquals(1000.0, balance);
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Check balance of non-existent account must throw exception")
    void checkBalanceOfNonExistentAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.checkBalance(accountNumber)
        );
        assertEquals("Account not found: " + accountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Check balance with null account number must throw exception")
    void checkBalanceWithNullAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.checkBalance(null)
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Check balance with blank account number must throw exception")
    void checkBalanceWithBlankAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.checkBalance("   ")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Find accounts by account holder")
    void findAccountsByAccountHolder() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String identification = "12345678";

        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account1 = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        BankAccount account2 = new BankAccount(accountHolder, AccountType.CHECKING, 500.0);
        List<BankAccount> expectedAccounts = List.of(account1, account2);

        when(accountRepository.findByAccountHolder(identification)).thenReturn(expectedAccounts);

        // Act
        List<BankAccount> accounts = accountService.findAccountsByAccountHolder(identification);

        // Assert
        assertEquals(2, accounts.size());
        verify(accountRepository, times(1)).findByAccountHolder(identification);
    }

    @Test
    @DisplayName("Find accounts with null identification must throw exception")
    void findAccountsWithNullIdentificationMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.findAccountsByAccountHolder(null)
        );
        assertEquals("The identification cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByAccountHolder(any());
    }

    @Test
    @DisplayName("Find accounts with blank identification must throw exception")
    void findAccountsWithBlankIdentificationMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.findAccountsByAccountHolder("  ")
        );
        assertEquals("The identification cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByAccountHolder(any());
    }

    @Test
    @DisplayName("Deactivate existing account")
    void deactivateExistingAccount() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        accountService.deactivateAccount(accountNumber);

        // Assert
        assertFalse(account.isActive());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Deactivate non-existent account must throw exception")
    void deactivateNonExistentAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deactivateAccount(accountNumber)
        );
        assertEquals("Account not found: " + accountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Deactivate with null account number must throw exception")
    void deactivateWithNullAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deactivateAccount(null)
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Deactivate with blank account number must throw exception")
    void deactivateWithBlankAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deactivateAccount("   ")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Activate existing account")
    void activateExistingAccount() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        account.deactivate();

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        accountService.activateAccount(accountNumber);

        // Assert
        assertTrue(account.isActive());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Activate non-existent account must throw exception")
    void activateNonExistentAccountMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);
        String accountNumber = "ACC-12345678";

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.activateAccount(accountNumber)
        );
        assertEquals("Account not found: " + accountNumber, exception.getMessage());
        verify(accountRepository, times(1)).findByNumber(accountNumber);
    }

    @Test
    @DisplayName("Activate with null account number must throw exception")
    void activateWithNullAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.activateAccount(null)
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }

    @Test
    @DisplayName("Activate with blank account number must throw exception")
    void activateWithBlankAccountNumberMustThrowException() {
        // Arrange
        AccountService accountService = new AccountService(accountRepository);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.activateAccount("   ")
        );
        assertEquals("The account number cannot be null or empty", exception.getMessage());
        verify(accountRepository, never()).findByNumber(any());
    }
}
