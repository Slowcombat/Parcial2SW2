package co.edu.poli.inmuebles.domain.model;

import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;

import java.util.List;
import java.util.Objects;

/**
 * Sótano opcional de una {@link Casa}.
 *
 * <p>Es un objeto de valor: no tiene identidad propia y solo existe como parte
 * de una casa (composición en el modelo UML). Es inmutable; para cambiarlo se
 * reemplaza por otro.</p>
 *
 * <p>Reglas: el área debe ser mayor que cero y el tipo de uso debe ser uno de
 * {@link #TIPOS_USO_PERMITIDOS}.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public final class Sotano {

    /** Valores válidos para el atributo {@code tipoUso}. */
    public static final List<String> TIPOS_USO_PERMITIDOS =
            List.of("Parqueadero", "Bodega", "Habitacional", "Servicios");

    private final double areaM2;
    private final String tipoUso;

    /**
     * Crea un sótano.
     *
     * @param areaM2  área en metros cuadrados, debe ser mayor que cero
     * @param tipoUso uso que se le da al sótano (uno de {@link #TIPOS_USO_PERMITIDOS});
     *                no importan mayúsculas ni espacios extremos
     * @throws DatosInvalidosException si el área no es positiva o el tipo de uso no es válido
     */
    public Sotano(double areaM2, String tipoUso) {
        if (Double.isNaN(areaM2) || areaM2 <= 0) {
            throw new DatosInvalidosException("El área del sótano debe ser mayor que 0 m².");
        }
        if (tipoUso == null || tipoUso.isBlank()) {
            throw new DatosInvalidosException("El tipo de uso del sótano es obligatorio.");
        }
        this.areaM2 = areaM2;
        this.tipoUso = TIPOS_USO_PERMITIDOS.stream()
                .filter(t -> t.equalsIgnoreCase(tipoUso.trim()))
                .findFirst()
                .orElseThrow(() -> new DatosInvalidosException("El tipo de uso '" + tipoUso.trim()
                        + "' no es válido. Valores permitidos: "
                        + String.join(", ", TIPOS_USO_PERMITIDOS) + "."));
    }

    /**
     * Devuelve el área del sótano en metros cuadrados.
     *
     * @return área del sótano en metros cuadrados
     */
    public double getAreaM2() {
        return areaM2;
    }

    /**
     * Devuelve el uso del sótano.
     *
     * @return uso del sótano
     */
    public String getTipoUso() {
        return tipoUso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sotano otro)) {
            return false;
        }
        return Double.compare(areaM2, otro.areaM2) == 0 && tipoUso.equals(otro.tipoUso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areaM2, tipoUso);
    }

    @Override
    public String toString() {
        return String.format("%.1f m² (%s)", areaM2, tipoUso);
    }
}
