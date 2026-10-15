package co.edu.poli.inmuebles.domain;

import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;
import co.edu.poli.inmuebles.domain.model.Apartamento;
import co.edu.poli.inmuebles.domain.model.Casa;
import co.edu.poli.inmuebles.domain.model.Propietario;
import co.edu.poli.inmuebles.domain.model.Sotano;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InmuebleTest {

    private final Propietario ana = new Propietario("1001", "Ana Gómez", "ana@correo.com");
    private final LocalDate ayer = LocalDate.now().minusDays(1);

    @Test
    void normalizaElChipAMayusculas() {
        Apartamento a = new Apartamento(" aaa0001abcd ", ayer, "Calle 1", "Disponible", ana, 3);
        assertEquals("AAA0001ABCD", a.getChip());
    }

    @Test
    void rechazaChipConSimbolos() {
        assertThrows(DatosInvalidosException.class,
                () -> new Apartamento("AAA-01", ayer, "Calle 1", "Disponible", ana, 3));
    }

    @Test
    void rechazaFechaDeCompraFutura() {
        LocalDate manana = LocalDate.now().plusDays(1);
        assertThrows(DatosInvalidosException.class,
                () -> new Apartamento("AAA0001ABCD", manana, "Calle 1", "Disponible", ana, 3));
    }

    @Test
    void rechazaPisoCero() {
        assertThrows(DatosInvalidosException.class,
                () -> new Apartamento("AAA0001ABCD", ayer, "Calle 1", "Disponible", ana, 0));
    }

    @Test
    void casaSinSotanoDevuelveOptionalVacio() {
        Casa c = new Casa("AAA0002ABCD", ayer, "Carrera 2", "Ocupado", ana, 2, null);
        assertFalse(c.tieneSotano());
        assertTrue(c.getSotano().isEmpty());
    }

    @Test
    void casaConSotano() {
        Sotano s = new Sotano(40.5, "Parqueadero");
        Casa c = new Casa("AAA0002ABCD", ayer, "Carrera 2", "Ocupado", ana, 2, s);
        assertEquals(s, c.getSotano().orElseThrow());
        assertEquals("Casa", c.getTipo());
    }

    @Test
    void sotanoConAreaNoPositivaEsInvalido() {
        assertThrows(DatosInvalidosException.class, () -> new Sotano(0, "Bodega"));
    }

    @Test
    void estadoSeNormalizaAlValorOficial() {
        Apartamento a = new Apartamento("AAA0001ABCD", ayer, "Calle 1", " en REMODELACIÓN ", ana, 3);
        assertEquals("En remodelación", a.getEstado());
    }

    @Test
    void rechazaEstadoNoPermitido() {
        assertThrows(DatosInvalidosException.class,
                () -> new Apartamento("AAA0001ABCD", ayer, "Calle 1", "Vendido", ana, 3));
    }

    @Test
    void rechazaTipoDeUsoNoPermitido() {
        assertThrows(DatosInvalidosException.class, () -> new Sotano(20, "Piscina"));
    }

    @Test
    void correoInvalidoEnPropietario() {
        assertThrows(DatosInvalidosException.class, () -> new Propietario("1", "X", "sin-arroba"));
    }

    @Test
    void dosInmueblesConElMismoChipSonIguales() {
        Apartamento a1 = new Apartamento("AAA0001ABCD", ayer, "Calle 1", "Disponible", ana, 3);
        Apartamento a2 = new Apartamento("aaa0001abcd", ayer, "Otra", "Arrendado", ana, 9);
        assertEquals(a1, a2);
    }
}
