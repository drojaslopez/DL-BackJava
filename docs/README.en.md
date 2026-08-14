# DL-BackJava

Bank account management system in **Java 17** with a **pure domain** (DDD) and layered architecture. It is a framework-free backend: the heart of the system is the business logic (entities and use cases), ready to be connected to any interface (REST API, CLI, etc.).

> Versión en español: [README.es.md](README.es.md)

## Scope

Backend project that models the core of a bank: **account holders**, **bank accounts**, and their **transactions**. It focuses on:

- **Validated business rules**: account states, balances, positive amounts, mandatory descriptions.
- **Pure domain entities**: no framework or infrastructure dependencies.
- **Clean Architecture**: `domain` → `application` (use cases) → `infrastructure` (adapters), with dependencies pointing inward.
- **Comprehensive test coverage**: JaCoCo at **100% line and branch coverage per package**.

**Out of scope:** real persistence (database), REST API, authentication, and frontend. Persistence is currently in-memory (`InMemoryAccountRepository`), interchangeable with any other adapter of the `AccountRepository` port.

## Features

| Operation | Description |
|---|---|
| `createAccount` | Creates an account (savings or checking) with an initial balance and generates a unique `ACC-XXXXXXXX` number. |
| `deposit` | Makes a deposit and records the transaction. |
| `withdraw` | Withdraws money if there is enough balance; otherwise throws `InsufficientBalanceException`. |
| `transfer` | Transfers between accounts (source → destination) validating that both exist, are active, and are not the same account. |
| `checkBalance` | Checks the current balance of an account. |
| `findAccountsByAccountHolder` | Lists a holder's accounts by identification. |
| `activateAccount` / `deactivateAccount` | Activates or deactivates an account (deactivated accounts cannot operate). |

**Domain rules:**

- Amounts are always positive; the initial balance cannot be negative.
- Description is mandatory for every operation.
- Only active accounts can operate.
- You cannot transfer to the same account or to a non-existent or inactive account.
- Every operation records a `Transaction` with type (`DEPOSIT`/`WITHDRAWAL`), date, and description.

### Usage example

```java
AccountRepository repository = new InMemoryAccountRepository();
AccountService service = new AccountService(repository);

AccountHolder holder = new AccountHolder("12345678", "Ana", "García");
BankAccount account = service.createAccount(holder, AccountType.SAVINGS, 1000.0);

service.deposit(account.getAccountNumber(), 500.0, "Payroll");
service.withdraw(account.getAccountNumber(), 200.0, "Grocery shopping");

double balance = service.checkBalance(account.getAccountNumber()); // 1300.0
```

## Domain model

```
AccountHolder 1 ───── * BankAccount 1 ───── * Transaction
```

| Entity | Attributes | Notes |
|---|---|---|
| `AccountHolder` | `identification`, `firstName`, `lastName` | Holder; identity by `identification`. `getFullName()` method. |
| `BankAccount` | `accountNumber`, `accountHolder`, `accountType`, `balance`, `transactionHistory`, `active` | Identity by `accountNumber` (`ACC-XXXXXXXX` format). |
| `Transaction` | `id`, `type`, `amount`, `date`, `description` | Immutable; identity by `id` (`TXN-XXXXXXXX` format). |
| `AccountType` | `SAVINGS`, `CHECKING` | Account type enum. |
| `TransactionType` | `DEPOSIT`, `WITHDRAWAL` | Transaction type enum. |

**Business exceptions** (`domain.exception`): `InvalidAmountException` (invalid amount) and `InsufficientBalanceException` (insufficient balance).

## Structure

```
src/
├── main/java/drl/desafio/
│   ├── domain/
│   │   ├── entity/          # AccountHolder, AccountType, BankAccount, Transaction, TransactionType
│   │   └── exception/       # InvalidAmountException, InsufficientBalanceException
│   ├── application/
│   │   ├── port/            # AccountRepository (interface / contract)
│   │   └── service/         # AccountService (use cases)
│   └── infrastructure/
│       └── persistence/     # InMemoryAccountRepository (in-memory adapter)
└── test/java/drl/desafio/
    ├── domain/entity/       # AccountHolderTest, BankAccountTest, TransactionTest
    ├── application/service/ # AccountServiceTest
    └── infrastructure/persistence/ # InMemoryAccountRepositoryTest
```

## Technologies

- **Java 17** · Maven
- **JUnit 5** + **Mockito** (tests)
- **JaCoCo** (coverage: 100% line and branch per package)

## Build and verification

```bash
mvn clean verify
```

Runs the **103 tests** and the JaCoCo coverage check. The HTML report is at `target/site/jacoco/index.html`.

## Additional documentation

- [README.es.md](README.es.md) — Spanish version of the README
- [docs/analisis.en.md](docs/analisis.en.md) — Structure analysis and change proposal (historical)
