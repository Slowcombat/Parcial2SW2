package co.edu.poli.inmuebles.domain.model;

import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;

import java.time.LocalDate;

/**
 * Inmueble ubicado en un piso de un edificio.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class Apartamento extends Inmueble {

    private int numeroPiso;

    /**
     * Crea un apartamento.
     *
     * @param chip        código predial
     * @param fechaCompra fecha de compra (no futura)
     * @param direccion   dirección
     * @param estado      estado actual
     * @param propietario dueño
     * @param numeroPiso  piso en el que está el apartamento (1 o mayor)
     * @throws DatosInvalidosException si algún dato no es válido
     */
    public Apartamento(String chip, LocalDate fechaCompra, String direccion,
                       String estado, Propietario propietario, int numeroPiso) {
        super(chip, fechaCompra, direccion, estado, propietario);
        setNumeroPiso(numeroPiso);
    }

    /**
     * Devuelve el piso en el que está el apartamento.
     *
     * @return piso en el que está el apartamento
     */
    public int getNumeroPiso() {
        return numeroPiso;
    }

    /**
     * Cambia el número de piso.
     *
     * @param numeroPiso piso, entre 1 y 200
     * @throws DatosInvalidosException si está fuera de rango
     */
    public final void setNumeroPiso(int numeroPiso) {
        if (numeroPiso < 1 || numeroPiso > 200) {
            throw new DatosInvalidosException("El número de piso debe estar entre 1 y 200.");
        }
        this.numeroPiso = numeroPiso;
    }

    @Override
    public String getTipo() {
        return "Apartamento";
    }

    @Override
    public String getDetalle() {
        return "Piso " + numeroPiso;
    }
}
