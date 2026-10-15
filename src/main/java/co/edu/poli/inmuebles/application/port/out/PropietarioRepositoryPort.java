package co.edu.poli.inmuebles.application.port.out;

import co.edu.poli.inmuebles.domain.model.Propietario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: lectura de los propietarios predeterminados.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface PropietarioRepositoryPort {

    /**
     * Lista todos los propietarios, ordenados por nombre.
     *
     * @return lista de propietarios
     */
    List<Propietario> listarTodos();

    /**
     * Busca un propietario por su identificación.
     *
     * @param identificacion número de documento
     * @return el propietario, o vacío si no existe
     */
    Optional<Propietario> buscarPorIdentificacion(String identificacion);
}
