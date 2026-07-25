package drl.desafio.servicio;

import drl.desafio.dominio.CuentaBancaria;
import drl.desafio.dominio.TipoCuenta;
import drl.desafio.dominio.Titular;
import drl.desafio.excepciones.ExcepcionMontoInvalido;
import drl.desafio.excepciones.ExcepcionSaldoInsuficiente;

import java.util.List;

public class ServicioCuenta {
    private final RepositorioCuentas repositorioCuentas;

    public ServicioCuenta(RepositorioCuentas repositorioCuentas) {
        if (repositorioCuentas == null) {
            throw new IllegalArgumentException("El repositorio de cuentas no puede ser nulo");
        }
        this.repositorioCuentas = repositorioCuentas;
    }

    public CuentaBancaria crearCuenta(Titular titular, TipoCuenta tipoCuenta, double saldoInicial) {
        if (titular == null) {
            throw new IllegalArgumentException("El titular no puede ser nulo");
        }
        if (tipoCuenta == null) {
            throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo");
        }
        if (saldoInicial < 0) {
            throw new ExcepcionMontoInvalido("El saldo inicial no puede ser negativo");
        }
        
        CuentaBancaria cuenta = new CuentaBancaria(titular, tipoCuenta, saldoInicial);
        repositorioCuentas.guardar(cuenta);
        return cuenta;
    }

    public void depositar(String numeroCuenta, double monto, String descripcion) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío");
        }
        
        CuentaBancaria cuenta = repositorioCuentas.buscarPorNumero(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
        
        cuenta.depositar(monto, descripcion);
    }

    public void retirar(String numeroCuenta, double monto, String descripcion) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío");
        }
        
        CuentaBancaria cuenta = repositorioCuentas.buscarPorNumero(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
        
        cuenta.retirar(monto, descripcion);
    }

    public void transferir(String cuentaOrigen, String cuentaDestino, double monto, String descripcion) {
        if (cuentaOrigen == null || cuentaOrigen.trim().isEmpty()) {
            throw new IllegalArgumentException("La cuenta origen no puede ser nula o vacía");
        }
        if (cuentaDestino == null || cuentaDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("La cuenta destino no puede ser nula o vacía");
        }
        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }
        
        CuentaBancaria origen = repositorioCuentas.buscarPorNumero(cuentaOrigen)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada: " + cuentaOrigen));
        
        CuentaBancaria destino = repositorioCuentas.buscarPorNumero(cuentaDestino)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada: " + cuentaDestino));
        
        origen.transferir(destino, monto, descripcion);
    }

    public double consultarSaldo(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío");
        }
        
        return repositorioCuentas.buscarPorNumero(numeroCuenta)
                .map(CuentaBancaria::getSaldo)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
    }

    public List<CuentaBancaria> buscarCuentasPorTitular(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación no puede ser nula o vacía");
        }
        
        return repositorioCuentas.buscarPorTitular(identificacion);
    }

    public void desactivarCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío");
        }
        
        CuentaBancaria cuenta = repositorioCuentas.buscarPorNumero(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
        
        cuenta.desactivar();
    }

    public void activarCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío");
        }
        
        CuentaBancaria cuenta = repositorioCuentas.buscarPorNumero(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
        
        cuenta.activar();
    }
}
