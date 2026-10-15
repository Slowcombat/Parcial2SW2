package co.edu.poli.inmuebles.application;

import co.edu.poli.inmuebles.application.port.out.InmuebleRepositoryPort;
import co.edu.poli.inmuebles.application.port.out.PropietarioRepositoryPort;
import co.edu.poli.inmuebles.application.service.InmuebleService;
import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;
import co.edu.poli.inmuebles.domain.exception.InmuebleDuplicadoException;
import co.edu.poli.inmuebles.domain.exception.InmuebleNoEncontradoException;
import co.edu.poli.inmuebles.domain.exception.PropietarioNoEncontradoException;
import co.edu.poli.inmuebles.domain.model.Apartamento;
import co.edu.poli.inmuebles.domain.model.Casa;
import co.edu.poli.inmuebles.domain.model.Inmueble;
import co.edu.poli.inmuebles.domain.model.Propietario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba los casos de uso sin base de datos: los puertos de salida se reemplazan
 * por adaptadores en memoria. Esto es posible gracias a la arquitectura hexagonal.
 */
class InmuebleServiceTest {

    private final Propietario ana = new Propietario("1001", "Ana Gómez", "ana@correo.com");
    private final Propietario luis = new Propietario("1002", "Luis Pérez", "luis@correo.com");
    private final LocalDate fecha = LocalDate.of(2020, 5, 10);

    private InmuebleService servicio;

    @BeforeEach
    void preparar() {
        servicio = new InmuebleService(new InmueblesEnMemoria(), new PropietariosEnMemoria(List.of(ana, luis)));
    }

    private Apartamento apto(String chip, Propietario p) {
        return new Apartamento(chip, fecha, "Calle 10 # 5-20", "Disponible", p, 4);
    }

    @Test
    void creaYConsultaPorChip() {
        servicio.crear(apto("AAA0001ABCD", ana));
        assertEquals("Apartamento", servicio.buscarPorChip("aaa0001abcd").getTipo());
    }

    @Test
    void noPermiteChipDuplicado() {
        servicio.crear(apto("AAA0001ABCD", ana));
        assertThrows(InmuebleDuplicadoException.class, () -> servicio.crear(apto("AAA0001ABCD", luis)));
    }

    @Test
    void noPermitePropietarioInexistente() {
        Propietario fantasma = new Propietario("9999", "Nadie", "nadie@correo.com");
        assertThrows(PropietarioNoEncontradoException.class, () -> servicio.crear(apto("AAA0001ABCD", fantasma)));
    }

    @Test
    void unPropietarioPuedeTenerVariosInmuebles() {
        servicio.crear(apto("AAA0001ABCD", ana));
        servicio.crear(new Casa("AAA0002ABCD", fecha, "Cra 7", "Ocupado", ana, 2, null));
        servicio.crear(apto("AAA0003ABCD", luis));
        assertEquals(2, servicio.listarPorPropietario("1001").size());
        assertEquals(3, servicio.listarTodos().size());
    }

    @Test
    void actualizaDatos() {
        servicio.crear(apto("AAA0001ABCD", ana));
        Apartamento cambiado = new Apartamento("AAA0001ABCD", fecha, "Nueva dirección",
                "Arrendado", luis, 8);
        servicio.actualizar(cambiado);
        Apartamento leido = (Apartamento) servicio.buscarPorChip("AAA0001ABCD");
        assertEquals("Nueva dirección", leido.getDireccion());
        assertEquals(8, leido.getNumeroPiso());
        assertEquals(luis, leido.getPropietario());
    }

    @Test
    void noPermiteCambiarElTipo() {
        servicio.crear(apto("AAA0001ABCD", ana));
        Casa comoCasa = new Casa("AAA0001ABCD", fecha, "x", "Disponible", ana, 1, null);
        assertThrows(DatosInvalidosException.class, () -> servicio.actualizar(comoCasa));
    }

    @Test
    void eliminaYLuegoNoLoEncuentra() {
        servicio.crear(apto("AAA0001ABCD", ana));
        servicio.eliminar("AAA0001ABCD");
        assertThrows(InmuebleNoEncontradoException.class, () -> servicio.buscarPorChip("AAA0001ABCD"));
        assertThrows(InmuebleNoEncontradoException.class, () -> servicio.eliminar("AAA0001ABCD"));
    }

    /** Adaptador de salida falso, solo para pruebas. */
    private static class InmueblesEnMemoria implements InmuebleRepositoryPort {
        private final Map<String, Inmueble> datos = new TreeMap<>();

        public void guardar(Inmueble i) { datos.put(i.getChip(), i); }
        public void actualizar(Inmueble i) { datos.put(i.getChip(), i); }
        public Optional<Inmueble> buscarPorChip(String chip) { return Optional.ofNullable(datos.get(chip)); }
        public boolean existePorChip(String chip) { return datos.containsKey(chip); }
        public List<Inmueble> listarTodos() { return new ArrayList<>(datos.values()); }
        public void eliminar(String chip) { datos.remove(chip); }

        public List<Inmueble> listarPorPropietario(String id) {
            return datos.values().stream()
                    .filter(i -> i.getPropietario().getIdentificacion().equals(id))
                    .toList();
        }
    }

    /** Adaptador de salida falso, solo para pruebas. */
    private record PropietariosEnMemoria(List<Propietario> lista) implements PropietarioRepositoryPort {
        public List<Propietario> listarTodos() { return lista; }

        public Optional<Propietario> buscarPorIdentificacion(String id) {
            return lista.stream().filter(p -> p.getIdentificacion().equals(id)).findFirst();
        }
    }
}
