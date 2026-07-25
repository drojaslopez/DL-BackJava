package drl.desafio.dominio;

import drl.desafio.excepciones.ExcepcionMontoInvalido;
import drl.desafio.excepciones.ExcepcionSaldoInsuficiente;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CuentaBancaria {
    private final String numeroCuenta;
    private final Titular titular;
    private final TipoCuenta tipoCuenta;
    private double saldo;
    private final List<Transaccion> historialTransacciones;
    private boolean activa;

    public CuentaBancaria(Titular titular, TipoCuenta tipoCuenta, double saldoInicial) {
        if (titular == null) {
            throw new IllegalArgumentException("El titular no puede ser nulo");
        }
        if (tipoCuenta == null) {
            throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.numeroCuenta = generarNumeroCuenta();
        this.titular = titular;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldoInicial;
        this.historialTransacciones = new ArrayList<>();
        this.activa = true;
    }

    private String generarNumeroCuenta() {
        return "CTA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public Titular getTitular() {
        return titular;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public List<Transaccion> getHistorialTransacciones() {
        return new ArrayList<>(historialTransacciones);
    }

    public void depositar(double monto, String descripcion) {
        if (!activa) {
            throw new IllegalStateException("La cuenta no está activa");
        }
        if (monto <= 0) {
            throw new ExcepcionMontoInvalido("El monto a depositar debe ser mayor a cero");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        
        saldo += monto;
        String idTransaccion = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaccion transaccion = new Transaccion(idTransaccion, TipoTransaccion.DEPOSITO, monto, descripcion);
        historialTransacciones.add(transaccion);
    }

    public void retirar(double monto, String descripcion) {
        if (!activa) {
            throw new IllegalStateException("La cuenta no está activa");
        }
        if (monto <= 0) {
            throw new ExcepcionMontoInvalido("El monto a retirar debe ser mayor a cero");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        if (saldo < monto) {
            throw new ExcepcionSaldoInsuficiente("Saldo insuficiente. Saldo actual: " + saldo + ", Monto requerido: " + monto);
        }
        
        saldo -= monto;
        String idTransaccion = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaccion transaccion = new Transaccion(idTransaccion, TipoTransaccion.RETIRO, monto, descripcion);
        historialTransacciones.add(transaccion);
    }

    public void transferir(CuentaBancaria cuentaDestino, double monto, String descripcion) {
        if (!activa) {
            throw new IllegalStateException("La cuenta origen no está activa");
        }
        if (cuentaDestino == null) {
            throw new IllegalArgumentException("La cuenta destino no puede ser nula");
        }
        if (!cuentaDestino.isActiva()) {
            throw new IllegalStateException("La cuenta destino no está activa");
        }
        if (this.numeroCuenta.equals(cuentaDestino.getNumeroCuenta())) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }
        
        this.retirar(monto, "Transferencia a " + cuentaDestino.getNumeroCuenta() + ": " + descripcion);
        cuentaDestino.depositar(monto, "Transferencia desde " + this.numeroCuenta + ": " + descripcion);
    }

    public void desactivar() {
        this.activa = false;
    }

    public void activar() {
        this.activa = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuentaBancaria that = (CuentaBancaria) o;
        return Objects.equals(numeroCuenta, that.numeroCuenta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroCuenta);
    }

    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", titular=" + titular.getNombreCompleto() +
                ", tipoCuenta=" + tipoCuenta +
                ", saldo=" + saldo +
                ", activa=" + activa +
                '}';
    }
}
