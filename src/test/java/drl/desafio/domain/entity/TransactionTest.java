package drl.desafio.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Create transaction with valid data")
    void createTransactionWithValidData() {
        // Arrange
        String id = "TXN-12345678";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 1000.0;
        String description = "Initial deposit";

        // Act
        Transaction transaction = new Transaction(id, type, amount, description);

        // Assert
        assertEquals(id, transaction.getId());
        assertEquals(type, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertNotNull(transaction.getDate());
    }

    @Test
    @DisplayName("Create transaction with null ID must throw exception")
    void createTransactionWithNullIdMustThrowException() {
        // Arrange
        String id = null;
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 1000.0;
        String description = "Initial deposit";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The transaction ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create transaction with blank ID must throw exception")
    void createTransactionWithBlankIdMustThrowException() {
        // Arrange
        String id = "  ";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 1000.0;
        String description = "Initial deposit";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The transaction ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create transaction with null type must throw exception")
    void createTransactionWithNullTypeMustThrowException() {
        // Arrange
        String id = "TXN-12345678";
        TransactionType type = null;
        double amount = 1000.0;
        String description = "Initial deposit";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The transaction type cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Create transaction with zero amount must throw exception")
    void createTransactionWithZeroAmountMustThrowException() {
        // Arrange
        String id = "TXN-12345678";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 0.0;
        String description = "Initial deposit";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Create transaction with negative amount must throw exception")
    void createTransactionWithNegativeAmountMustThrowException() {
        // Arrange
        String id = "TXN-12345678";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = -100.0;
        String description = "Initial deposit";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Create transaction with null description must throw exception")
    void createTransactionWithNullDescriptionMustThrowException() {
        // Arrange
        String id = "TXN-12345678";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 1000.0;
        String description = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The description cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create transaction with empty description must throw exception")
    void createTransactionWithEmptyDescriptionMustThrowException() {
        // Arrange
        String id = "TXN-12345678";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 1000.0;
        String description = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(id, type, amount, description)
        );
        assertEquals("The description cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Trim whitespace from fields")
    void trimWhitespaceFromFields() {
        // Arrange
        String id = " TXN-12345678 ";
        TransactionType type = TransactionType.DEPOSIT;
        double amount = 1000.0;
        String description = " Initial deposit ";

        // Act
        Transaction transaction = new Transaction(id, type, amount, description);

        // Assert
        assertEquals("TXN-12345678", transaction.getId());
        assertEquals("Initial deposit", transaction.getDescription());
    }

    @Test
    @DisplayName("Equality of transactions by ID")
    void equalityOfTransactionsById() {
        // Arrange
        Transaction transaction1 = new Transaction("TXN-12345678", TransactionType.DEPOSIT, 1000.0, "Deposit");
        Transaction transaction2 = new Transaction("TXN-12345678", TransactionType.WITHDRAWAL, 500.0, "Withdrawal");
        Transaction transaction3 = new Transaction("TXN-87654321", TransactionType.DEPOSIT, 1000.0, "Deposit");

        // Act & Assert
        assertEquals(transaction1, transaction2);
        assertNotEquals(transaction1, transaction3);
    }

    @Test
    @DisplayName("Equals with the same instance")
    void equalsWithSameInstance() {
        // Arrange
        Transaction transaction = new Transaction("TXN-12345678", TransactionType.DEPOSIT, 1000.0, "Deposit");

        // Act & Assert
        assertTrue(transaction.equals(transaction));
    }

    @Test
    @DisplayName("Equals with null object")
    void equalsWithNullObject() {
        // Arrange
        Transaction transaction = new Transaction("TXN-12345678", TransactionType.DEPOSIT, 1000.0, "Deposit");

        // Act & Assert
        assertNotEquals(transaction, null);
    }

    @Test
    @DisplayName("Equals with object of a different class")
    void equalsWithObjectOfDifferentClass() {
        // Arrange
        Transaction transaction = new Transaction("TXN-12345678", TransactionType.DEPOSIT, 1000.0, "Deposit");
        String otherObject = "text";

        // Act & Assert
        assertNotEquals(transaction, otherObject);
    }

    @Test
    @DisplayName("HashCode consistent with equals")
    void hashCodeConsistentWithEquals() {
        // Arrange
        Transaction transaction1 = new Transaction("TXN-12345678", TransactionType.DEPOSIT, 1000.0, "Deposit");
        Transaction transaction2 = new Transaction("TXN-12345678", TransactionType.WITHDRAWAL, 500.0, "Withdrawal");

        // Act & Assert
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    @DisplayName("ToString of transaction")
    void toStringOfTransaction() {
        // Arrange
        Transaction transaction = new Transaction("TXN-12345678", TransactionType.DEPOSIT, 1000.0, "Initial deposit");

        // Act
        String toString = transaction.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("Transaction"));
        assertTrue(toString.contains("TXN-12345678"));
        assertTrue(toString.contains("DEPOSIT"));
        assertTrue(toString.contains("1000.0"));
        assertTrue(toString.contains("Initial deposit"));
    }
}
