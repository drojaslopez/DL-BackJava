# DL-BackJava — Análisis de Estructura y Propuesta de Cambios

> **Idioma del documento:** español · **Idioma del código:** inglés
> English version: [README.en.md](README.en.md)

Este documento toma como base `TEMPLATE_SYSTEM_README.md`, analiza la estructura actual del repositorio, detecta problemas y propone cambios concretos junto con los elementos que deben añadirse.

---

## 1. Estructura actual del repositorio

```
DL-BackJava/
├── .git/
├── Desafio.md                    # Enunciado del desafío
├── README.md                     # Documentación principal (español)
├── TEMPLATE_SYSTEM_README.md     # Docs del sistema de plantillas (sin referenciar)
├── pom.xml                       # Configuración Maven (Java 17)
├── src/
│   ├── main/java/drl/desafio/
│   │   ├── dominio/
│   │   │   ├── CuentaBancaria.java
│   │   │   ├── TipoCuenta.java
│   │   │   ├── TipoTransaccion.java
│   │   │   ├── Titular.java
│   │   │   └── Transaccion.java
│   │   ├── excepciones/
│   │   │   ├── ExcepcionMontoInvalido.java
│   │   │   └── ExcepcionSaldoInsuficiente.java
│   │   └── servicio/
│   │       ├── RepositorioCuentas.java
│   │       └── ServicioCuenta.java
│   └── test/java/drl/desafio/
│       ├── dominio/
│       │   ├── CuentaBancariaTest.java
│       │   ├── TitularTest.java
│       │   └── TransaccionTest.java
│       └── servicio/
│           └── ServicioCuentaTest.java
└── target/                       # ⚠️ Build output COMMITEADO en git
```

**Aspectos positivos:** dominio puro sin frameworks, excepciones de negocio separadas, tests con patrón AAA, cobertura JaCoCo ~98%, `pom.xml` bien configurado con Java 17.

---

## 2. Problemas detectados

### 🔴 Críticos

| # | Problema | Impacto |
|---|----------|---------|
| C1 | `target/` está commiteado en git (`*.class` versionados) | Repositorio contaminado con artefactos binarios |
| C2 | No existe `.gitignore` | Cualquier artefacto de build/IDE quedará versionado |
| C3 | `TEMPLATE_SYSTEM_README.md` referencia archivos inexistentes (`project-template.yaml`, `structure-analyzer.py`) | Documentación rota / promesa sin implementar |

### 🟠 Importantes

| # | Problema | Impacto |
|---|----------|---------|
| I1 | Todo el código está nombrado en español (`CuentaBancaria`, `ServicioCuenta`, etc.) | Convenciones de código inconsistentes con el estándar (inglés) |
| I2 | Paquetes `dominio`, `excepciones`, `servicio` en español | Alineación pobre con Clean Architecture / DDD |
| I3 | `pom.xml`: `groupId=drl.desafio`, `artifactId=desafioHito1` con `name`/`url` de ejemplo (`FIXME`) | Metadatos de proyecto incompletos |

### 🟡 Recomendados

| # | Problema | Impacto |
|---|----------|---------|
| R1 | No hay archivo `LICENSE` | No se define licencia de distribución |
| R2 | No hay CI/CD (`.github/workflows/`) | Cobertura y tests no se validan automáticamente |
| R3 | No hay plantilla YAML ni analizador del sistema de plantillas | `TEMPLATE_SYSTEM_README.md` queda como docs huérfanas |
| R4 | La documentación solo existe en español | Reduce el alcance de consumo del repositorio |

---

## 3. Propuesta de cambios

### 3.1 Nombrado del código (español → inglés)

Renombrar clases, paquetes y métodos a inglés:

| Español actual | Inglés propuesto |
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

| Método / campo actual | Propuesto |
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

Ejemplo de código tras el renombrado:

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

### 3.2 Estructura de paquetes objetivo (Clean Architecture)

Alineada con el espíritu de `TEMPLATE_SYSTEM_README.md` (capas de dominio, aplicación e infraestructura):

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
        └── InMemoryAccountRepository.java   # 🔧 nuevo (adapter opcional)
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

> **Nota:** la estructura actual (`dominio`/`excepciones`/`servicio`) ya es válida para el desafío de dominio puro. La propuesta de capas es una evolución recomendada, no un requisito del desafío.

### 3.3 Metadatos Maven

- Alinear `groupId` con el nuevo paquete base, p. ej. `drl.desafio`.
- Reemplazar `name`/`url` de ejemplo por metadatos reales (eliminar `FIXME`).
- Añadir `maven-enforcer-plugin` para fijar la versión de Java.

---

## 4. Cosas que hay que añadir

### Checklist de archivos nuevos

| Archivo | Prioridad | Propósito |
|---|---|---|
| `.gitignore` | 🔴 Crítica | Excluir `target/`, `.idea/`, `*.iml`, `.classpath`, `.project`, `.settings/`, `.vscode/`, `*.log` |
| `project-template.yaml` | 🟠 Importante | Plantilla YAML referenciada por `TEMPLATE_SYSTEM_README.md` |
| `structure-analyzer.py` | 🟠 Importante | Analizador Python referenciado por `TEMPLATE_SYSTEM_README.md` |
| `LICENSE` | 🟡 Recomendada | Definir licencia del proyecto |
| `.github/workflows/ci.yml` | 🟡 Recomendada | CI: `mvn clean verify` + chequeo de cobertura JaCoCo |
| `README.es.md` / `README.en.md` | ✅ Hecho | Documentación bilingüe apuntada desde `README.md` |

### Ejemplo de `.gitignore`

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

### Pasos para limpiar `target/` del historial

```bash
# 1. Añadir .gitignore
echo "target/" >> .gitignore

# 2. Quitar target/ del índice (sin borrar archivos locales)
git rm -r --cached target/

# 3. Commit de limpieza
git add .gitignore
git commit -m "chore: remove build artifacts from version control"
```

---

## 5. Roadmap sugerido

1. **Hito 0 — Higiene del repo:** crear `.gitignore`, sacar `target/` de git.
2. **Hito 1 — Código en inglés:** renombrar paquetes, clases, métodos y mensajes; actualizar `pom.xml` y tests; verificar con `mvn clean verify` (cobertura ≥ 1.00 sigue pasando).
3. **Hito 2 — Sistema de plantillas:** crear `project-template.yaml` y `structure-analyzer.py` para dar vida a `TEMPLATE_SYSTEM_README.md`.
4. **Hito 3 — Calidad/CI:** `LICENSE`, workflow de GitHub Actions, enforcer plugin.

---

## 6. Verificación

```bash
mvn clean verify
```

El build ejecuta todos los tests (~96) y el chequeo de JaCoCo exige 100% de cobertura de línea y ramas por paquete. Reporte HTML en `target/site/jacoco/index.html`.
