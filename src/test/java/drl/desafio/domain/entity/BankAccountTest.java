package drl.desafio.domain.entity;

import drl.desafio.domain.exception.InvalidAmountException;
import drl.desafio.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    @DisplayName("Create bank account with valid data")
    void createBankAccountWithValidData() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = 1000.0;

        // Act
        BankAccount account = new BankAccount(accountHolder, accountType, initialBalance);

        // Assert
        assertNotNull(account.getAccountNumber());
        assertEquals(accountHolder, account.getAccountHolder());
        assertEquals(accountType, account.getAccountType());
        assertEquals(initialBalance, account.getBalance());
        assertTrue(account.isActive());
        assertTrue(account.getTransactionHistory().isEmpty());
    }

    @Test
    @DisplayName("Create account with null holder must throw exception")
    void createAccountWithNullHolderMustThrowException() {
        // Arrange
        AccountHolder accountHolder = null;
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BankAccount(accountHolder, accountType, initialBalance)
        );
        assertEquals("The account holder cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Create account with null type must throw exception")
    void createAccountWithNullTypeMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = null;
        double initialBalance = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BankAccount(accountHolder, accountType, initialBalance)
        );
        assertEquals("The account type cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Create account with negative initial balance must throw exception")
    void createAccountWithNegativeInitialBalanceMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = -100.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BankAccount(accountHolder, accountType, initialBalance)
        );
        assertEquals("The initial balance cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Create account with zero initial balance")
    void createAccountWithZeroInitialBalance() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = 0.0;

        // Act
        BankAccount account = new BankAccount(accountHolder, accountType, initialBalance);

        // Assert
        assertEquals(0.0, account.getBalance());
    }

    @Test
    @DisplayName("Deposit valid amount")
    void depositValidAmount() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        double amount = 500.0;
        String description = "Payroll deposit";

        // Act
        account.deposit(amount, description);

        // Assert
        assertEquals(1500.0, account.getBalance());
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(TransactionType.DEPOSIT, account.getTransactionHistory().get(0).getType());
        assertEquals(amount, account.getTransactionHistory().get(0).getAmount());
    }

    @Test
    @DisplayName("Deposit in inactive account must throw exception")
    void depositInInactiveAccountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        account.deactivate();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> account.deposit(500.0, "Deposit")
        );
        assertEquals("The account is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Deposit zero amount must throw exception")
    void depositZeroAmountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(0.0, "Deposit")
        );
        assertEquals("The deposit amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deposit negative amount must throw exception")
    void depositNegativeAmountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(-100.0, "Deposit")
        );
        assertEquals("The deposit amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deposit with null description must throw exception")
    void depositWithNullDescriptionMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(500.0, null)
        );
        assertEquals("The description cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deposit with blank description must throw exception")
    void depositWithBlankDescriptionMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(500.0, "  ")
        );
        assertEquals("The description cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw valid amount with sufficient balance")
    void withdrawValidAmountWithSufficientBalance() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        double amount = 500.0;
        String description = "Cash withdrawal";

        // Act
        account.withdraw(amount, description);

        // Assert
        assertEquals(500.0, account.getBalance());
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactionHistory().get(0).getType());
        assertEquals(amount, account.getTransactionHistory().get(0).getAmount());
    }

    @Test
    @DisplayName("Withdraw in inactive account must throw exception")
    void withdrawInInactiveAccountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        account.deactivate();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> account.withdraw(500.0, "Withdrawal")
        );
        assertEquals("The account is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw zero amount must throw exception")
    void withdrawZeroAmountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(0.0, "Withdrawal")
        );
        assertEquals("The withdrawal amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw negative amount must throw exception")
    void withdrawNegativeAmountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(-100.0, "Withdrawal")
        );
        assertEquals("The withdrawal amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw with insufficient balance must throw exception")
    void withdrawWithInsufficientBalanceMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(1500.0, "Withdrawal")
        );
        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    @DisplayName("Withdraw with null description must throw exception")
    void withdrawWithNullDescriptionMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(500.0, null)
        );
        assertEquals("The description cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Withdraw with empty description must throw exception")
    void withdrawWithEmptyDescriptionMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(500.0, "")
        );
        assertEquals("The description cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Transfer between valid accounts")
    void transferBetweenValidAccounts() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("87654321", "Maria", "Gomez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);
        BankAccount destinationAccount = new BankAccount(holder2, AccountType.CHECKING, 500.0);
        double amount = 300.0;

        // Act
        sourceAccount.transfer(destinationAccount, amount, "Transfer");

        // Assert
        assertEquals(700.0, sourceAccount.getBalance());
        assertEquals(800.0, destinationAccount.getBalance());
        assertEquals(1, sourceAccount.getTransactionHistory().size());
        assertEquals(1, destinationAccount.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Transfer from inactive account must throw exception")
    void transferFromInactiveAccountMustThrowException() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("87654321", "Maria", "Gomez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);
        BankAccount destinationAccount = new BankAccount(holder2, AccountType.CHECKING, 500.0);
        sourceAccount.deactivate();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sourceAccount.transfer(destinationAccount, 300.0, "Transfer")
        );
        assertEquals("The source account is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Transfer to null account must throw exception")
    void transferToNullAccountMustThrowException() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sourceAccount.transfer(null, 300.0, "Transfer")
        );
        assertEquals("The destination account cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Transfer to inactive account must throw exception")
    void transferToInactiveAccountMustThrowException() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("87654321", "Maria", "Gomez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);
        BankAccount destinationAccount = new BankAccount(holder2, AccountType.CHECKING, 500.0);
        destinationAccount.deactivate();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sourceAccount.transfer(destinationAccount, 300.0, "Transfer")
        );
        assertEquals("The destination account is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Transfer to the same account must throw exception")
    void transferToSameAccountMustThrowException() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.transfer(account, 300.0, "Transfer")
        );
        assertEquals("Cannot transfer to the same account", exception.getMessage());
    }

    @Test
    @DisplayName("Transfer with insufficient balance must throw exception")
    void transferWithInsufficientBalanceMustThrowException() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("87654321", "Maria", "Gomez");
        BankAccount sourceAccount = new BankAccount(holder1, AccountType.SAVINGS, 1000.0);
        BankAccount destinationAccount = new BankAccount(holder2, AccountType.CHECKING, 500.0);

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> sourceAccount.transfer(destinationAccount, 1500.0, "Transfer")
        );
        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    @DisplayName("Deactivate account")
    void deactivateAccount() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act
        account.deactivate();

        // Assert
        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("Activate account")
    void activateAccount() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        account.deactivate();

        // Act
        account.activate();

        // Assert
        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("Equality of accounts by account number")
    void equalityOfAccountsByAccountNumber() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account1 = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        BankAccount account2 = account1;

        // Act & Assert
        assertEquals(account1, account2);
    }

    @Test
    @DisplayName("Inequality of accounts with different numbers")
    void inequalityOfAccountsWithDifferentNumbers() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account1 = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        BankAccount account2 = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        assertNotEquals(account1, account2);
    }

    @Test
    @DisplayName("Equals with null object")
    void equalsWithNullObject() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act & Assert
        assertNotEquals(account, null);
    }

    @Test
    @DisplayName("Equals with object of a different class")
    void equalsWithObjectOfDifferentClass() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        String otherObject = "text";

        // Act & Assert
        assertNotEquals(account, otherObject);
    }

    @Test
    @DisplayName("HashCode of account")
    void hashCodeOfAccount() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act
        int hashCode = account.hashCode();

        // Assert
        assertNotEquals(0, hashCode);
    }

    @Test
    @DisplayName("ToString of account")
    void toStringOfAccount() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);

        // Act
        String toString = account.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("BankAccount"));
        assertTrue(toString.contains("accountNumber"));
        assertTrue(toString.contains("Juan Perez"));
        assertTrue(toString.contains("SAVINGS"));
        assertTrue(toString.contains("1000.0"));
        assertTrue(toString.contains("active=true"));
    }

    @Test
    @DisplayName("Transaction history returns a defensive copy")
    void transactionHistoryReturnsDefensiveCopy() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        BankAccount account = new BankAccount(accountHolder, AccountType.SAVINGS, 1000.0);
        account.deposit(500.0, "Deposit");

        // Act
        var history1 = account.getTransactionHistory();
        var history2 = account.getTransactionHistory();

        // Assert
        assertNotSame(history1, history2);
        assertEquals(history1, history2);
    }
}
