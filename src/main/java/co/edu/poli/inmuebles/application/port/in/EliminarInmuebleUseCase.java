package co.edu.poli.inmuebles.application.port.in;

import co.edu.poli.inmuebles.domain.exception.DominioException;

/**
 * Puerto de entrada: caso de uso <b>Eliminar inmueble</b> (D del CRUD).
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface EliminarInmuebleUseCase {

    /**
     * Elimina un inmueble y sus datos dependientes (piso, pisos y sótano).
     *
     * @param chip CHIP del inmueble a eliminar
     * @throws DominioException si el inmueble no existe
     */
    void eliminar(String chip);
}
