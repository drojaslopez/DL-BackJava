# DL-BackJava — Structure Analysis and Change Proposal

> **Document language:** English · **Code language:** English
> Versión en español: [analisis.es.md](analisis.es.md)

This document uses `TEMPLATE_SYSTEM_README.md` as a baseline, analyzes the current repository structure, detects issues, and proposes concrete changes along with the elements that should be added.

---

## 1. Current repository structure

```
DL-BackJava/
├── .git/
├── Desafio.md                    # Challenge statement
├── README.md                     # Main documentation (Spanish)
├── TEMPLATE_SYSTEM_README.md     # Template system docs (orphaned references)
├── pom.xml                       # Maven config (Java 17)
├── src/
│   ├── main/java/drl/desafio/
│   │   ├── domain/
│   │   │   ├── entity/           # AccountHolder, AccountType, BankAccount, Transaction, TransactionType
│   │   │   └── exception/        # InvalidAmountException, InsufficientBalanceException
│   │   ├── application/
│   │   │   ├── port/             # AccountRepository
│   │   │   └── service/          # AccountService
│   │   └── infrastructure/
│   │       └── persistence/      # InMemoryAccountRepository
│   └── test/java/drl/desafio/
│       ├── domain/entity/
│       ├── application/service/
│       └── infrastructure/persistence/
└── target/                       # ⚠️ Build output COMMITTED to git
```

**Positive aspects:** pure domain with no frameworks, separated business exceptions, AAA-pattern tests, JaCoCo check at 100% line and branch coverage per package, well-configured `pom.xml` (Java 17).

---

## 2. Detected issues

### 🔴 Critical

| # | Issue | Impact |
|---|-------|--------|
| C1 | `target/` is committed to git (`*.class` versioned) | Repository polluted with binary artifacts |
| C2 | No `.gitignore` exists | Any build/IDE artifact will be versioned |
| C3 | `TEMPLATE_SYSTEM_README.md` references non-existent files (`project-template.yaml`, `structure-analyzer.py`) | Broken docs / unfulfilled promise |

### 🟠 Important

| # | Issue | Impact |
|---|-------|--------|
| ~~I1~~ ✅ | ~~All code is named in Spanish (`CuentaBancaria`, `ServicioCuenta`, etc.)~~ — **resolved in Milestone 1** | Inconsistent with the English coding standard |
| ~~I2~~ ✅ | ~~`dominio`, `excepciones`, `servicio` packages in Spanish~~ — **resolved in Milestone 1** | Poor alignment with Clean Architecture / DDD |
| I3 | `pom.xml`: `groupId=drl.desafio`, `artifactId=desafioHito1` with placeholder `name`/`url` (`FIXME`) | Incomplete project metadata |

### 🟡 Recommended

| # | Issue | Impact |
|---|-------|--------|
| R1 | No `LICENSE` file | Distribution license is undefined |
| R2 | No CI/CD (`.github/workflows/`) | Tests and coverage are not validated automatically |
| R3 | No YAML template or analyzer for the template system | `TEMPLATE_SYSTEM_README.md` remains as orphaned docs |
| R4 | Documentation exists only in Spanish | Limits repository reach |

---

## 3. Change proposal

### 3.1 Code naming (Spanish → English) — ✅ applied in Milestone 1

Rename packages, classes, and methods to English:

| Current Spanish | Proposed English |
|---|---|
| `drl.desafio.dominio` | `drl.desafio.domain` |
| `drl.desafio.servicio` | `drl.desafio.service` |
| `drl.desafio.excepciones` | `drl.desafio.exception` |
| `Titular` | `AccountHolder` |
| `CuentaBancaria` | `BankAccount` |
| `Transaccion` | `Transaction` |
| `TipoCuenta` | `AccountType` |
| `TipoTransaccion` | `TransactionType` |
| `ExcepcionMontoInvalido` | `InvalidAmountException` |
| `ExcepcionSaldoInsuficiente` | `InsufficientBalanceException` |
| `RepositorioCuentas` | `AccountRepository` |
| `ServicioCuenta` | `AccountService` |

| Current method / field | Proposed |
|---|---|
| `numeroCuenta` | `accountNumber` |
| `generarNumeroCuenta()` | `generateAccountNumber()` |
| `depositar()` | `deposit()` |
| `retirar()` | `withdraw()` |
| `transferir()` | `transfer()` |
| `desactivar()` / `activar()` | `deactivate()` / `activate()` |
| `getSaldo()` | `getBalance()` |
| `getHistorialTransacciones()` | `getTransactionHistory()` |
| `getNombreCompleto()` | `getFullName()` |
| `guardar()` | `save()` |
| `buscarPorNumero()` | `findByNumber()` |
| `buscarPorTitular()` | `findByAccountHolder()` |
| `crearCuenta()` | `createAccount()` |
| `consultarSaldo()` | `checkBalance()` |
| `desactivarCuenta()` / `activarCuenta()` | `deactivateAccount()` / `activateAccount()` |

