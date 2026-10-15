package co.edu.poli.inmuebles.infrastructure.adapter.out.persistence;

import co.edu.poli.inmuebles.application.port.out.PropietarioRepositoryPort;
import co.edu.poli.inmuebles.domain.model.Propietario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa {@link PropietarioRepositoryPort} con JDBC sobre MySQL.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class MySqlPropietarioRepository implements PropietarioRepositoryPort {

    private static final String SELECT =
            "SELECT identificacion, nombre, correo_electronico FROM propietario";

    private final ConexionMySQL conexion;

    /**
     * Crea el adaptador.
     *
     * @param conexion fábrica de conexiones a MySQL
     */
    public MySqlPropietarioRepository(ConexionMySQL conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<Propietario> listarTodos() {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(SELECT + " ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            List<Propietario> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar propietarios: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Propietario> buscarPorIdentificacion(String identificacion) {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE identificacion = ?")) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el propietario: " + e.getMessage(), e);
        }
    }

    private static Propietario mapear(ResultSet rs) throws SQLException {
        return new Propietario(rs.getString("identificacion"), rs.getString("nombre"),
                rs.getString("correo_electronico"));
    }
}
