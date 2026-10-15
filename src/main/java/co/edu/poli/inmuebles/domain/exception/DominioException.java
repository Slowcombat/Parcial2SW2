package co.edu.poli.inmuebles.domain.exception;

/**
 * Excepción base de las reglas de negocio del dominio.
 *
 * <p>Es una excepción no verificada para que las capas externas (adaptadores)
 * decidan cómo presentarla al usuario sin acoplar el dominio a la interfaz.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class DominioException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje legible para el usuario.
     *
     * @param mensaje descripción de la regla incumplida
     */
    public DominioException(String mensaje) {
        super(mensaje);
    }
}
