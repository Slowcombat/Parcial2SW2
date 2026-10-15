package co.edu.poli.inmuebles.application.service;

import co.edu.poli.inmuebles.application.port.in.ActualizarInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.in.ConsultarInmueblesUseCase;
import co.edu.poli.inmuebles.application.port.in.CrearInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.in.EliminarInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.out.InmuebleRepositoryPort;
import co.edu.poli.inmuebles.application.port.out.PropietarioRepositoryPort;
import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;
import co.edu.poli.inmuebles.domain.exception.InmuebleDuplicadoException;
import co.edu.poli.inmuebles.domain.exception.InmuebleNoEncontradoException;
import co.edu.poli.inmuebles.domain.exception.PropietarioNoEncontradoException;
import co.edu.poli.inmuebles.domain.model.Inmueble;

import java.util.List;
import java.util.Objects;

/**
 * Implementación de los casos de uso del CRUD de inmuebles.
 *
 * <p>Orquesta las reglas que involucran más de una entidad o la persistencia
 * (CHIP único, propietario existente, tipo inmutable). Las reglas propias de cada
 * entidad las valida el dominio al construir los objetos.</p>
 *
 * <p>Solo conoce los puertos de salida: no sabe que existe MySQL ni JavaFX.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class InmuebleService implements CrearInmuebleUseCase, ConsultarInmueblesUseCase,
        ActualizarInmuebleUseCase, EliminarInmuebleUseCase {

    private final InmuebleRepositoryPort inmuebleRepository;
    private final PropietarioRepositoryPort propietarioRepository;

    /**
     * Crea el servicio recibiendo sus dependencias por constructor
     * (inyección de dependencias manual).
     *
     * @param inmuebleRepository    puerto de persistencia de inmuebles
     * @param propietarioRepository puerto de lectura de propietarios
     */
    public InmuebleService(InmuebleRepositoryPort inmuebleRepository,
                           PropietarioRepositoryPort propietarioRepository) {
        this.inmuebleRepository = Objects.requireNonNull(inmuebleRepository);
        this.propietarioRepository = Objects.requireNonNull(propietarioRepository);
    }

    @Override
    public Inmueble crear(Inmueble inmueble) {
        requerido(inmueble);
        if (inmuebleRepository.existePorChip(inmueble.getChip())) {
            throw new InmuebleDuplicadoException(inmueble.getChip());
        }
        verificarPropietario(inmueble);
        inmuebleRepository.guardar(inmueble);
        return inmueble;
    }

    @Override
    public List<Inmueble> listarTodos() {
        return inmuebleRepository.listarTodos();
    }

    @Override
    public Inmueble buscarPorChip(String chip) {
        String normalizado = Inmueble.validarChip(chip);
        return inmuebleRepository.buscarPorChip(normalizado)
                .orElseThrow(() -> new InmuebleNoEncontradoException(normalizado));
    }

    @Override
    public List<Inmueble> listarPorPropietario(String identificacion) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new DatosInvalidosException("Seleccione un propietario.");
        }
        propietarioRepository.buscarPorIdentificacion(identificacion)
                .orElseThrow(() -> new PropietarioNoEncontradoException(identificacion));
        return inmuebleRepository.listarPorPropietario(identificacion);
    }

    @Override
    public Inmueble actualizar(Inmueble inmueble) {
        requerido(inmueble);
        Inmueble actual = inmuebleRepository.buscarPorChip(inmueble.getChip())
                .orElseThrow(() -> new InmuebleNoEncontradoException(inmueble.getChip()));
        if (!actual.getClass().equals(inmueble.getClass())) {
            throw new DatosInvalidosException("No se puede cambiar un " + actual.getTipo().toLowerCase()
                    + " a " + inmueble.getTipo().toLowerCase()
                    + ". Elimine el inmueble y regístrelo de nuevo.");
        }
        verificarPropietario(inmueble);
        inmuebleRepository.actualizar(inmueble);
        return inmueble;
    }

    @Override
    public void eliminar(String chip) {
        String normalizado = Inmueble.validarChip(chip);
        if (!inmuebleRepository.existePorChip(normalizado)) {
            throw new InmuebleNoEncontradoException(normalizado);
        }
        inmuebleRepository.eliminar(normalizado);
    }

    private static void requerido(Inmueble inmueble) {
        if (inmueble == null) {
            throw new DatosInvalidosException("No se recibió el inmueble.");
        }
    }

    private void verificarPropietario(Inmueble inmueble) {
        String id = inmueble.getPropietario().getIdentificacion();
        propietarioRepository.buscarPorIdentificacion(id)
                .orElseThrow(() -> new PropietarioNoEncontradoException(id));
    }
}
