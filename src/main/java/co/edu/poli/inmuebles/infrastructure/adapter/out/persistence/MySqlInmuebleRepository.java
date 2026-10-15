package co.edu.poli.inmuebles.infrastructure.adapter.out.persistence;

import co.edu.poli.inmuebles.application.port.out.InmuebleRepositoryPort;
import co.edu.poli.inmuebles.domain.model.Apartamento;
import co.edu.poli.inmuebles.domain.model.Casa;
import co.edu.poli.inmuebles.domain.model.Inmueble;
import co.edu.poli.inmuebles.domain.model.Propietario;
import co.edu.poli.inmuebles.domain.model.Sotano;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa {@link InmuebleRepositoryPort} con JDBC sobre MySQL.
 *
 * <p>La herencia del dominio se guarda con una tabla por subclase:
 * {@code inmueble} (datos comunes), {@code apartamento}, {@code casa} y
 * {@code sotano}. Al leer, el subtipo se detecta con {@code LEFT JOIN}: si hay
 * fila en {@code apartamento} es un {@link Apartamento}; si no, es una {@link Casa}.</p>
 *
 * <p>Las escrituras que tocan varias tablas se hacen en una transacción para no
 * dejar datos a medias.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class MySqlInmuebleRepository implements InmuebleRepositoryPort {

    private static final String SELECT_BASE = """
            SELECT i.chip, i.fecha_compra, i.direccion, i.estado,
                   p.identificacion, p.nombre, p.correo_electronico,
                   a.numero_piso, c.cantidad_pisos, s.area_m2, s.tipo_uso
              FROM inmueble i
              JOIN propietario p ON p.identificacion = i.propietario_identificacion
              LEFT JOIN apartamento a ON a.chip = i.chip
              LEFT JOIN casa c        ON c.chip = i.chip
              LEFT JOIN sotano s      ON s.chip = i.chip
            """;

    private final ConexionMySQL conexion;

    /**
     * Crea el adaptador.
     *
     * @param conexion fábrica de conexiones a MySQL
     */
    public MySqlInmuebleRepository(ConexionMySQL conexion) {
        this.conexion = conexion;
    }

    // ------------------------------------------------------------------ CREATE

    @Override
    public void guardar(Inmueble inmueble) {
        enTransaccion("guardar el inmueble", c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO inmueble (chip, fecha_compra, direccion, estado, propietario_identificacion) "
                            + "VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, inmueble.getChip());
                llenarDatosComunes(ps, inmueble, 2);
                ps.executeUpdate();
            }
            insertarSubtipo(c, inmueble);
        });
    }

    // ------------------------------------------------------------------ READ

    @Override
    public Optional<Inmueble> buscarPorChip(String chip) {
        List<Inmueble> r = consultar(SELECT_BASE + " WHERE i.chip = ?", chip);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public boolean existePorChip(String chip) {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM inmueble WHERE chip = ?")) {
            ps.setString(1, chip);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar el CHIP: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Inmueble> listarTodos() {
        return consultar(SELECT_BASE + " ORDER BY i.chip");
    }

    @Override
    public List<Inmueble> listarPorPropietario(String identificacion) {
        return consultar(SELECT_BASE + " WHERE p.identificacion = ? ORDER BY i.chip", identificacion);
    }

    // ------------------------------------------------------------------ UPDATE

    @Override
    public void actualizar(Inmueble inmueble) {
        enTransaccion("actualizar el inmueble", c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE inmueble SET fecha_compra = ?, direccion = ?, estado = ?, "
                            + "propietario_identificacion = ? WHERE chip = ?")) {
                llenarDatosComunes(ps, inmueble, 1);
                ps.setString(5, inmueble.getChip());
                ps.executeUpdate();
            }
            if (inmueble instanceof Apartamento a) {
                ejecutar(c, "UPDATE apartamento SET numero_piso = ? WHERE chip = ?",
                        a.getNumeroPiso(), a.getChip());
            } else if (inmueble instanceof Casa casa) {
                ejecutar(c, "UPDATE casa SET cantidad_pisos = ? WHERE chip = ?",
                        casa.getCantidadPisos(), casa.getChip());
                // El sótano puede agregarse, cambiarse o quitarse: se reemplaza completo.
                ejecutar(c, "DELETE FROM sotano WHERE chip = ?", casa.getChip());
                insertarSotano(c, casa);
            }
        });
    }

    // ------------------------------------------------------------------ DELETE

    @Override
    public void eliminar(String chip) {
        // apartamento, casa y sotano se borran por ON DELETE CASCADE.
        enTransaccion("eliminar el inmueble", c -> ejecutar(c, "DELETE FROM inmueble WHERE chip = ?", chip));
    }

    // ------------------------------------------------------------------ apoyo

    private void insertarSubtipo(Connection c, Inmueble inmueble) throws SQLException {
        if (inmueble instanceof Apartamento a) {
            ejecutar(c, "INSERT INTO apartamento (chip, numero_piso) VALUES (?, ?)",
                    a.getChip(), a.getNumeroPiso());
        } else if (inmueble instanceof Casa casa) {
            ejecutar(c, "INSERT INTO casa (chip, cantidad_pisos) VALUES (?, ?)",
                    casa.getChip(), casa.getCantidadPisos());
            insertarSotano(c, casa);
        } else {
            throw new IllegalArgumentException("Tipo de inmueble no soportado: " + inmueble.getClass());
        }
    }

    private void insertarSotano(Connection c, Casa casa) throws SQLException {
        if (casa.tieneSotano()) {
            Sotano s = casa.getSotano().orElseThrow();
            ejecutar(c, "INSERT INTO sotano (chip, area_m2, tipo_uso) VALUES (?, ?, ?)",
                    casa.getChip(), BigDecimal.valueOf(s.getAreaM2()), s.getTipoUso());
        }
    }

    private static void llenarDatosComunes(PreparedStatement ps, Inmueble i, int desde) throws SQLException {
        ps.setDate(desde, Date.valueOf(i.getFechaCompra()));
        ps.setString(desde + 1, i.getDireccion());
        ps.setString(desde + 2, i.getEstado());
        ps.setString(desde + 3, i.getPropietario().getIdentificacion());
    }

    private static void ejecutar(Connection c, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int k = 0; k < parametros.length; k++) {
                ps.setObject(k + 1, parametros[k]);
            }
            ps.executeUpdate();
        }
    }

    private List<Inmueble> consultar(String sql, Object... parametros) {
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int k = 0; k < parametros.length; k++) {
                ps.setObject(k + 1, parametros[k]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Inmueble> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar inmuebles: " + e.getMessage(), e);
        }
    }

    /** Convierte una fila del SELECT_BASE en el subtipo de dominio correcto. */
    private static Inmueble mapear(ResultSet rs) throws SQLException {
        Propietario propietario = new Propietario(rs.getString("identificacion"),
                rs.getString("nombre"), rs.getString("correo_electronico"));
        String chip = rs.getString("chip");
        var fecha = rs.getDate("fecha_compra").toLocalDate();
        String direccion = rs.getString("direccion");
        String estado = rs.getString("estado");

        int numeroPiso = rs.getInt("numero_piso");
        if (!rs.wasNull()) {
            return new Apartamento(chip, fecha, direccion, estado, propietario, numeroPiso);
        }
        int cantidadPisos = rs.getInt("cantidad_pisos");
        if (rs.wasNull()) {
            throw new PersistenciaException("El inmueble " + chip
                    + " no tiene fila en apartamento ni en casa (datos inconsistentes).", null);
        }
        Sotano sotano = null;
        BigDecimal area = rs.getBigDecimal("area_m2");
        if (area != null) {
            sotano = new Sotano(area.doubleValue(), rs.getString("tipo_uso"));
        }
        return new Casa(chip, fecha, direccion, estado, propietario, cantidadPisos, sotano);
    }

    /** Operación JDBC que puede lanzar SQLException. */
    @FunctionalInterface
    private interface OperacionSql {
        void ejecutar(Connection c) throws SQLException;
    }

    private void enTransaccion(String accion, OperacionSql operacion) {
        try (Connection c = conexion.abrir()) {
            c.setAutoCommit(false);
            try {
                operacion.ejecutar(c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al " + accion + ": " + e.getMessage(), e);
        }
    }
}
