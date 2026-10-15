package co.edu.poli.inmuebles;

import co.edu.poli.inmuebles.infrastructure.adapter.in.ui.InmueblesApp;
import javafx.application.Application;

/**
 * Punto de entrada del programa.
 *
 * <p>Es una clase aparte de {@link InmueblesApp} (que extiende
 * {@link Application}) para que la aplicación también arranque desde VS Code o
 * desde un JAR sin configurar el module-path de JavaFX.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Inicia la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        Application.launch(InmueblesApp.class, args);
    }
}
