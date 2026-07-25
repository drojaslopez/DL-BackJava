package drl.desafio.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaccion {
    private final String id;
    private final TipoTransaccion tipo;
    private final double monto;
    private final LocalDateTime fecha;
    private final String descripcion;

    public Transaccion(String id, TipoTransaccion tipo, double monto, String descripcion) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de transacción no puede ser nulo o vacío");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de transacción no puede ser nulo");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        this.id = id.trim();
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.descripcion = descripcion.trim();
    }

    public String getId() {
        return id;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaccion that = (Transaccion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
