package co.edu.poli.inmuebles.application.port.out;

import co.edu.poli.inmuebles.domain.model.Inmueble;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: lo que la aplicación necesita para guardar y recuperar
 * inmuebles, sin saber qué tecnología de persistencia hay detrás.
 *
 * <p>La implementación concreta (por ejemplo MySQL con JDBC) vive en la capa de
 * infraestructura como un adaptador.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface InmuebleRepositoryPort {

    /**
     * Inserta un inmueble nuevo con sus datos específicos.
     *
     * @param inmueble inmueble a guardar
     */
    void guardar(Inmueble inmueble);

    /**
     * Actualiza un inmueble existente (identificado por su CHIP).
     *
     * @param inmueble inmueble con los datos nuevos
     */
    void actualizar(Inmueble inmueble);

    /**
     * Busca un inmueble por CHIP, reconstruyendo el subtipo correcto.
     *
     * @param chip CHIP normalizado
     * @return el inmueble, o vacío si no existe
     */
    Optional<Inmueble> buscarPorChip(String chip);

    /**
     * Indica si ya existe un inmueble con ese CHIP.
     *
     * @param chip CHIP normalizado
     * @return {@code true} si existe
     */
    boolean existePorChip(String chip);

    /**
     * Lista todos los inmuebles, ordenados por CHIP.
     *
     * @return lista de inmuebles
     */
    List<Inmueble> listarTodos();

    /**
     * Lista los inmuebles de un propietario, ordenados por CHIP.
     *
     * @param identificacion identificación del propietario
     * @return lista de inmuebles del propietario
     */
    List<Inmueble> listarPorPropietario(String identificacion);

    /**
     * Elimina un inmueble y sus datos dependientes.
     *
     * @param chip CHIP normalizado
     */
    void eliminar(String chip);
}
