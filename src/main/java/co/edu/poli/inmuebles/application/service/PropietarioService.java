package co.edu.poli.inmuebles.application.service;

import co.edu.poli.inmuebles.application.port.in.ConsultarPropietariosUseCase;
import co.edu.poli.inmuebles.application.port.out.PropietarioRepositoryPort;
import co.edu.poli.inmuebles.domain.model.Propietario;

import java.util.List;
import java.util.Objects;

/**
 * Implementación del caso de uso de consulta de propietarios.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class PropietarioService implements ConsultarPropietariosUseCase {

    private final PropietarioRepositoryPort propietarioRepository;

    /**
     * Crea el servicio.
     *
     * @param propietarioRepository puerto de lectura de propietarios
     */
    public PropietarioService(PropietarioRepositoryPort propietarioRepository) {
        this.propietarioRepository = Objects.requireNonNull(propietarioRepository);
    }

    @Override
    public List<Propietario> listarPropietarios() {
        return propietarioRepository.listarTodos();
    }
}
