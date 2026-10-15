package co.edu.poli.inmuebles.domain.exception;

/**
 * Se lanza cuando un inmueble referencia un propietario que no está registrado.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class PropietarioNoEncontradoException extends DominioException {

    /**
     * Crea la excepción indicando la identificación buscada.
     *
     * @param identificacion identificación del propietario que no existe
     */
    public PropietarioNoEncontradoException(String identificacion) {
        super("No existe un propietario con identificación " + identificacion + ".");
    }
}
