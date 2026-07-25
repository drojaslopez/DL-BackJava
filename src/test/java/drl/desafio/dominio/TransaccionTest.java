package drl.desafio.dominio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TransaccionTest {

    @Test
    @DisplayName("Crear transacción con datos válidos")
    void crearTransaccionConDatosValidos() {
        // Arrange
        String id = "TXN-12345678";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 1000.0;
        String descripcion = "Depósito inicial";

        // Act
        Transaccion transaccion = new Transaccion(id, tipo, monto, descripcion);

        // Assert
        assertEquals(id, transaccion.getId());
        assertEquals(tipo, transaccion.getTipo());
        assertEquals(monto, transaccion.getMonto());
        assertEquals(descripcion, transaccion.getDescripcion());
        assertNotNull(transaccion.getFecha());
    }

    @Test
    @DisplayName("Crear transacción con ID nulo debe lanzar excepción")
    void crearTransaccionConIdNuloDebeLanzarExcepcion() {
        // Arrange
        String id = null;
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 1000.0;
        String descripcion = "Depósito inicial";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("El ID de transacción no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Crear transacción con ID vacío debe lanzar excepción")
    void crearTransaccionConIdVacioDebeLanzarExcepcion() {
        // Arrange
        String id = "  ";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 1000.0;
        String descripcion = "Depósito inicial";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("El ID de transacción no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Crear transacción con tipo nulo debe lanzar excepción")
    void crearTransaccionConTipoNuloDebeLanzarExcepcion() {
        // Arrange
        String id = "TXN-12345678";
        TipoTransaccion tipo = null;
        double monto = 1000.0;
        String descripcion = "Depósito inicial";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("El tipo de transacción no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Crear transacción con monto cero debe lanzar excepción")
    void crearTransaccionConMontoCeroDebeLanzarExcepcion() {
        // Arrange
        String id = "TXN-12345678";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 0.0;
        String descripcion = "Depósito inicial";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("El monto debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("Crear transacción con monto negativo debe lanzar excepción")
    void crearTransaccionConMontoNegativoDebeLanzarExcepcion() {
        // Arrange
        String id = "TXN-12345678";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = -100.0;
        String descripcion = "Depósito inicial";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("El monto debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("Crear transacción con descripción nula debe lanzar excepción")
    void crearTransaccionConDescripcionNulaDebeLanzarExcepcion() {
        // Arrange
        String id = "TXN-12345678";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 1000.0;
        String descripcion = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("La descripción no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Crear transacción con descripción vacía debe lanzar excepción")
    void crearTransaccionConDescripcionVaciaDebeLanzarExcepcion() {
        // Arrange
        String id = "TXN-12345678";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 1000.0;
        String descripcion = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaccion(id, tipo, monto, descripcion)
        );
        assertEquals("La descripción no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Trim de espacios en blanco en campos")
    void trimDeEspaciosEnBlancoEnCampos() {
        // Arrange
        String id = " TXN-12345678 ";
        TipoTransaccion tipo = TipoTransaccion.DEPOSITO;
        double monto = 1000.0;
        String descripcion = " Depósito inicial ";

        // Act
        Transaccion transaccion = new Transaccion(id, tipo, monto, descripcion);

        // Assert
        assertEquals("TXN-12345678", transaccion.getId());
        assertEquals("Depósito inicial", transaccion.getDescripcion());
    }

    @Test
    @DisplayName("Igualdad de transacciones por ID")
    void igualdadDeTransaccionesPorId() {
        // Arrange
        Transaccion transaccion1 = new Transaccion("TXN-12345678", TipoTransaccion.DEPOSITO, 1000.0, "Depósito");
        Transaccion transaccion2 = new Transaccion("TXN-12345678", TipoTransaccion.RETIRO, 500.0, "Retiro");
        Transaccion transaccion3 = new Transaccion("TXN-87654321", TipoTransaccion.DEPOSITO, 1000.0, "Depósito");

        // Act & Assert
        assertEquals(transaccion1, transaccion2);
        assertNotEquals(transaccion1, transaccion3);
    }

    @Test
    @DisplayName("Equals con objeto nulo")
    void equalsConObjetoNulo() {
        // Arrange
        Transaccion transaccion = new Transaccion("TXN-12345678", TipoTransaccion.DEPOSITO, 1000.0, "Depósito");

        // Act & Assert
        assertNotEquals(transaccion, null);
    }

    @Test
    @DisplayName("Equals con objeto de diferente clase")
    void equalsConObjetoDeDiferenteClase() {
        // Arrange
        Transaccion transaccion = new Transaccion("TXN-12345678", TipoTransaccion.DEPOSITO, 1000.0, "Depósito");
        String otroObjeto = "texto";

        // Act & Assert
        assertNotEquals(transaccion, otroObjeto);
    }

    @Test
    @DisplayName("HashCode consistente con equals")
    void hashCodeConsistenteConEquals() {
        // Arrange
        Transaccion transaccion1 = new Transaccion("TXN-12345678", TipoTransaccion.DEPOSITO, 1000.0, "Depósito");
        Transaccion transaccion2 = new Transaccion("TXN-12345678", TipoTransaccion.RETIRO, 500.0, "Retiro");

        // Act & Assert
        assertEquals(transaccion1.hashCode(), transaccion2.hashCode());
    }

    @Test
    @DisplayName("ToString de transacción")
    void toStringDeTransaccion() {
        // Arrange
        Transaccion transaccion = new Transaccion("TXN-12345678", TipoTransaccion.DEPOSITO, 1000.0, "Depósito");

        // Act
        String toString = transaccion.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("Transaccion"));
        assertTrue(toString.contains("TXN-12345678"));
        assertTrue(toString.contains("DEPOSITO"));
        assertTrue(toString.contains("1000.0"));
        assertTrue(toString.contains("Depósito"));
    }
}
