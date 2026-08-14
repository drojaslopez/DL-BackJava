package drl.desafio.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime date;
    private final String description;

    public Transaction(String id, TransactionType type, double amount, String description) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("The transaction ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("The transaction type cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("The amount must be greater than zero");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("The description cannot be null or empty");
        }
        this.id = id.trim();
        this.type = type;
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.description = description.trim();
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
