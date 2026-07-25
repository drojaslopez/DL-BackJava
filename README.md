# Sistema de Gestión de Cuentas Bancarias
## Daniel Rojas Lopez

## Objetivo del Proyecto

El desafío consistió en crear un repositorio estructurado que cumpla con tres requerimientos fundamentales:

1. **Core de Entidades de Dominio Puro** (3 Puntos) - Modelo de negocio en Java puro, libre de acoplamientos
2. **Suite Automatizada con JUnit 5 y Mockito** (3 Puntos) - Tests exhaustivos con patrón AAA
3. **Cobertura del 100% en Métodos Críticos** (4 Puntos) - Cobertura verificable con JaCoCo

##  Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas con separación clara de responsabilidades:

```
src/
├── main/java/drl/desafio/
│   ├── dominio/           # Entidades de dominio puro
│   ├── excepciones/      # Excepciones de negocio personalizadas
│   └── servicio/         # Servicios con lógica de negocio
└── test/java/drl/desafio/
    ├── dominio/          # Tests de entidades
    └── servicio/         # Tests de servicios con Mockito
```

##  Modelo de Dominio

### Entidades Principales

El sistema gestiona cuentas bancarias con las siguientes entidades:

#### Titular
Representa a la persona propietaria de una cuenta bancaria. Incluye validaciones para asegurar que los datos esenciales estén presentes.

```java
public class Titular {
    private final String identificacion;
    private final String nombre;
    private final String apellido;

    public Titular(String identificacion, String nombre, String apellido) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación no puede ser nula o vacía");
        }
        // ... más validaciones
        this.identificacion = identificacion.trim();
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
    }
}
```

**¿Cómo funciona?**
- El constructor valida que ningún campo esencial sea nulo o vacío
- Aplica `trim()` para eliminar espacios en blanco innecesarios
- Los campos son `final` para garantizar inmutabilidad
- Implementa `equals()` y `hashCode()` basados en la identificación

#### CuentaBancaria
Es la entidad central del sistema. Maneja el saldo, transacciones y operaciones bancarias fundamentales.

```java
public class CuentaBancaria {
    private final String numeroCuenta;
    private final Titular titular;
    private final TipoCuenta tipoCuenta;
    private double saldo;
    private final List<Transaccion> historialTransacciones;
    private boolean activa;

    public void depositar(double monto, String descripcion) {
        if (!activa) {
            throw new IllegalStateException("La cuenta no está activa");
        }
        if (monto <= 0) {
            throw new ExcepcionMontoInvalido("El monto a depositar debe ser mayor a cero");
        }
        
        saldo += monto;
        Transaccion transaccion = new Transaccion(/* ... */);
        historialTransacciones.add(transaccion);
    }
}
```

**¿Cómo funciona?**
- Valida que la cuenta esté activa antes de permitir operaciones
- Verifica que el monto sea positivo
- Actualiza el saldo automáticamente
- Registra cada transacción en el historial
- Lanza excepciones específicas del dominio

#### Transaccion
Representa cualquier movimiento de dinero (depósito o retiro) con metadatos completos.

```java
public class Transaccion {
    private final String id;
    private final TipoTransaccion tipo;
    private final double monto;
    private final LocalDateTime fecha;
    private final String descripcion;

    public Transaccion(String id, TipoTransaccion tipo, double monto, String descripcion) {
        // Validaciones de integridad
        this.fecha = LocalDateTime.now(); // Timestamp automático
    }
}
```

**¿Cómo funciona?**
- Genera automáticamente la fecha/hora de la transacción
- Valida que el monto sea mayor a cero
- Usa un ID único para identificar cada transacción
- Incluye una descripción para auditoría

### Excepciones de Negocio

El sistema define excepciones personalizadas para manejar errores de negocio de forma específica:

```java
// Excepción para cuando no hay saldo suficiente
public class ExcepcionSaldoInsuficiente extends RuntimeException {
    public ExcepcionSaldoInsuficiente(String mensaje) {
        super(mensaje);
    }
}

// Excepción para montos inválidos
public class ExcepcionMontoInvalido extends RuntimeException {
    public ExcepcionMontoInvalido(String mensaje) {
        super(mensaje);
    }
}
```

**¿Por qué excepciones personalizadas?**
- Permiten un manejo de errores más específico
- Facilitan el testing con `assertThrows`
- Mejoran la legibilidad del código de negocio
- Separan errores de negocio de errores técnicos

## Servicios con Inyección por Constructor

### ServicioCuenta
Orquesta las operaciones de negocio utilizando el patrón de inyección de dependencias por constructor.

```java
public class ServicioCuenta {
    private final RepositorioCuentas repositorioCuentas;

    public ServicioCuenta(RepositorioCuentas repositorioCuentas) {
        if (repositorioCuentas == null) {
            throw new IllegalArgumentException("El repositorio de cuentas no puede ser nulo");
        }
        this.repositorioCuentas = repositorioCuentas;
    }

    public void transferir(String cuentaOrigen, String cuentaDestino, double monto, String descripcion) {
        // Validaciones de entrada
        if (cuentaOrigen == null || cuentaOrigen.trim().isEmpty()) {
            throw new IllegalArgumentException("La cuenta origen no puede ser nula o vacía");
        }
        
        // Recuperación de entidades
        CuentaBancaria origen = repositorioCuentas.buscarPorNumero(cuentaOrigen)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));
        
        CuentaBancaria destino = repositorioCuentas.buscarPorNumero(cuentaDestino)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));
        
        // Ejecución de la lógica de negocio
        origen.transferir(destino, monto, descripcion);
    }
}
```

