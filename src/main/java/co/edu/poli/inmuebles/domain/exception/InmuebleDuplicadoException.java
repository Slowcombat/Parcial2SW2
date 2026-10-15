package co.edu.poli.inmuebles.domain.exception;

/**
 * Se lanza al intentar registrar un inmueble con un CHIP que ya existe.
 * El CHIP es la identidad del inmueble, por eso no puede repetirse.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class InmuebleDuplicadoException extends DominioException {

    /**
     * Crea la excepción indicando el CHIP repetido.
     *
     * @param chip CHIP que ya está registrado
     */
    public InmuebleDuplicadoException(String chip) {
        super("Ya existe un inmueble con CHIP " + chip + ".");
    }
}
