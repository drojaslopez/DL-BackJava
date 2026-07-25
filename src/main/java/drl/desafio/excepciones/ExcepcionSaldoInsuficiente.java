package drl.desafio.excepciones;

public class ExcepcionSaldoInsuficiente extends RuntimeException {
    public ExcepcionSaldoInsuficiente(String mensaje) {
        super(mensaje);
    }
}