**¿Cómo funciona?**
- Recibe el repositorio por constructor (inyección de dependencias)
- Valida los parámetros de entrada
- Usa `Optional` para manejar la ausencia de cuentas
- Delega la lógica de transferencia a las entidades
- Facilita el testing con Mockito

### RepositorioCuentas
Interfaz que define el contrato para persistencia de cuentas, permitiendo diferentes implementaciones.

```java
public interface RepositorioCuentas {
    void guardar(CuentaBancaria cuenta);
    Optional<CuentaBancaria> buscarPorNumero(String numeroCuenta);
    List<CuentaBancaria> buscarPorTitular(String identificacion);
    boolean existe(String numeroCuenta);
}
```

**¿Por qué una interfaz?**
- Permite cambiar la implementación sin afectar el servicio
- Facilita el testing con mocks
- Sigue el principio de inversión de dependencias
- Habilita múltiples estrategias de persistencia

## Suite de Tests

### Patrón AAA (Arrange, Act, Assert)

Todos los tests siguen rigurosamente el patrón AAA para claridad y mantenibilidad:

```java
@Test
@DisplayName("Depositar monto válido")
void depositarMontoValido() {
    // Arrange - Preparación del escenario
    Titular titular = new Titular("12345678", "Juan", "Perez");
    CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
    double monto = 500.0;
    String descripcion = "Depósito de nómina";

    // Act - Ejecución de la acción a probar
    cuenta.depositar(monto, descripcion);

    // Assert - Verificación de resultados
    assertEquals(1500.0, cuenta.getSaldo());
    assertEquals(1, cuenta.getHistorialTransacciones().size());
    assertEquals(TipoTransaccion.DEPOSITO, cuenta.getHistorialTransacciones().get(0).getTipo());
}
```

**¿Por qué el patrón AAA?**
- Separa claramente cada fase del test
- Mejora la legibilidad y mantenibilidad
- Facilita la identificación de problemas
- Es un estándar de la industria

### Testing de Excepciones con assertThrows

Las excepciones de negocio se prueban usando `assertThrows`:

```java
@Test
@DisplayName("Retirar con saldo insuficiente debe lanzar excepción")
void retirarConSaldoInsuficienteDebeLanzarExcepcion() {
    // Arrange
    Titular titular = new Titular("12345678", "Juan", "Perez");
    CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

    // Act & Assert
    ExcepcionSaldoInsuficiente exception = assertThrows(
            ExcepcionSaldoInsuficiente.class,
            () -> cuenta.retirar(1500.0, "Retiro")
    );
    assertTrue(exception.getMessage().contains("Saldo insuficiente"));
}
```

**¿Cómo funciona?**
- `assertThrows` captura la excepción esperada
- Verifica el tipo exacto de la excepción
- Permite validar el mensaje de error
- Garantiza que el flujo de error funciona correctamente

### Mockito para Doble de Prueba

Los servicios se prueban usando Mockito para simular dependencias:

```java
@ExtendWith(MockitoExtension.class)
class ServicioCuentaTest {

    @Mock
    private RepositorioCuentas repositorioCuentas;

    @Test
    @DisplayName("Depositar en cuenta existente")
    void depositarEnCuentaExistente() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta))
                .thenReturn(Optional.of(cuenta));

        // Act
        servicioCuenta.depositar(numeroCuenta, 500.0, "Depósito");

        // Assert
        assertEquals(1500.0, cuenta.getSaldo());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }
}
```

**¿Cómo funciona Mockito?**
- `@Mock` crea un doble de prueba del repositorio
- `when().thenReturn()` define el comportamiento simulado
- `verify()` confirma que se llamó al método esperado
- Permite probar el servicio sin dependencias reales

## Cobertura de Código con JaCoCo

El proyecto está configurado para garantizar el 100% de cobertura en métodos críticos:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>1.00</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>1.00</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

**¿Qué garantiza esta configuración?**
- **Line Coverage 100%**: Cada línea de código es ejecutada por algún test
- **Branch Coverage 100%**: Cada condición (if、else、switch) se prueba en todas sus ramas
- El build falla si no se cumple el objetivo
- Genera un reporte HTML en `target/site/jacoco/index.html`

##  Ejecución del Proyecto

### Requisitos Previos
- Java 21 o superior
- Maven 3.6+ 
- IDE compatible (IntelliJ IDEA, Eclipse, VS Code)

### Compilar y Ejecutar Tests

```bash
mvn clean test
```

Este comando:
1. Limpia el directorio `target`
2. Compila el código fuente
3. Ejecuta todos los tests (69 tests en total)
4. Genera el reporte de JaCoCo

### Verificar Reporte de Cobertura

Después de ejecutar los tests, abre el reporte en tu navegador:

```
target/site/jacoco/index.html
```

El reporte muestra:
- Cobertura por paquete y clase
- Líneas no cubiertas en rojo
- Ramas condicionales no cubiertas
- Métricas detalladas de cobertura

### Ejecutar Solo Tests Específicos

```bash
# Tests de una clase específica
mvn test -Dtest=CuentaBancariaTest

# Tests con un nombre específico
mvn test -Dtest=ServicioCuentaTest#transferirEntreCuentasExistentes
```

