package drl.desafio.dominio;

import drl.desafio.excepciones.ExcepcionMontoInvalido;
import drl.desafio.excepciones.ExcepcionSaldoInsuficiente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CuentaBancariaTest {

    @Test
    @DisplayName("Crear cuenta bancaria con datos válidos")
    void crearCuentaBancariaConDatosValidos() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = 1000.0;

        // Act
        CuentaBancaria cuenta = new CuentaBancaria(titular, tipoCuenta, saldoInicial);

        // Assert
        assertNotNull(cuenta.getNumeroCuenta());
        assertEquals(titular, cuenta.getTitular());
        assertEquals(tipoCuenta, cuenta.getTipoCuenta());
        assertEquals(saldoInicial, cuenta.getSaldo());
        assertTrue(cuenta.isActiva());
        assertTrue(cuenta.getHistorialTransacciones().isEmpty());
    }

    @Test
    @DisplayName("Crear cuenta con titular nulo debe lanzar excepción")
    void crearCuentaConTitularNuloDebeLanzarExcepcion() {
        // Arrange
        Titular titular = null;
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CuentaBancaria(titular, tipoCuenta, saldoInicial)
        );
        assertEquals("El titular no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Crear cuenta con tipo nulo debe lanzar excepción")
    void crearCuentaConTipoNuloDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = null;
        double saldoInicial = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CuentaBancaria(titular, tipoCuenta, saldoInicial)
        );
        assertEquals("El tipo de cuenta no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Crear cuenta con saldo inicial negativo debe lanzar excepción")
    void crearCuentaConSaldoInicialNegativoDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = -100.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CuentaBancaria(titular, tipoCuenta, saldoInicial)
        );
        assertEquals("El saldo inicial no puede ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Crear cuenta con saldo inicial cero")
    void crearCuentaConSaldoInicialCero() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = 0.0;

        // Act
        CuentaBancaria cuenta = new CuentaBancaria(titular, tipoCuenta, saldoInicial);

        // Assert
        assertEquals(0.0, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Depositar monto válido")
    void depositarMontoValido() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        double monto = 500.0;
        String descripcion = "Depósito de nómina";

        // Act
        cuenta.depositar(monto, descripcion);

        // Assert
        assertEquals(1500.0, cuenta.getSaldo());
        assertEquals(1, cuenta.getHistorialTransacciones().size());
        assertEquals(TipoTransaccion.DEPOSITO, cuenta.getHistorialTransacciones().get(0).getTipo());
        assertEquals(monto, cuenta.getHistorialTransacciones().get(0).getMonto());
    }

    @Test
    @DisplayName("Depositar en cuenta inactiva debe lanzar excepción")
    void depositarEnCuentaInactivaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        cuenta.desactivar();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cuenta.depositar(500.0, "Depósito")
        );
        assertEquals("La cuenta no está activa", exception.getMessage());
    }

    @Test
    @DisplayName("Depositar monto cero debe lanzar excepción")
    void depositarMontoCeroDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        ExcepcionMontoInvalido exception = assertThrows(
                ExcepcionMontoInvalido.class,
                () -> cuenta.depositar(0.0, "Depósito")
        );
        assertEquals("El monto a depositar debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("Depositar monto negativo debe lanzar excepción")
    void depositarMontoNegativoDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        ExcepcionMontoInvalido exception = assertThrows(
                ExcepcionMontoInvalido.class,
                () -> cuenta.depositar(-100.0, "Depósito")
        );
        assertEquals("El monto a depositar debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("Depositar con descripción nula debe lanzar excepción")
    void depositarConDescripcionNulaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(500.0, null)
        );
        assertEquals("La descripción no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Depositar con descripción vacía debe lanzar excepción")
    void depositarConDescripcionVaciaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(500.0, "  ")
        );
        assertEquals("La descripción no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Retirar monto válido con saldo suficiente")
    void retirarMontoValidoConSaldoSuficiente() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        double monto = 500.0;
        String descripcion = "Retiro de efectivo";

        // Act
        cuenta.retirar(monto, descripcion);

        // Assert
        assertEquals(500.0, cuenta.getSaldo());
        assertEquals(1, cuenta.getHistorialTransacciones().size());
        assertEquals(TipoTransaccion.RETIRO, cuenta.getHistorialTransacciones().get(0).getTipo());
        assertEquals(monto, cuenta.getHistorialTransacciones().get(0).getMonto());
    }

    @Test
    @DisplayName("Retirar en cuenta inactiva debe lanzar excepción")
    void retirarEnCuentaInactivaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        cuenta.desactivar();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cuenta.retirar(500.0, "Retiro")
        );
        assertEquals("La cuenta no está activa", exception.getMessage());
    }

    @Test
    @DisplayName("Retirar monto cero debe lanzar excepción")
    void retirarMontoCeroDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        ExcepcionMontoInvalido exception = assertThrows(
                ExcepcionMontoInvalido.class,
                () -> cuenta.retirar(0.0, "Retiro")
        );
        assertEquals("El monto a retirar debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("Retirar monto negativo debe lanzar excepción")
    void retirarMontoNegativoDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        ExcepcionMontoInvalido exception = assertThrows(
                ExcepcionMontoInvalido.class,
                () -> cuenta.retirar(-100.0, "Retiro")
        );
        assertEquals("El monto a retirar debe ser mayor a cero", exception.getMessage());
    }

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

    @Test
    @DisplayName("Retirar con descripción nula debe lanzar excepción")
    void retirarConDescripcionNulaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(500.0, null)
        );
        assertEquals("La descripción no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Retirar con descripción vacía debe lanzar excepción")
    void retirarConDescripcionVaciaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(500.0, "")
        );
        assertEquals("La descripción no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Transferir entre cuentas válidas")
    void transferirEntreCuentasValidas() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("87654321", "Maria", "Gomez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuentaDestino = new CuentaBancaria(titular2, TipoCuenta.CORRIENTE, 500.0);
        double monto = 300.0;

        // Act
        cuentaOrigen.transferir(cuentaDestino, monto, "Transferencia");

        // Assert
        assertEquals(700.0, cuentaOrigen.getSaldo());
        assertEquals(800.0, cuentaDestino.getSaldo());
        assertEquals(1, cuentaOrigen.getHistorialTransacciones().size());
        assertEquals(1, cuentaDestino.getHistorialTransacciones().size());
    }

    @Test
    @DisplayName("Transferir desde cuenta inactiva debe lanzar excepción")
    void transferirDesdeCuentaInactivaDebeLanzarExcepcion() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("87654321", "Maria", "Gomez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuentaDestino = new CuentaBancaria(titular2, TipoCuenta.CORRIENTE, 500.0);
        cuentaOrigen.desactivar();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cuentaOrigen.transferir(cuentaDestino, 300.0, "Transferencia")
        );
        assertEquals("La cuenta origen no está activa", exception.getMessage());
    }

    @Test
    @DisplayName("Transferir a cuenta nula debe lanzar excepción")
    void transferirACuentaNulaDebeLanzarExcepcion() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaOrigen.transferir(null, 300.0, "Transferencia")
        );
        assertEquals("La cuenta destino no puede ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Transferir a cuenta inactiva debe lanzar excepción")
    void transferirACuentaInactivaDebeLanzarExcepcion() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("87654321", "Maria", "Gomez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuentaDestino = new CuentaBancaria(titular2, TipoCuenta.CORRIENTE, 500.0);
        cuentaDestino.desactivar();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cuentaOrigen.transferir(cuentaDestino, 300.0, "Transferencia")
        );
        assertEquals("La cuenta destino no está activa", exception.getMessage());
    }

    @Test
    @DisplayName("Transferir a la misma cuenta debe lanzar excepción")
    void transferirALaMismaCuentaDebeLanzarExcepcion() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.transferir(cuenta, 300.0, "Transferencia")
        );
        assertEquals("No se puede transferir a la misma cuenta", exception.getMessage());
    }

    @Test
    @DisplayName("Transferir con saldo insuficiente debe lanzar excepción")
    void transferirConSaldoInsuficienteDebeLanzarExcepcion() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("87654321", "Maria", "Gomez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuentaDestino = new CuentaBancaria(titular2, TipoCuenta.CORRIENTE, 500.0);

        // Act & Assert
        ExcepcionSaldoInsuficiente exception = assertThrows(
                ExcepcionSaldoInsuficiente.class,
                () -> cuentaOrigen.transferir(cuentaDestino, 1500.0, "Transferencia")
        );
        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
    }

    @Test
    @DisplayName("Desactivar cuenta")
    void desactivarCuenta() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act
        cuenta.desactivar();

        // Assert
        assertFalse(cuenta.isActiva());
    }

    @Test
    @DisplayName("Activar cuenta")
    void activarCuenta() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        cuenta.desactivar();

        // Act
        cuenta.activar();

        // Assert
        assertTrue(cuenta.isActiva());
    }

    @Test
    @DisplayName("Igualdad de cuentas por número de cuenta")
    void igualdadDeCuentasPorNumeroCuenta() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta1 = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuenta2 = cuenta1;

        // Act & Assert
        assertEquals(cuenta1, cuenta2);
    }

    @Test
    @DisplayName("Desigualdad de cuentas con diferentes números")
    void desigualdadDeCuentasConDiferentesNumeros() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta1 = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuenta2 = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        assertNotEquals(cuenta1, cuenta2);
    }

    @Test
    @DisplayName("Equals con objeto nulo")
    void equalsConObjetoNulo() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act & Assert
        assertNotEquals(cuenta, null);
    }

    @Test
    @DisplayName("Equals con objeto de diferente clase")
    void equalsConObjetoDeDiferenteClase() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        String otroObjeto = "texto";

        // Act & Assert
        assertNotEquals(cuenta, otroObjeto);
    }

    @Test
    @DisplayName("HashCode de cuenta")
    void hashCodeDeCuenta() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act
        int hashCode = cuenta.hashCode();

        // Assert
        assertNotEquals(0, hashCode);
    }

    @Test
    @DisplayName("ToString de cuenta")
    void toStringDeCuenta() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);

        // Act
        String toString = cuenta.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("CuentaBancaria"));
        assertTrue(toString.contains("numeroCuenta"));
        assertTrue(toString.contains("Juan Perez"));
        assertTrue(toString.contains("AHORROS"));
        assertTrue(toString.contains("1000.0"));
        assertTrue(toString.contains("activa=true"));
    }

    @Test
    @DisplayName("Historial de transacciones retorna copia defensiva")
    void historialDeTransaccionesRetornaCopiaDefensiva() {
        // Arrange
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        cuenta.depositar(500.0, "Depósito");

        // Act
        var historial1 = cuenta.getHistorialTransacciones();
        var historial2 = cuenta.getHistorialTransacciones();

        // Assert
        assertNotSame(historial1, historial2);
        assertEquals(historial1, historial2);
    }
}