Code example after renaming:

```java
public class BankAccount {
    private final String accountNumber;
    private final AccountHolder accountHolder;
    private final AccountType accountType;
    private double balance;
    private final List<Transaction> transactionHistory;
    private boolean active;

    public void deposit(double amount, String description) {
        if (!active) {
            throw new IllegalStateException("The account is not active");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        balance += amount;
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        transactionHistory.add(new Transaction(transactionId, TransactionType.DEPOSIT, amount, description));
    }
}
```

### 3.2 Target package structure (Clean Architecture) — ✅ applied in Milestone 1

Aligned with the spirit of `TEMPLATE_SYSTEM_README.md` (domain, application, and infrastructure layers):

```
src/main/java/drl/desafio/
├── domain/
│   ├── entity/
│   │   ├── AccountHolder.java
│   │   ├── AccountType.java
│   │   ├── BankAccount.java
│   │   ├── Transaction.java
│   │   └── TransactionType.java
│   └── exception/
│       ├── InvalidAmountException.java
│       └── InsufficientBalanceException.java
├── application/
│   ├── port/
│   │   └── AccountRepository.java
│   └── service/
│       └── AccountService.java
└── infrastructure/
    └── persistence/
        └── InMemoryAccountRepository.java   # 🔧 new (optional adapter)
```

```
src/test/java/drl/desafio/
├── domain/
│   ├── BankAccountTest.java
│   ├── AccountHolderTest.java
│   └── TransactionTest.java
└── application/
    └── AccountServiceTest.java
```

> **Note:** the original structure (`dominio`/`excepciones`/`servicio`) was already valid for the pure-domain challenge. The `domain`/`application`/`infrastructure` layers are now applied (Milestone 1).

### 3.3 Maven metadata

- `groupId` is already aligned with the base package (`drl.desafio`). ✅
- Placeholder `name`/`url` replaced (`FIXME` removed). ✅
- Add `maven-enforcer-plugin` to pin the Java version (pending — Milestone 3).

---

## 4. What to add

### Checklist of new files

| File | Priority | Purpose |
|---|---|---|
| `.gitignore` | 🔴 Critical | Exclude `target/`, `.idea/`, `*.iml`, `.classpath`, `.project`, `.settings/`, `.vscode/`, `*.log` |
| `project-template.yaml` | 🟠 Important | YAML template referenced by `TEMPLATE_SYSTEM_README.md` |
| `structure-analyzer.py` | 🟠 Important | Python analyzer referenced by `TEMPLATE_SYSTEM_README.md` |
| `LICENSE` | 🟡 Recommended | Define project license |
| `.github/workflows/ci.yml` | 🟡 Recommended | CI: `mvn clean verify` + JaCoCo coverage check |
| `README.es.md` / `README.en.md` | ✅ Done | Bilingual docs linked from `README.md` |

### Example `.gitignore`

```gitignore
# Build output
target/

# IDE
.idea/
*.iml
.classpath
.project
.settings/
.vscode/

# OS
.DS_Store
Thumbs.db
```

### Steps to remove `target/` from version control

```bash
# 1. Add .gitignore
echo "target/" >> .gitignore

# 2. Remove target/ from the index (keep local files)
git rm -r --cached target/

# 3. Cleanup commit
git add .gitignore
git commit -m "chore: remove build artifacts from version control"
```

---

## 5. Suggested roadmap

1. ✅ **Milestone 0 — Repo hygiene:** create `.gitignore`, untrack `target/`.
2. ✅ **Milestone 1 — English code:** rename packages, classes, methods, and messages; update `pom.xml` and tests; verify with `mvn clean verify` (≥ 1.00 coverage still passes).
3. **Milestone 2 — Template system:** create `project-template.yaml` and `structure-analyzer.py` to bring `TEMPLATE_SYSTEM_README.md` to life.
4. **Milestone 3 — Quality/CI:** `LICENSE`, GitHub Actions workflow, enforcer plugin.

---

## 6. Verification

```bash
mvn clean verify
```

The build runs all tests (103) and the JaCoCo check enforces 100% line and branch coverage per package. HTML report at `target/site/jacoco/index.html`.
