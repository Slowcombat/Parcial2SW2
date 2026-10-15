package co.edu.poli.inmuebles.infrastructure.config;

import co.edu.poli.inmuebles.application.port.in.ActualizarInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.in.ConsultarInmueblesUseCase;
import co.edu.poli.inmuebles.application.port.in.ConsultarPropietariosUseCase;
import co.edu.poli.inmuebles.application.port.in.CrearInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.in.EliminarInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.out.InmuebleRepositoryPort;
import co.edu.poli.inmuebles.application.port.out.PropietarioRepositoryPort;
import co.edu.poli.inmuebles.application.service.InmuebleService;
import co.edu.poli.inmuebles.application.service.PropietarioService;
import co.edu.poli.inmuebles.infrastructure.adapter.out.persistence.ConexionMySQL;
import co.edu.poli.inmuebles.infrastructure.adapter.out.persistence.MySqlInmuebleRepository;
import co.edu.poli.inmuebles.infrastructure.adapter.out.persistence.MySqlPropietarioRepository;

/**
 * Raíz de composición (<i>composition root</i>): el único lugar donde se
 * conectan los adaptadores concretos con los puertos.
 *
 * <p>Aquí se decide que los puertos de salida se implementan con MySQL. Para
 * usar otra base de datos basta con cambiar estas pocas líneas; el dominio, los
 * casos de uso y la interfaz no se modifican.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public final class ContenedorDependencias {

    private final ConexionMySQL conexion;
    private final InmuebleService inmuebleService;
    private final PropietarioService propietarioService;

    private ContenedorDependencias(ConexionMySQL conexion) {
        this.conexion = conexion;
        InmuebleRepositoryPort inmuebles = new MySqlInmuebleRepository(conexion);
        PropietarioRepositoryPort propietarios = new MySqlPropietarioRepository(conexion);
        this.inmuebleService = new InmuebleService(inmuebles, propietarios);
        this.propietarioService = new PropietarioService(propietarios);
    }

    /**
     * Arma todas las dependencias usando la configuración de {@code db.properties}.
     *
     * @return contenedor listo
     */
    public static ContenedorDependencias crear() {
        return new ContenedorDependencias(new ConexionMySQL(DatabaseConfig.cargar()));
    }

    /**
     * Comprueba la conexión a MySQL.
     *
     * @throws co.edu.poli.inmuebles.infrastructure.adapter.out.persistence.PersistenciaException
     *         si no hay conexión
     */
    public void verificarBaseDeDatos() {
        conexion.probar();
    }

    /**
     * Devuelve el caso de uso Crear inmueble.
     *
     * @return caso de uso Crear inmueble
     */
    public CrearInmuebleUseCase crearInmueble() {
        return inmuebleService;
    }

    /**
     * Devuelve el caso de uso Consultar inmuebles.
     *
     * @return caso de uso Consultar inmuebles
     */
    public ConsultarInmueblesUseCase consultarInmuebles() {
        return inmuebleService;
    }

    /**
     * Devuelve el caso de uso Actualizar inmueble.
     *
     * @return caso de uso Actualizar inmueble
     */
    public ActualizarInmuebleUseCase actualizarInmueble() {
        return inmuebleService;
    }

    /**
     * Devuelve el caso de uso Eliminar inmueble.
     *
     * @return caso de uso Eliminar inmueble
     */
    public EliminarInmuebleUseCase eliminarInmueble() {
        return inmuebleService;
    }

    /**
     * Devuelve el caso de uso Consultar propietarios.
     *
     * @return caso de uso Consultar propietarios
     */
    public ConsultarPropietariosUseCase consultarPropietarios() {
        return propietarioService;
    }
}
