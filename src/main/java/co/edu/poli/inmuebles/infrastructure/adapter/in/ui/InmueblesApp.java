package co.edu.poli.inmuebles.infrastructure.adapter.in.ui;

import co.edu.poli.inmuebles.infrastructure.config.ContenedorDependencias;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Aplicación JavaFX: crea la ventana principal y le entrega a la vista los
 * casos de uso (puertos de entrada) ya ensamblados.
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class InmueblesApp extends Application {

    /**
     * Crea la aplicación. JavaFX la instancia por reflexión al llamar a
     * {@link Application#launch(Class, String...)}.
     */
    public InmueblesApp() {
        // Constructor requerido por JavaFX.
    }

    @Override
    public void start(Stage stage) {
        ContenedorDependencias contenedor;
        try {
            contenedor = ContenedorDependencias.crear();
            contenedor.verificarBaseDeDatos();
        } catch (RuntimeException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Base de datos no disponible");
            alerta.setHeaderText("No fue posible conectarse a MySQL");
            alerta.setContentText(e.getMessage()
                    + "\n\nRevise que MySQL esté encendido, que ejecutó database/schema.sql y "
                    + "database/data.sql, y los datos de src/main/resources/db.properties.");
            alerta.getDialogPane().setPrefWidth(620);
            alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alerta.setResizable(true);
            alerta.showAndWait();
            Platform.exit();
            return;
        }

        InmuebleView vista = new InmuebleView(
                contenedor.crearInmueble(),
                contenedor.consultarInmuebles(),
                contenedor.actualizarInmueble(),
                contenedor.eliminarInmueble(),
                contenedor.consultarPropietarios());

        Scene escena = new Scene(vista.getRaiz(), 1280, 760);
        escena.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/estilos.css")).toExternalForm());
        stage.setTitle("Gestión de Inmuebles");
        stage.setMinWidth(1050);
        stage.setMinHeight(640);
        stage.setScene(escena);
        stage.setMaximized(true);
        stage.show();
    }
}
