# DL-BackJava

Sistema de gestión de cuentas bancarias en **Java 17** con **dominio puro** (DDD) y arquitectura en capas. Es un backend sin framework web: el corazón del sistema es la lógica de negocio (entidades y casos de uso), lista para conectarse a cualquier interfaz (API REST, CLI, etc.).

> English version: [README.en.md](README.en.md)

## Ámbito

Proyecto backend que modela el núcleo de un banco: **titulares**, **cuentas bancarias** y sus **transacciones**. El foco está en:

- **Reglas de negocio con validación**: estados de cuenta, saldos, montos positivos, descripciones obligatorias.
- **Entidades de dominio puras**: sin dependencias de frameworks ni infraestructura.
- **Clean Architecture**: capas `domain` → `application` (casos de uso) → `infrastructure` (adaptadores), con dependencias dirigidas hacia adentro.
- **Cobertura exhaustiva de tests**: JaCoCo al **100% de líneas y ramas por paquete**.

**Fuera de alcance:** persistencia real (base de datos), API REST, autenticación y frontend. La persistencia actual es en memoria (`InMemoryAccountRepository`), intercambiable por cualquier otro adaptador del puerto `AccountRepository`.

## Funcionalidades

| Operación                                  | Descripción                                                                                                 |
| ------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `createAccount`                           | Crea una cuenta (ahorro o corriente) con saldo inicial y genera un número único`ACC-XXXXXXXX`.           |
| `deposit`                                 | Realiza un depósito y registra la transacción.                                                             |
| `withdraw`                                | Retira dinero si hay saldo suficiente; si no, lanza`InsufficientBalanceException`.                         |
| `transfer`                                | Transfiere entre cuentas (origen → destino) validando que ambas existan, estén activas y no sean la misma. |
| `checkBalance`                            | Consulta el saldo actual de una cuenta.                                                                      |
| `findAccountsByAccountHolder`             | Lista las cuentas de un titular por identificación.                                                         |
| `activateAccount` / `deactivateAccount` | Activa o desactiva una cuenta (las cuentas desactivadas no admiten operaciones).                             |

**Reglas de dominio:**

- Montos siempre positivos; saldo inicial no negativo.
- Descripción obligatoria en toda operación.
- Solo las cuentas activas pueden operar.
- No se puede transferir a la misma cuenta ni a una cuenta inexistente o inactiva.
- Cada operación registra una `Transaction` con tipo (`DEPOSIT`/`WITHDRAWAL`), fecha y descripción.

### Ejemplo de uso

```java
AccountRepository repository = new InMemoryAccountRepository();
AccountService service = new AccountService(repository);

AccountHolder holder = new AccountHolder("12345678", "Ana", "García");
BankAccount account = service.createAccount(holder, AccountType.SAVINGS, 1000.0);

service.deposit(account.getAccountNumber(), 500.0, "Nómina");
service.withdraw(account.getAccountNumber(), 200.0, "Compra supermercado");

double balance = service.checkBalance(account.getAccountNumber()); // 1300.0
```

## Modelo de dominio

```
AccountHolder 1 ───── * BankAccount 1 ───── * Transaction
```

| Entidad             | Atributos                                                                                              | Notas                                                                |
| ------------------- | ------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------- |
| `AccountHolder`   | `identification`, `firstName`, `lastName`                                                        | Titular; identidad por`identification`. Método `getFullName()`. |
| `BankAccount`     | `accountNumber`, `accountHolder`, `accountType`, `balance`, `transactionHistory`, `active` | Identidad por`accountNumber` (formato `ACC-XXXXXXXX`).           |
| `Transaction`     | `id`, `type`, `amount`, `date`, `description`                                                | Inmutable; identidad por`id` (formato `TXN-XXXXXXXX`).           |
| `AccountType`     | `SAVINGS`, `CHECKING`                                                                              | Enum de tipos de cuenta.                                             |
| `TransactionType` | `DEPOSIT`, `WITHDRAWAL`                                                                            | Enum de tipos de transacción.                                       |

**Excepciones de negocio** (`domain.exception`): `InvalidAmountException` (monto inválido) e `InsufficientBalanceException` (saldo insuficiente).

## Estructura

```
src/
├── main/java/drl/desafio/
│   ├── domain/
│   │   ├── entity/          # AccountHolder, AccountType, BankAccount, Transaction, TransactionType
│   │   └── exception/       # InvalidAmountException, InsufficientBalanceException
│   ├── application/
│   │   ├── port/            # AccountRepository (interfaz / contrato)
│   │   └── service/         # AccountService (casos de uso)
│   └── infrastructure/
│       └── persistence/     # InMemoryAccountRepository (adaptador en memoria)
└── test/java/drl/desafio/
    ├── domain/entity/       # AccountHolderTest, BankAccountTest, TransactionTest
    ├── application/service/ # AccountServiceTest
    └── infrastructure/persistence/ # InMemoryAccountRepositoryTest
```

## Tecnologías

- **Java 17** · Maven
- **JUnit 5** + **Mockito** (tests)
- **JaCoCo** (cobertura: 100% de líneas y ramas por paquete)

## Ejecución y verificación

```bash
mvn clean verify
```

Ejecuta los **103 tests** y el chequeo de cobertura JaCoCo. El reporte HTML queda en `target/site/jacoco/index.html`.

## Documentación complementaria

- [README.en.md](README.en.md) — Versión en inglés del README
- [docs/analisis.es.md](docs/analisis.es.md) — Análisis de estructura y propuesta de cambios (histórico)
