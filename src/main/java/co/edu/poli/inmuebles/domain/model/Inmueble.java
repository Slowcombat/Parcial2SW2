package co.edu.poli.inmuebles.domain.model;

import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Bien raíz registrado en la aplicación. Es la raíz del modelo de dominio.
 *
 * <p>Es una clase abstracta: todo inmueble es concretamente un {@link Apartamento}
 * o una {@link Casa}. Su identidad es el CHIP (Código Homologado de Identificación
 * Predial), por eso dos inmuebles con el mismo CHIP se consideran el mismo.</p>
 *
 * <p>Reglas de negocio que protege esta clase:</p>
 * <ul>
 *   <li>El CHIP es obligatorio, alfanumérico, de 5 a 20 caracteres, y se guarda en mayúsculas.</li>
 *   <li>La fecha de compra es obligatoria y no puede ser futura.</li>
 *   <li>La dirección es obligatoria (máximo 150 caracteres).</li>
 *   <li>El estado es obligatorio y debe ser uno de {@link #ESTADOS_PERMITIDOS}.</li>
 *   <li>El propietario es obligatorio.</li>
 * </ul>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public abstract class Inmueble {

    /** Valores válidos para el atributo {@code estado}. */
    public static final List<String> ESTADOS_PERMITIDOS =
            List.of("Disponible", "Arrendado", "Ocupado", "En remodelación");

    private static final Pattern FORMATO_CHIP = Pattern.compile("^[A-Z0-9]{5,20}$");

    private final String chip;
    private LocalDate fechaCompra;
    private String direccion;
    private String estado;
    private Propietario propietario;

    /**
     * Constructor usado por las subclases; valida todos los datos comunes.
     *
     * @param chip        código predial del inmueble
     * @param fechaCompra fecha en que se compró
     * @param direccion   dirección del inmueble
     * @param estado      estado actual (uno de {@link #ESTADOS_PERMITIDOS})
     * @param propietario dueño del inmueble
     * @throws DatosInvalidosException si algún dato no cumple las reglas
     */
    protected Inmueble(String chip, LocalDate fechaCompra, String direccion,
                       String estado, Propietario propietario) {
        this.chip = validarChip(chip);
        setFechaCompra(fechaCompra);
        setDireccion(direccion);
        setEstado(estado);
        setPropietario(propietario);
    }

    /**
     * Normaliza y valida un CHIP. Es pública para que los casos de uso puedan
     * validar un CHIP de búsqueda antes de consultar el repositorio.
     *
     * @param chip valor ingresado
     * @return CHIP sin espacios y en mayúsculas
     * @throws DatosInvalidosException si el CHIP está vacío o no cumple el formato
     */
    public static String validarChip(String chip) {
        if (chip == null || chip.isBlank()) {
            throw new DatosInvalidosException("El CHIP es obligatorio.");
        }
        String normalizado = chip.trim().toUpperCase();
        if (!FORMATO_CHIP.matcher(normalizado).matches()) {
            throw new DatosInvalidosException(
                    "El CHIP debe tener entre 5 y 20 letras o números, sin espacios ni símbolos.");
        }
        return normalizado;
    }

    /**
     * Nombre del tipo concreto de inmueble ("Apartamento" o "Casa").
     *
     * @return tipo del inmueble
     */
    public abstract String getTipo();

    /**
     * Resumen de los datos propios del subtipo, para mostrarlo en listados.
     *
     * @return descripción corta de los atributos específicos
     */
    public abstract String getDetalle();

    /**
     * Devuelve el CHIP del inmueble (identificador inmutable).
     *
     * @return CHIP del inmueble (identificador inmutable)
     */
    public String getChip() {
        return chip;
    }

    /**
     * Devuelve la fecha de compra.
     *
     * @return fecha de compra
     */
    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    /**
     * Cambia la fecha de compra.
     *
     * @param fechaCompra nueva fecha; no puede ser nula ni futura
     * @throws DatosInvalidosException si la fecha es nula o posterior a hoy
     */
    public final void setFechaCompra(LocalDate fechaCompra) {
        if (fechaCompra == null) {
            throw new DatosInvalidosException("La fecha de compra es obligatoria.");
        }
        if (fechaCompra.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha de compra no puede ser una fecha futura.");
        }
        this.fechaCompra = fechaCompra;
    }

    /**
     * Devuelve la dirección del inmueble.
     *
     * @return dirección del inmueble
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Cambia la dirección.
     *
     * @param direccion nueva dirección; obligatoria y de máximo 150 caracteres
     * @throws DatosInvalidosException si está vacía o es demasiado larga
     */
    public final void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new DatosInvalidosException("La dirección es obligatoria.");
        }
        String limpia = direccion.trim();
        if (limpia.length() > 150) {
            throw new DatosInvalidosException("La dirección no puede superar 150 caracteres.");
        }
        this.direccion = limpia;
    }

    /**
     * Devuelve el estado actual del inmueble.
     *
     * @return estado actual del inmueble
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Cambia el estado. Acepta el valor sin importar mayúsculas o espacios
     * extremos y lo guarda con la escritura oficial de {@link #ESTADOS_PERMITIDOS}.
     *
     * @param estado nuevo estado, obligatorio
     * @throws DatosInvalidosException si está vacío o no es un estado permitido
     */
    public final void setEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new DatosInvalidosException("El estado del inmueble es obligatorio.");
        }
        this.estado = ESTADOS_PERMITIDOS.stream()
                .filter(e -> e.equalsIgnoreCase(estado.trim()))
                .findFirst()
                .orElseThrow(() -> new DatosInvalidosException("El estado '" + estado.trim()
                        + "' no es válido. Valores permitidos: " + String.join(", ", ESTADOS_PERMITIDOS) + "."));
    }

    /**
     * Devuelve el propietario del inmueble.
     *
     * @return propietario del inmueble
     */
    public Propietario getPropietario() {
        return propietario;
    }

    /**
     * Cambia el propietario (por ejemplo, tras una venta).
     *
     * @param propietario nuevo dueño, obligatorio
     * @throws DatosInvalidosException si es nulo
     */
    public final void setPropietario(Propietario propietario) {
        if (propietario == null) {
            throw new DatosInvalidosException("El inmueble debe tener un propietario.");
        }
        this.propietario = propietario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inmueble otro)) {
            return false;
        }
        return chip.equals(otro.chip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chip);
    }

    @Override
    public String toString() {
        return getTipo() + " " + chip + " - " + direccion;
    }
}
