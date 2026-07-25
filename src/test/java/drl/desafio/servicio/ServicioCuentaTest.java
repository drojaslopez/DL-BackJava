package drl.desafio.servicio;

import drl.desafio.dominio.CuentaBancaria;
import drl.desafio.dominio.TipoCuenta;
import drl.desafio.dominio.Titular;
import drl.desafio.excepciones.ExcepcionMontoInvalido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioCuentaTest {

    @Mock
    private RepositorioCuentas repositorioCuentas;

    @Test
    @DisplayName("Crear cuenta con datos válidos")
    void crearCuentaConDatosValidos() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = 1000.0;

        // Act
        CuentaBancaria cuenta = servicioCuenta.crearCuenta(titular, tipoCuenta, saldoInicial);

        // Assert
        assertNotNull(cuenta);
        assertEquals(titular, cuenta.getTitular());
        assertEquals(tipoCuenta, cuenta.getTipoCuenta());
        assertEquals(saldoInicial, cuenta.getSaldo());
        verify(repositorioCuentas, times(1)).guardar(any(CuentaBancaria.class));
    }

    @Test
    @DisplayName("Crear cuenta con titular nulo debe lanzar excepción")
    void crearCuentaConTitularNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        Titular titular = null;
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.crearCuenta(titular, tipoCuenta, saldoInicial)
        );
        assertEquals("El titular no puede ser nulo", exception.getMessage());
        verify(repositorioCuentas, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear cuenta con tipo nulo debe lanzar excepción")
    void crearCuentaConTipoNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = null;
        double saldoInicial = 1000.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.crearCuenta(titular, tipoCuenta, saldoInicial)
        );
        assertEquals("El tipo de cuenta no puede ser nulo", exception.getMessage());
        verify(repositorioCuentas, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear cuenta con saldo inicial negativo debe lanzar excepción")
    void crearCuentaConSaldoInicialNegativoDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        Titular titular = new Titular("12345678", "Juan", "Perez");
        TipoCuenta tipoCuenta = TipoCuenta.AHORROS;
        double saldoInicial = -100.0;

        // Act & Assert
        ExcepcionMontoInvalido exception = assertThrows(
                ExcepcionMontoInvalido.class,
                () -> servicioCuenta.crearCuenta(titular, tipoCuenta, saldoInicial)
        );
        assertEquals("El saldo inicial no puede ser negativo", exception.getMessage());
        verify(repositorioCuentas, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear servicio con repositorio nulo debe lanzar excepción")
    void crearServicioConRepositorioNuloDebeLanzarExcepcion() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServicioCuenta(null)
        );
        assertEquals("El repositorio de cuentas no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Depositar en cuenta existente")
    void depositarEnCuentaExistente() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        double monto = 500.0;
        String descripcion = "Depósito de nómina";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.of(cuenta));

        // Act
        servicioCuenta.depositar(numeroCuenta, monto, descripcion);

        // Assert
        assertEquals(1500.0, cuenta.getSaldo());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Depositar en cuenta inexistente debe lanzar excepción")
    void depositarEnCuentaInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        double monto = 500.0;
        String descripcion = "Depósito";
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.depositar(numeroCuenta, monto, descripcion)
        );
        assertEquals("Cuenta no encontrada: " + numeroCuenta, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Depositar con número de cuenta nulo debe lanzar excepción")
    void depositarConNumeroDeCuentaNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.depositar(null, 500.0, "Depósito")
        );
        assertEquals("El número de cuenta no puede ser nulo o vacío", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Depositar con número de cuenta vacío debe lanzar excepción")
    void depositarConNumeroDeCuentaVacioDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.depositar("  ", 500.0, "Depósito")
        );
        assertEquals("El número de cuenta no puede ser nulo o vacío", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Retirar de cuenta existente con saldo suficiente")
    void retirarDeCuentaExistenteConSaldoSuficiente() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        double monto = 500.0;
        String descripcion = "Retiro de efectivo";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.of(cuenta));

        // Act
        servicioCuenta.retirar(numeroCuenta, monto, descripcion);

        // Assert
        assertEquals(500.0, cuenta.getSaldo());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Retirar de cuenta inexistente debe lanzar excepción")
    void retirarDeCuentaInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        double monto = 500.0;
        String descripcion = "Retiro";
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.retirar(numeroCuenta, monto, descripcion)
        );
        assertEquals("Cuenta no encontrada: " + numeroCuenta, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Retirar con número de cuenta nulo debe lanzar excepción")
    void retirarConNumeroDeCuentaNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.retirar(null, 500.0, "Retiro")
        );
        assertEquals("El número de cuenta no puede ser nulo o vacío", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Transferir entre cuentas existentes")
    void transferirEntreCuentasExistentes() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String cuentaOrigenNum = "CTA-12345678";
        String cuentaDestinoNum = "CTA-87654321";
        double monto = 300.0;
        String descripcion = "Transferencia";
        
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("87654321", "Maria", "Gomez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuentaDestino = new CuentaBancaria(titular2, TipoCuenta.CORRIENTE, 500.0);
        
        when(repositorioCuentas.buscarPorNumero(cuentaOrigenNum)).thenReturn(Optional.of(cuentaOrigen));
        when(repositorioCuentas.buscarPorNumero(cuentaDestinoNum)).thenReturn(Optional.of(cuentaDestino));

        // Act
        servicioCuenta.transferir(cuentaOrigenNum, cuentaDestinoNum, monto, descripcion);

        // Assert
        assertEquals(700.0, cuentaOrigen.getSaldo());
        assertEquals(800.0, cuentaDestino.getSaldo());
        verify(repositorioCuentas, times(1)).buscarPorNumero(cuentaOrigenNum);
        verify(repositorioCuentas, times(1)).buscarPorNumero(cuentaDestinoNum);
    }

    @Test
    @DisplayName("Transferir con cuenta origen nula debe lanzar excepción")
    void transferirConCuentaOrigenNulaDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.transferir(null, "CTA-87654321", 300.0, "Transferencia")
        );
        assertEquals("La cuenta origen no puede ser nula o vacía", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Transferir con cuenta destino nula debe lanzar excepción")
    void transferirConCuentaDestinoNulaDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.transferir("CTA-12345678", null, 300.0, "Transferencia")
        );
        assertEquals("La cuenta destino no puede ser nula o vacía", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Transferir a la misma cuenta debe lanzar excepción")
    void transferirALaMismaCuentaDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.transferir(numeroCuenta, numeroCuenta, 300.0, "Transferencia")
        );
        assertEquals("No se puede transferir a la misma cuenta", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Transferir con cuenta origen inexistente debe lanzar excepción")
    void transferirConCuentaOrigenInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String cuentaOrigenNum = "CTA-12345678";
        String cuentaDestinoNum = "CTA-87654321";
        
        when(repositorioCuentas.buscarPorNumero(cuentaOrigenNum)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.transferir(cuentaOrigenNum, cuentaDestinoNum, 300.0, "Transferencia")
        );
        assertEquals("Cuenta origen no encontrada: " + cuentaOrigenNum, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(cuentaOrigenNum);
        verify(repositorioCuentas, never()).buscarPorNumero(cuentaDestinoNum);
    }

    @Test
    @DisplayName("Transferir con cuenta destino inexistente debe lanzar excepción")
    void transferirConCuentaDestinoInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String cuentaOrigenNum = "CTA-12345678";
        String cuentaDestinoNum = "CTA-87654321";
        
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuentaOrigen = new CuentaBancaria(titular1, TipoCuenta.AHORROS, 1000.0);
        
        when(repositorioCuentas.buscarPorNumero(cuentaOrigenNum)).thenReturn(Optional.of(cuentaOrigen));
        when(repositorioCuentas.buscarPorNumero(cuentaDestinoNum)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.transferir(cuentaOrigenNum, cuentaDestinoNum, 300.0, "Transferencia")
        );
        assertEquals("Cuenta destino no encontrada: " + cuentaDestinoNum, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(cuentaOrigenNum);
        verify(repositorioCuentas, times(1)).buscarPorNumero(cuentaDestinoNum);
    }

    @Test
    @DisplayName("Consultar saldo de cuenta existente")
    void consultarSaldoDeCuentaExistente() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.of(cuenta));

        // Act
        double saldo = servicioCuenta.consultarSaldo(numeroCuenta);

        // Assert
        assertEquals(1000.0, saldo);
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Consultar saldo de cuenta inexistente debe lanzar excepción")
    void consultarSaldoDeCuentaInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.consultarSaldo(numeroCuenta)
        );
        assertEquals("Cuenta no encontrada: " + numeroCuenta, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Consultar saldo con número de cuenta nulo debe lanzar excepción")
    void consultarSaldoConNumeroDeCuentaNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.consultarSaldo(null)
        );
        assertEquals("El número de cuenta no puede ser nulo o vacío", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Buscar cuentas por titular")
    void buscarCuentasPorTitular() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String identificacion = "12345678";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta1 = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        CuentaBancaria cuenta2 = new CuentaBancaria(titular, TipoCuenta.CORRIENTE, 500.0);
        List<CuentaBancaria> cuentasEsperadas = List.of(cuenta1, cuenta2);
        
        when(repositorioCuentas.buscarPorTitular(identificacion)).thenReturn(cuentasEsperadas);

        // Act
        List<CuentaBancaria> cuentas = servicioCuenta.buscarCuentasPorTitular(identificacion);

        // Assert
        assertEquals(2, cuentas.size());
        verify(repositorioCuentas, times(1)).buscarPorTitular(identificacion);
    }

    @Test
    @DisplayName("Buscar cuentas por titular con identificación nula debe lanzar excepción")
    void buscarCuentasPorTitularConIdentificacionNulaDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.buscarCuentasPorTitular(null)
        );
        assertEquals("La identificación no puede ser nula o vacía", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorTitular(any());
    }

    @Test
    @DisplayName("Buscar cuentas por titular con identificación vacía debe lanzar excepción")
    void buscarCuentasPorTitularConIdentificacionVaciaDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.buscarCuentasPorTitular("  ")
        );
        assertEquals("La identificación no puede ser nula o vacía", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorTitular(any());
    }

    @Test
    @DisplayName("Desactivar cuenta existente")
    void desactivarCuentaExistente() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.of(cuenta));

        // Act
        servicioCuenta.desactivarCuenta(numeroCuenta);

        // Assert
        assertFalse(cuenta.isActiva());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Desactivar cuenta inexistente debe lanzar excepción")
    void desactivarCuentaInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.desactivarCuenta(numeroCuenta)
        );
        assertEquals("Cuenta no encontrada: " + numeroCuenta, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Desactivar cuenta con número nulo debe lanzar excepción")
    void desactivarCuentaConNumeroNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.desactivarCuenta(null)
        );
        assertEquals("El número de cuenta no puede ser nulo o vacío", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }

    @Test
    @DisplayName("Activar cuenta existente")
    void activarCuentaExistente() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        Titular titular = new Titular("12345678", "Juan", "Perez");
        CuentaBancaria cuenta = new CuentaBancaria(titular, TipoCuenta.AHORROS, 1000.0);
        cuenta.desactivar();
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.of(cuenta));

        // Act
        servicioCuenta.activarCuenta(numeroCuenta);

        // Assert
        assertTrue(cuenta.isActiva());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Activar cuenta inexistente debe lanzar excepción")
    void activarCuentaInexistenteDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);
        String numeroCuenta = "CTA-12345678";
        
        when(repositorioCuentas.buscarPorNumero(numeroCuenta)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.activarCuenta(numeroCuenta)
        );
        assertEquals("Cuenta no encontrada: " + numeroCuenta, exception.getMessage());
        verify(repositorioCuentas, times(1)).buscarPorNumero(numeroCuenta);
    }

    @Test
    @DisplayName("Activar cuenta con número nulo debe lanzar excepción")
    void activarCuentaConNumeroNuloDebeLanzarExcepcion() {
        // Arrange
        ServicioCuenta servicioCuenta = new ServicioCuenta(repositorioCuentas);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.activarCuenta(null)
        );
        assertEquals("El número de cuenta no puede ser nulo o vacío", exception.getMessage());
        verify(repositorioCuentas, never()).buscarPorNumero(any());
    }
}
