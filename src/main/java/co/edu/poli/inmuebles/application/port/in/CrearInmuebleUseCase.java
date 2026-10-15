package co.edu.poli.inmuebles.application.port.in;

import co.edu.poli.inmuebles.domain.exception.DominioException;
import co.edu.poli.inmuebles.domain.model.Inmueble;

/**
 * Puerto de entrada: caso de uso <b>Crear inmueble</b> (C del CRUD).
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface CrearInmuebleUseCase {

    /**
     * Registra un inmueble nuevo.
     *
     * @param inmueble apartamento o casa a registrar
     * @return el inmueble registrado
     * @throws DominioException si el CHIP ya existe o el propietario no está registrado
     */
    Inmueble crear(Inmueble inmueble);
}
