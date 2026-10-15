package co.edu.poli.inmuebles.application.port.in;

import co.edu.poli.inmuebles.domain.model.Propietario;

import java.util.List;

/**
 * Puerto de entrada: consulta de los propietarios predeterminados, que la
 * interfaz necesita para asignar un dueño a cada inmueble. No hay CRUD de
 * propietarios: solo lectura.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public interface ConsultarPropietariosUseCase {

    /**
     * Lista los propietarios registrados, ordenados por nombre.
     *
     * @return lista de propietarios
     */
    List<Propietario> listarPropietarios();
}
