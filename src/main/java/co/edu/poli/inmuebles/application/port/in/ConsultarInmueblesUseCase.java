package co.edu.poli.inmuebles.application.port.in;

import co.edu.poli.inmuebles.domain.exception.DominioException;
import co.edu.poli.inmuebles.domain.model.Inmueble;

import java.util.List;

/**
 * Puerto de entrada: caso de uso <b>Consultar inmuebles</b> (R del CRUD).
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface ConsultarInmueblesUseCase {

    /**
     * Lista todos los inmuebles registrados, ordenados por CHIP.
     *
     * @return lista de inmuebles (vacía si no hay ninguno)
     */
    List<Inmueble> listarTodos();

    /**
     * Busca un inmueble por su CHIP.
     *
     * @param chip CHIP a buscar
     * @return el inmueble encontrado
     * @throws DominioException si el CHIP no es válido o no existe
     */
    Inmueble buscarPorChip(String chip);

    /**
     * Lista los inmuebles de un propietario (un propietario puede tener varios).
     *
     * @param identificacion identificación del propietario
     * @return inmuebles del propietario, ordenados por CHIP
     * @throws DominioException si el propietario no existe
     */
    List<Inmueble> listarPorPropietario(String identificacion);
}
