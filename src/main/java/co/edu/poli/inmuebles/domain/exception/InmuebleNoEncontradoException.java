package co.edu.poli.inmuebles.domain.exception;

/**
 * Se lanza cuando se consulta, actualiza o elimina un inmueble cuyo CHIP no existe.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class InmuebleNoEncontradoException extends DominioException {

    /**
     * Crea la excepción indicando el CHIP buscado.
     *
     * @param chip CHIP que no se encontró
     */
    public InmuebleNoEncontradoException(String chip) {
        super("No existe un inmueble con CHIP " + chip + ".");
    }
}
