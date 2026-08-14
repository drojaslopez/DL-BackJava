package drl.desafio.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AccountHolderTest {

    @Test
    @DisplayName("Create account holder with valid data")
    void createAccountHolderWithValidData() {
        // Arrange
        String identification = "12345678";
        String firstName = "Juan";
        String lastName = "Perez";

        // Act
        AccountHolder accountHolder = new AccountHolder(identification, firstName, lastName);

        // Assert
        assertEquals(identification, accountHolder.getIdentification());
        assertEquals(firstName, accountHolder.getFirstName());
        assertEquals(lastName, accountHolder.getLastName());
        assertEquals("Juan Perez", accountHolder.getFullName());
    }

    @Test
    @DisplayName("Create account holder with null identification must throw exception")
    void createAccountHolderWithNullIdentificationMustThrowException() {
        // Arrange
        String identification = null;
        String firstName = "Juan";
        String lastName = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountHolder(identification, firstName, lastName)
        );
        assertEquals("The identification cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create account holder with blank identification must throw exception")
    void createAccountHolderWithBlankIdentificationMustThrowException() {
        // Arrange
        String identification = "   ";
        String firstName = "Juan";
        String lastName = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountHolder(identification, firstName, lastName)
        );
        assertEquals("The identification cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create account holder with null first name must throw exception")
    void createAccountHolderWithNullFirstNameMustThrowException() {
        // Arrange
        String identification = "12345678";
        String firstName = null;
        String lastName = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountHolder(identification, firstName, lastName)
        );
        assertEquals("The first name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create account holder with empty first name must throw exception")
    void createAccountHolderWithEmptyFirstNameMustThrowException() {
        // Arrange
        String identification = "12345678";
        String firstName = "";
        String lastName = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountHolder(identification, firstName, lastName)
        );
        assertEquals("The first name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create account holder with null last name must throw exception")
    void createAccountHolderWithNullLastNameMustThrowException() {
        // Arrange
        String identification = "12345678";
        String firstName = "Juan";
        String lastName = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountHolder(identification, firstName, lastName)
        );
        assertEquals("The last name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Create account holder with blank last name must throw exception")
    void createAccountHolderWithBlankLastNameMustThrowException() {
        // Arrange
        String identification = "12345678";
        String firstName = "Juan";
        String lastName = "  ";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountHolder(identification, firstName, lastName)
        );
        assertEquals("The last name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Trim whitespace from fields")
    void trimWhitespaceFromFields() {
        // Arrange
        String identification = " 12345678 ";
        String firstName = " Juan ";
        String lastName = " Perez ";

        // Act
        AccountHolder accountHolder = new AccountHolder(identification, firstName, lastName);

        // Assert
        assertEquals("12345678", accountHolder.getIdentification());
        assertEquals("Juan", accountHolder.getFirstName());
        assertEquals("Perez", accountHolder.getLastName());
    }

    @Test
    @DisplayName("Equality of account holders by identification")
    void equalityOfAccountHoldersByIdentification() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("12345678", "Maria", "Gomez");
        AccountHolder holder3 = new AccountHolder("87654321", "Juan", "Perez");

        // Act & Assert
        assertEquals(holder1, holder2);
        assertNotEquals(holder1, holder3);
    }

    @Test
    @DisplayName("Equals with null object")
    void equalsWithNullObject() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");

        // Act & Assert
        assertNotEquals(accountHolder, null);
    }

    @Test
    @DisplayName("Equals with object of a different class")
    void equalsWithObjectOfDifferentClass() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");
        String otherObject = "text";

        // Act & Assert
        assertNotEquals(accountHolder, otherObject);
    }

    @Test
    @DisplayName("HashCode consistent with equals")
    void hashCodeConsistentWithEquals() {
        // Arrange
        AccountHolder holder1 = new AccountHolder("12345678", "Juan", "Perez");
        AccountHolder holder2 = new AccountHolder("12345678", "Maria", "Gomez");

        // Act & Assert
        assertEquals(holder1.hashCode(), holder2.hashCode());
    }

    @Test
    @DisplayName("ToString of account holder")
    void toStringOfAccountHolder() {
        // Arrange
        AccountHolder accountHolder = new AccountHolder("12345678", "Juan", "Perez");

        // Act
        String toString = accountHolder.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("AccountHolder"));
        assertTrue(toString.contains("12345678"));
        assertTrue(toString.contains("Juan"));
        assertTrue(toString.contains("Perez"));
    }
}
