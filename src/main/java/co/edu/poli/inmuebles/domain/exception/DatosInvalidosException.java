package co.edu.poli.inmuebles.domain.exception;

/**
 * Se lanza cuando un dato de una entidad del dominio no cumple sus reglas
 * (campo obligatorio vacío, número fuera de rango, fecha futura, correo mal formado, etc.).
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class DatosInvalidosException extends DominioException {

    /**
     * Crea la excepción con la descripción del dato inválido.
     *
     * @param mensaje explicación de por qué el dato no es válido
     */
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
