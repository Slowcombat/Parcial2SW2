package co.edu.poli.inmuebles.domain.model;

import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Persona dueña de uno o varios inmuebles.
 *
 * <p>En esta aplicación los propietarios son datos predeterminados (se cargan
 * con el script de la base de datos); solo se usan para asociarlos a los inmuebles.
 * Dos propietarios son iguales si tienen la misma identificación.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class Propietario {

    private static final Pattern CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final String identificacion;
    private final String nombre;
    private final String correoElectronico;

    /**
     * Crea un propietario validando sus datos.
     *
     * @param identificacion    número de documento, obligatorio
     * @param nombre            nombre completo, obligatorio
     * @param correoElectronico correo con formato válido
     * @throws DatosInvalidosException si algún dato está vacío o el correo es inválido
     */
    public Propietario(String identificacion, String nombre, String correoElectronico) {
        this.identificacion = obligatorio(identificacion, "La identificación");
        this.nombre = obligatorio(nombre, "El nombre");
        String correo = obligatorio(correoElectronico, "El correo electrónico");
        if (!CORREO.matcher(correo).matches()) {
            throw new DatosInvalidosException("El correo electrónico '" + correo + "' no es válido.");
        }
        this.correoElectronico = correo;
    }

    private static String obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException(campo + " del propietario es obligatorio.");
        }
        return valor.trim();
    }

    /**
     * Devuelve el número de documento del propietario.
     *
     * @return número de documento del propietario
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * Devuelve el nombre completo del propietario.
     *
     * @return nombre completo del propietario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el correo electrónico del propietario.
     *
     * @return correo electrónico del propietario
     */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Propietario otro)) {
            return false;
        }
        return identificacion.equals(otro.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    /**
     * Devuelve el texto "nombre (identificación)" usado en las listas de la interfaz.
     *
     * @return texto "nombre (identificación)" usado en las listas de la interfaz
     */
    @Override
    public String toString() {
        return nombre + " (" + identificacion + ")";
    }
}
