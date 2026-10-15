package co.edu.poli.inmuebles.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Datos de conexión a la base de datos.
 *
 * <p>Se leen de {@code db.properties} (en {@code src/main/resources}) y cada valor
 * puede sobrescribirse con las variables de entorno {@code DB_URL}, {@code DB_USER}
 * y {@code DB_PASSWORD}, útil para no subir contraseñas reales a GitHub.</p>
 *
 * @param url      URL JDBC
 * @param usuario  usuario de MySQL
 * @param password contraseña de MySQL
 * @author Julian Larrarte
 * @version 1.0
 */
public record DatabaseConfig(String url, String usuario, String password) {

    private static final String ARCHIVO = "/db.properties";

    /**
     * Carga la configuración desde el archivo y las variables de entorno.
     *
     * @return configuración lista para usar
     * @throws IllegalStateException si no se puede leer el archivo o falta la URL
     */
    public static DatabaseConfig cargar() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getResourceAsStream(ARCHIVO)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO, e);
        }
        String url = valor("DB_URL", props.getProperty("db.url"));
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Falta db.url en db.properties o la variable DB_URL.");
        }
        return new DatabaseConfig(url,
                valor("DB_USER", props.getProperty("db.user", "root")),
                valor("DB_PASSWORD", props.getProperty("db.password", "")));
    }

    private static String valor(String variableEntorno, String porDefecto) {
        String env = System.getenv(variableEntorno);
        return env != null && !env.isBlank() ? env : porDefecto;
    }

    /**
     * Oculta la contraseña al imprimir la configuración.
     *
     * @return texto con la URL y el usuario
     */
    @Override
    public String toString() {
        return "DatabaseConfig[url=" + url + ", usuario=" + usuario + "]";
    }
}
