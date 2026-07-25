package drl.desafio.dominio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TitularTest {

    @Test
    @DisplayName("Crear titular con datos válidos")
    void crearTitularConDatosValidos() {
        // Arrange
        String identificacion = "12345678";
        String nombre = "Juan";
        String apellido = "Perez";

        // Act
        Titular titular = new Titular(identificacion, nombre, apellido);

        // Assert
        assertEquals(identificacion, titular.getIdentificacion());
        assertEquals(nombre, titular.getNombre());
        assertEquals(apellido, titular.getApellido());
        assertEquals("Juan Perez", titular.getNombreCompleto());
    }

    @Test
    @DisplayName("Crear titular con identificación nula debe lanzar excepción")
    void crearTitularConIdentificacionNulaDebeLanzarExcepcion() {
        // Arrange
        String identificacion = null;
        String nombre = "Juan";
        String apellido = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Titular(identificacion, nombre, apellido)
        );
        assertEquals("La identificación no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Crear titular con identificación vacía debe lanzar excepción")
    void crearTitularConIdentificacionVaciaDebeLanzarExcepcion() {
        // Arrange
        String identificacion = "   ";
        String nombre = "Juan";
        String apellido = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Titular(identificacion, nombre, apellido)
        );
        assertEquals("La identificación no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    @DisplayName("Crear titular con nombre nulo debe lanzar excepción")
    void crearTitularConNombreNuloDebeLanzarExcepcion() {
        // Arrange
        String identificacion = "12345678";
        String nombre = null;
        String apellido = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Titular(identificacion, nombre, apellido)
        );
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Crear titular con nombre vacío debe lanzar excepción")
    void crearTitularConNombreVacioDebeLanzarExcepcion() {
        // Arrange
        String identificacion = "12345678";
        String nombre = "";
        String apellido = "Perez";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Titular(identificacion, nombre, apellido)
        );
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Crear titular con apellido nulo debe lanzar excepción")
    void crearTitularConApellidoNuloDebeLanzarExcepcion() {
        // Arrange
        String identificacion = "12345678";
        String nombre = "Juan";
        String apellido = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Titular(identificacion, nombre, apellido)
        );
        assertEquals("El apellido no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Crear titular con apellido vacío debe lanzar excepción")
    void crearTitularConApellidoVacioDebeLanzarExcepcion() {
        // Arrange
        String identificacion = "12345678";
        String nombre = "Juan";
        String apellido = "  ";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Titular(identificacion, nombre, apellido)
        );
        assertEquals("El apellido no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Trim de espacios en blanco en campos")
    void trimDeEspaciosEnBlancoEnCampos() {
        // Arrange
        String identificacion = " 12345678 ";
        String nombre = " Juan ";
        String apellido = " Perez ";

        // Act
        Titular titular = new Titular(identificacion, nombre, apellido);

        // Assert
        assertEquals("12345678", titular.getIdentificacion());
        assertEquals("Juan", titular.getNombre());
        assertEquals("Perez", titular.getApellido());
    }

    @Test
    @DisplayName("Igualdad de titulares por identificación")
    void igualdadDeTitularesPorIdentificacion() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("12345678", "Maria", "Gomez");
        Titular titular3 = new Titular("87654321", "Juan", "Perez");

        // Act & Assert
        assertEquals(titular1, titular2);
        assertNotEquals(titular1, titular3);
    }

    @Test
    @DisplayName("HashCode consistente con equals")
    void hashCodeConsistenteConEquals() {
        // Arrange
        Titular titular1 = new Titular("12345678", "Juan", "Perez");
        Titular titular2 = new Titular("12345678", "Maria", "Gomez");

        // Act & Assert
        assertEquals(titular1.hashCode(), titular2.hashCode());
    }
}
