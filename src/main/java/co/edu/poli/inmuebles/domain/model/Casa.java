package co.edu.poli.inmuebles.domain.model;

import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Inmueble independiente de uno o varios pisos, que puede tener un sótano.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class Casa extends Inmueble {

    private int cantidadPisos;
    private Sotano sotano;

    /**
     * Crea una casa.
     *
     * @param chip          código predial
     * @param fechaCompra   fecha de compra (no futura)
     * @param direccion     dirección
     * @param estado        estado actual
     * @param propietario   dueño
     * @param cantidadPisos número de pisos (1 o más)
     * @param sotano        sótano de la casa, o {@code null} si no tiene
     * @throws DatosInvalidosException si algún dato no es válido
     */
    public Casa(String chip, LocalDate fechaCompra, String direccion, String estado,
                Propietario propietario, int cantidadPisos, Sotano sotano) {
        super(chip, fechaCompra, direccion, estado, propietario);
        setCantidadPisos(cantidadPisos);
        this.sotano = sotano;
    }

    /**
     * Devuelve la cantidad de pisos de la casa.
     *
     * @return cantidad de pisos de la casa
     */
    public int getCantidadPisos() {
        return cantidadPisos;
    }

    /**
     * Cambia la cantidad de pisos.
     *
     * @param cantidadPisos número de pisos, entre 1 y 10
     * @throws DatosInvalidosException si está fuera de rango
     */
    public final void setCantidadPisos(int cantidadPisos) {
        if (cantidadPisos < 1 || cantidadPisos > 10) {
            throw new DatosInvalidosException("La cantidad de pisos debe estar entre 1 y 10.");
        }
        this.cantidadPisos = cantidadPisos;
    }

    /**
     * El sótano es opcional, por eso se devuelve envuelto en {@link Optional}.
     *
     * @return el sótano, o vacío si la casa no tiene
     */
    public Optional<Sotano> getSotano() {
        return Optional.ofNullable(sotano);
    }

    /**
     * Indica si la casa tiene sótano.
     *
     * @return {@code true} si tiene sótano
     */
    public boolean tieneSotano() {
        return sotano != null;
    }

    /**
     * Asigna, reemplaza o quita el sótano.
     *
     * @param sotano nuevo sótano, o {@code null} para quitarlo
     */
    public void setSotano(Sotano sotano) {
        this.sotano = sotano;
    }

    @Override
    public String getTipo() {
        return "Casa";
    }

    @Override
    public String getDetalle() {
        String pisos = cantidadPisos + (cantidadPisos == 1 ? " piso" : " pisos");
        return tieneSotano() ? pisos + ", sótano " + sotano : pisos + ", sin sótano";
    }
}
