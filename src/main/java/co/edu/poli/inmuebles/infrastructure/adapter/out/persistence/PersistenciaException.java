package co.edu.poli.inmuebles.infrastructure.adapter.out.persistence;

/**
 * Error técnico de acceso a datos. Envuelve la {@link java.sql.SQLException}
 * para que no se filtre a la aplicación ni al dominio.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción.
     *
     * @param mensaje descripción del problema
     * @param causa   excepción original de JDBC (puede ser {@code null})
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
