package drl.desafio.domain.entity;

import java.util.Objects;

public class AccountHolder {
    private final String identification;
    private final String firstName;
    private final String lastName;

    public AccountHolder(String identification, String firstName, String lastName) {
        if (identification == null || identification.trim().isEmpty()) {
            throw new IllegalArgumentException("The identification cannot be null or empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("The first name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("The last name cannot be null or empty");
        }
        this.identification = identification.trim();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    public String getIdentification() {
        return identification;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountHolder that = (AccountHolder) o;
        return Objects.equals(identification, that.identification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identification);
    }

    @Override
    public String toString() {
        return "AccountHolder{" +
                "identification='" + identification + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
