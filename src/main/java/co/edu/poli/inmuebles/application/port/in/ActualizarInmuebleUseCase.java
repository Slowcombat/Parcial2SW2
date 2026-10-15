package co.edu.poli.inmuebles.application.port.in;

import co.edu.poli.inmuebles.domain.exception.DominioException;
import co.edu.poli.inmuebles.domain.model.Inmueble;

/**
 * Puerto de entrada: caso de uso <b>Actualizar inmueble</b> (U del CRUD).
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface ActualizarInmuebleUseCase {

    /**
     * Reemplaza los datos de un inmueble existente. El CHIP identifica cuál
     * inmueble se actualiza y no cambia; el tipo (apartamento/casa) tampoco.
     *
     * @param inmueble inmueble con los datos nuevos
     * @return el inmueble actualizado
     * @throws DominioException si el inmueble o el propietario no existen,
     *                          o si se intenta cambiar el tipo
     */
    Inmueble actualizar(Inmueble inmueble);
}
