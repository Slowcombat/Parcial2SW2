package co.edu.poli.inmuebles.infrastructure.adapter.out.persistence;

import co.edu.poli.inmuebles.infrastructure.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Fábrica de conexiones JDBC a MySQL.
 *
 * <p>Abre una conexión nueva por operación; cada repositorio la cierra con
 * try-with-resources. Para una aplicación de escritorio de un solo usuario
 * es suficiente y evita conexiones colgadas.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class ConexionMySQL {

    private final DatabaseConfig config;

    /**
     * Crea la fábrica con los datos de conexión.
     *
     * @param config URL, usuario y contraseña
     */
    public ConexionMySQL(DatabaseConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Abre una conexión nueva.
     *
     * @return conexión abierta; quien la pide debe cerrarla
     * @throws SQLException si MySQL no está disponible o las credenciales fallan
     */
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(config.url(), config.usuario(), config.password());
    }

    /**
     * Verifica que la base de datos responde. Se usa al iniciar la aplicación
     * para mostrar un mensaje claro si MySQL no está configurado.
     *
     * @throws PersistenciaException si no se puede conectar
     */
    public void probar() {
        try (Connection c = abrir()) {
            if (!c.isValid(3)) {
                throw new PersistenciaException("La conexión a MySQL no es válida.", null);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a MySQL (" + config.url() + "): "
                    + e.getMessage(), e);
        }
    }
}
