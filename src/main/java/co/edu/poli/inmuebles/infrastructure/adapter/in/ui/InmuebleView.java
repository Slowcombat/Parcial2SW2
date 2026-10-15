package co.edu.poli.inmuebles.infrastructure.adapter.in.ui;

import co.edu.poli.inmuebles.application.port.in.ActualizarInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.in.ConsultarInmueblesUseCase;
import co.edu.poli.inmuebles.application.port.in.ConsultarPropietariosUseCase;
import co.edu.poli.inmuebles.application.port.in.CrearInmuebleUseCase;
import co.edu.poli.inmuebles.application.port.in.EliminarInmuebleUseCase;
import co.edu.poli.inmuebles.domain.exception.DatosInvalidosException;
import co.edu.poli.inmuebles.domain.exception.DominioException;
import co.edu.poli.inmuebles.domain.model.Apartamento;
import co.edu.poli.inmuebles.domain.model.Casa;
import co.edu.poli.inmuebles.domain.model.Inmueble;
import co.edu.poli.inmuebles.domain.model.Propietario;
import co.edu.poli.inmuebles.domain.model.Sotano;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Adaptador de entrada: pantalla JavaFX del CRUD de inmuebles.
 *
 * <p>Traduce las acciones del usuario en llamadas a los puertos de entrada
 * (casos de uso) y muestra sus resultados. No conoce la base de datos ni los
 * servicios concretos: solo las interfaces de {@code application.port.in}.</p>
 *
 * <p>Distribución: a la izquierda el formulario; a la derecha los filtros y la
 * tabla. Al seleccionar una fila, el formulario pasa a modo edición.</p>
 *
 * @author Julian Larrarte
 * @version 1.0
 */
public class InmuebleView {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CrearInmuebleUseCase crearUseCase;
    private final ConsultarInmueblesUseCase consultarUseCase;
    private final ActualizarInmuebleUseCase actualizarUseCase;
    private final EliminarInmuebleUseCase eliminarUseCase;
    private final ConsultarPropietariosUseCase propietariosUseCase;

    private final BorderPane raiz = new BorderPane();
    private final ObservableList<Inmueble> filas = FXCollections.observableArrayList();
    private final TableView<Inmueble> tabla = new TableView<>(filas);

    // Formulario: datos comunes
    private final ToggleGroup grupoTipo = new ToggleGroup();
    private final RadioButton rbApartamento = new RadioButton("Apartamento");
    private final RadioButton rbCasa = new RadioButton("Casa");
    private final TextField txtChip = new TextField();
    private final DatePicker dpFechaCompra = new DatePicker();
    private final TextField txtDireccion = new TextField();
    private final ComboBox<String> cbEstado = new ComboBox<>();
    private final ComboBox<Propietario> cbPropietario = new ComboBox<>();

    // Formulario: apartamento
    private final Spinner<Integer> spNumeroPiso = new Spinner<>(1, 200, 1);
    private final VBox seccionApartamento = new VBox(8);

    // Formulario: casa y sótano
    private final Spinner<Integer> spCantidadPisos = new Spinner<>(1, 10, 1);
    private final CheckBox chkSotano = new CheckBox("Tiene sótano");
    private final TextField txtAreaSotano = new TextField();
    private final ComboBox<String> cbTipoUso = new ComboBox<>();
    private final GridPane gridSotano = new GridPane();
    private final VBox seccionCasa = new VBox(8);

    // Botones y estado
    private final Button btnGuardar = new Button("Guardar nuevo");
    private final Button btnActualizar = new Button("Actualizar");
    private final Button btnEliminar = new Button("Eliminar");
    private final Button btnLimpiar = new Button("Limpiar");
    private final Label lblModo = new Label();
    private final Label lblMensaje = new Label();
    private final Label lblResumen = new Label();

    // Filtros
    private final TextField txtBuscarChip = new TextField();
    private final ComboBox<Propietario> cbFiltroPropietario = new ComboBox<>();

    /**
     * Crea la vista recibiendo los casos de uso que necesita.
     *
     * @param crearUseCase        caso de uso Crear
     * @param consultarUseCase    caso de uso Consultar
     * @param actualizarUseCase   caso de uso Actualizar
     * @param eliminarUseCase     caso de uso Eliminar
     * @param propietariosUseCase consulta de propietarios predeterminados
     */
    public InmuebleView(CrearInmuebleUseCase crearUseCase, ConsultarInmueblesUseCase consultarUseCase,
                        ActualizarInmuebleUseCase actualizarUseCase, EliminarInmuebleUseCase eliminarUseCase,
                        ConsultarPropietariosUseCase propietariosUseCase) {
        this.crearUseCase = crearUseCase;
        this.consultarUseCase = consultarUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.eliminarUseCase = eliminarUseCase;
        this.propietariosUseCase = propietariosUseCase;
        construir();
        cargarPropietarios();
        mostrarTodos();
        limpiarFormulario();
    }

    /**
     * Devuelve el nodo raíz para ponerlo en la escena.
     *
     * @return nodo raíz para ponerlo en la escena
     */
    public Parent getRaiz() {
        return raiz;
    }

    // =================================================================== construcción

    private void construir() {
        raiz.getStyleClass().add("raiz");
        raiz.setTop(encabezado());

        ScrollPane formulario = new ScrollPane(formulario());
        formulario.setFitToWidth(true);
        formulario.setPrefWidth(440);
        formulario.setMinWidth(420);
        formulario.getStyleClass().add("panel-scroll");
        raiz.setLeft(formulario);

        raiz.setCenter(panelListado());
        BorderPane.setMargin(raiz.getCenter(), new Insets(16, 16, 16, 8));
        BorderPane.setMargin(formulario, new Insets(16, 8, 16, 16));
    }

    private Node encabezado() {
        Label titulo = new Label("Gestión de Inmuebles");
        titulo.getStyleClass().add("titulo");
        Label subtitulo = new Label("Apartamentos y casas · CRUD con arquitectura hexagonal");
        subtitulo.getStyleClass().add("subtitulo");
        VBox caja = new VBox(2, titulo, subtitulo);
        caja.getStyleClass().add("encabezado");
        return caja;
    }

    private Node formulario() {
        rbApartamento.setToggleGroup(grupoTipo);
        rbCasa.setToggleGroup(grupoTipo);
        rbApartamento.setSelected(true);
        grupoTipo.selectedToggleProperty().addListener((obs, a, b) -> actualizarSecciones());

        txtChip.setPromptText("Ej: AAA0123ABCD");
        txtChip.textProperty().addListener((obs, a, b) -> {
            if (b != null && !b.equals(b.toUpperCase())) {
                txtChip.setText(b.toUpperCase());
            }
        });
        configurarFecha();
        txtDireccion.setPromptText("Ej: Calle 26 # 13-19");
        cbEstado.setItems(FXCollections.observableArrayList(Inmueble.ESTADOS_PERMITIDOS));
        cbPropietario.setPromptText("Seleccione un propietario");
        for (Control c : List.of(cbEstado, cbPropietario, dpFechaCompra, spNumeroPiso, spCantidadPisos, cbTipoUso)) {
            c.setMaxWidth(Double.MAX_VALUE);
        }
        spNumeroPiso.setEditable(true);
        spCantidadPisos.setEditable(true);

        GridPane comunes = grilla();
        fila(comunes, 0, "Tipo", new HBox(16, rbApartamento, rbCasa));
        fila(comunes, 1, "CHIP *", txtChip);
        fila(comunes, 2, "Fecha de compra *", dpFechaCompra);
        fila(comunes, 3, "Dirección *", txtDireccion);
        fila(comunes, 4, "Estado *", cbEstado);
        fila(comunes, 5, "Propietario *", cbPropietario);

        GridPane gApto = grilla();
        fila(gApto, 0, "Número de piso *", spNumeroPiso);
        seccionApartamento.getChildren().setAll(subtitulo("Datos del apartamento"), gApto);

        txtAreaSotano.setPromptText("Ej: 35.5");
        cbTipoUso.setItems(FXCollections.observableArrayList(Sotano.TIPOS_USO_PERMITIDOS));
        cbTipoUso.setPromptText("Uso del sótano");
        chkSotano.selectedProperty().addListener((obs, a, b) -> actualizarSecciones());
        GridPane gCasa = grilla();
        fila(gCasa, 0, "Cantidad de pisos *", spCantidadPisos);
        gridSotano.getColumnConstraints().setAll(grilla().getColumnConstraints());
        gridSotano.setHgap(10);
        gridSotano.setVgap(8);
        fila(gridSotano, 0, "Área (m²) *", txtAreaSotano);
        fila(gridSotano, 1, "Tipo de uso *", cbTipoUso);
        gridSotano.getStyleClass().add("sotano");
        seccionCasa.getChildren().setAll(subtitulo("Datos de la casa"), gCasa, chkSotano, gridSotano);

        btnGuardar.getStyleClass().add("primario");
        btnActualizar.getStyleClass().add("primario");
        btnEliminar.getStyleClass().add("peligro");
        btnGuardar.setOnAction(e -> guardar());
        btnActualizar.setOnAction(e -> actualizar());
        btnEliminar.setOnAction(e -> eliminar());
        btnLimpiar.setOnAction(e -> {
            tabla.getSelectionModel().clearSelection();
            limpiarFormulario();
            lblMensaje.setText("");
        });
        HBox botones = new HBox(8, btnGuardar, btnActualizar, btnEliminar, btnLimpiar);
        botones.setAlignment(Pos.CENTER_LEFT);
        for (Button b : List.of(btnGuardar, btnActualizar, btnEliminar, btnLimpiar)) {
            b.setMinWidth(Region.USE_PREF_SIZE);
            b.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }

        lblModo.getStyleClass().add("modo");
        lblMensaje.setWrapText(true);
        lblMensaje.getStyleClass().add("mensaje");

        VBox tarjeta = new VBox(12, lblModo, subtitulo("Datos del inmueble"), comunes,
                seccionApartamento, seccionCasa, new Separator(), botones, lblMensaje);
        tarjeta.getStyleClass().add("tarjeta");
        return tarjeta;
    }

    private Node panelListado() {
        txtBuscarChip.setPromptText("Buscar por CHIP");
        txtBuscarChip.setOnAction(e -> buscarPorChip());
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setOnAction(e -> buscarPorChip());

        cbFiltroPropietario.setPromptText("Filtrar por propietario");
        cbFiltroPropietario.setPrefWidth(260);
        cbFiltroPropietario.setOnAction(e -> filtrarPorPropietario());
        Button btnTodos = new Button("Ver todos");
        btnTodos.setOnAction(e -> {
            txtBuscarChip.clear();
            cbFiltroPropietario.getSelectionModel().clearSelection();
            mostrarTodos();
        });
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox.setHgrow(txtBuscarChip, Priority.SOMETIMES);
        HBox filtros = new HBox(8, txtBuscarChip, btnBuscar, espacio, cbFiltroPropietario, btnTodos);
        filtros.setAlignment(Pos.CENTER_LEFT);

        // Pesos relativos: cada columna ocupa una fracción del ancho de la tabla (sin barra horizontal).
        columna("CHIP", Inmueble::getChip, 118);
        columna("Tipo", Inmueble::getTipo, 95);
        columna("Dirección", Inmueble::getDireccion, 190);
        columna("Compra", i -> i.getFechaCompra().format(FORMATO_FECHA), 92);
        columna("Estado", Inmueble::getEstado, 110);
        columna("Propietario", i -> i.getPropietario().getNombre(), 160);
        columna("Detalle", Inmueble::getDetalle, 200);
        double total = tabla.getColumns().stream().mapToDouble(c -> (double) c.getUserData()).sum();
        for (TableColumn<Inmueble, ?> c : tabla.getColumns()) {
            c.prefWidthProperty().bind(tabla.widthProperty().subtract(18).multiply((double) c.getUserData() / total));
        }
        tabla.setPlaceholder(new Label("No hay inmuebles para mostrar."));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, a, sel) -> {
            if (sel != null) {
                cargarEnFormulario(sel);
            }
        });
        VBox.setVgrow(tabla, Priority.ALWAYS);

        lblResumen.getStyleClass().add("resumen");
        VBox panel = new VBox(12, filtros, tabla, lblResumen);
        panel.getStyleClass().add("tarjeta");
        return panel;
    }

    private void columna(String titulo, Function<Inmueble, String> valor, double peso) {
        TableColumn<Inmueble, String> col = new TableColumn<>(titulo);
        col.setCellValueFactory(d -> new ReadOnlyStringWrapper(valor.apply(d.getValue())));
        col.setUserData(peso);
        col.setReorderable(false);
        col.setMinWidth(60);
        tabla.getColumns().add(col);
    }

    private void configurarFecha() {
        dpFechaCompra.setPromptText("dd/mm/aaaa");
        dpFechaCompra.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : fecha.format(FORMATO_FECHA);
            }

            @Override
            public LocalDate fromString(String texto) {
                return texto == null || texto.isBlank() ? null : LocalDate.parse(texto.trim(), FORMATO_FECHA);
            }
        });
        // No permitir elegir fechas futuras en el calendario.
        dpFechaCompra.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setDisable(vacia || fecha.isAfter(LocalDate.now()));
            }
        });
    }

    private static GridPane grilla() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        ColumnConstraints etiquetas = new ColumnConstraints(130);
        ColumnConstraints campos = new ColumnConstraints();
        campos.setHgrow(Priority.ALWAYS);
        campos.setFillWidth(true);
        g.getColumnConstraints().setAll(etiquetas, campos);
        return g;
    }

    private static void fila(GridPane g, int fila, String etiqueta, Node campo) {
        g.add(new Label(etiqueta), 0, fila);
        g.add(campo, 1, fila);
    }

    private static Label subtitulo(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("seccion");
        return l;
    }

    // =================================================================== acciones CRUD

    /** CREATE: arma el inmueble desde el formulario y llama al caso de uso Crear. */
    private void guardar() {
        ejecutar(() -> {
            Inmueble nuevo = crearUseCase.crear(leerFormulario());
            recargarManteniendoFiltro();
            seleccionar(nuevo.getChip());
            exito("Inmueble " + nuevo.getChip() + " (" + nuevo.getTipo().toLowerCase() + ") registrado correctamente.");
        });
    }

    /** READ: busca un inmueble por CHIP. */
    private void buscarPorChip() {
        ejecutar(() -> {
            lblMensaje.setText("");
            Inmueble encontrado = consultarUseCase.buscarPorChip(txtBuscarChip.getText());
            cbFiltroPropietario.getSelectionModel().clearSelection();
            filas.setAll(encontrado);
            lblResumen.setText("Resultado de la búsqueda por CHIP " + encontrado.getChip() + ".");
            tabla.getSelectionModel().select(encontrado);
        });
    }

    /** READ: lista los inmuebles del propietario elegido en el filtro. */
    private void filtrarPorPropietario() {
        Propietario p = cbFiltroPropietario.getValue();
        if (p == null) {
            return;
        }
        lblMensaje.setText("");
        ejecutar(() -> {
            List<Inmueble> lista = consultarUseCase.listarPorPropietario(p.getIdentificacion());
            filas.setAll(lista);
            int n = lista.size();
            lblResumen.setText(p.getNombre() + " tiene " + n + (n == 1 ? " inmueble." : " inmuebles."));
        });
    }

    /** READ: lista todos los inmuebles. */
    private void mostrarTodos() {
        ejecutar(() -> {
            filas.setAll(consultarUseCase.listarTodos());
            lblResumen.setText(filas.size() + " inmuebles registrados.");
        });
    }

    /** UPDATE: envía los datos del formulario al caso de uso Actualizar. */
    private void actualizar() {
        ejecutar(() -> {
            Inmueble actualizado = actualizarUseCase.actualizar(leerFormulario());
            recargarManteniendoFiltro();
            seleccionar(actualizado.getChip());
            exito("Inmueble " + actualizado.getChip() + " (" + actualizado.getTipo().toLowerCase() + ") actualizado correctamente.");
        });
    }

    /** DELETE: pide confirmación y llama al caso de uso Eliminar. */
    private void eliminar() {
        String chip = txtChip.getText();
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Confirmar eliminación");
        confirmar.setHeaderText("¿Eliminar el inmueble " + chip + "?");
        confirmar.setContentText("Se borrarán también sus datos de apartamento, casa o sótano. "
                + "Esta acción no se puede deshacer.");
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        ejecutar(() -> {
            eliminarUseCase.eliminar(chip);
            tabla.getSelectionModel().clearSelection();
            limpiarFormulario();
            recargarManteniendoFiltro();
            exito("Inmueble " + chip + " eliminado.");
        });
    }

    // =================================================================== formulario <-> dominio

    /**
     * Convierte el formulario en un objeto del dominio. Las validaciones de
     * negocio las hace el propio dominio al construirlo.
     */
    private Inmueble leerFormulario() {
        LocalDate fecha = leerFecha();
        Propietario propietario = cbPropietario.getValue();
        if (propietario == null) {
            throw new DatosInvalidosException("Seleccione el propietario del inmueble.");
        }
        if (rbApartamento.isSelected()) {
            return new Apartamento(txtChip.getText(), fecha, txtDireccion.getText(), cbEstado.getValue(),
                    propietario, valorSpinner(spNumeroPiso, "número de piso"));
        }
        Sotano sotano = null;
        if (chkSotano.isSelected()) {
            sotano = new Sotano(leerArea(), cbTipoUso.getValue());
        }
        return new Casa(txtChip.getText(), fecha, txtDireccion.getText(), cbEstado.getValue(),
                propietario, valorSpinner(spCantidadPisos, "cantidad de pisos"), sotano);
    }

    private LocalDate leerFecha() {
        String texto = dpFechaCompra.getEditor().getText();
        try {
            // Toma también lo que el usuario escribió sin presionar Enter.
            LocalDate fecha = dpFechaCompra.getConverter().fromString(texto);
            dpFechaCompra.setValue(fecha);
            return fecha;
        } catch (RuntimeException e) {
            throw new DatosInvalidosException("La fecha '" + texto + "' no es válida. Use el formato dd/mm/aaaa.");
        }
    }

    private double leerArea() {
        String texto = txtAreaSotano.getText() == null ? "" : txtAreaSotano.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            throw new DatosInvalidosException("Ingrese el área del sótano en m².");
        }
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("El área del sótano debe ser un número, por ejemplo 35.5");
        }
    }

    private static int valorSpinner(Spinner<Integer> spinner, String campo) {
        String texto = spinner.getEditor().getText().trim();
        try {
            int valor = Integer.parseInt(texto);
            spinner.getValueFactory().setValue(valor);
            return valor;
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("El campo " + campo + " debe ser un número entero.");
        }
    }

    private void cargarEnFormulario(Inmueble i) {
        txtChip.setText(i.getChip());
        dpFechaCompra.setValue(i.getFechaCompra());
        txtDireccion.setText(i.getDireccion());
        cbEstado.setValue(i.getEstado());
        cbPropietario.getItems().stream()
                .filter(p -> p.equals(i.getPropietario()))
                .findFirst()
                .ifPresentOrElse(cbPropietario::setValue, () -> cbPropietario.setValue(i.getPropietario()));
        if (i instanceof Apartamento a) {
            rbApartamento.setSelected(true);
            spNumeroPiso.getValueFactory().setValue(a.getNumeroPiso());
        } else if (i instanceof Casa c) {
            rbCasa.setSelected(true);
            spCantidadPisos.getValueFactory().setValue(c.getCantidadPisos());
            chkSotano.setSelected(c.tieneSotano());
            txtAreaSotano.setText(c.getSotano().map(s -> String.valueOf(s.getAreaM2())).orElse(""));
            cbTipoUso.setValue(c.getSotano().map(Sotano::getTipoUso).orElse(null));
        }
        modoEdicion(true);
        lblMensaje.setText("");
    }

    private void limpiarFormulario() {
        rbApartamento.setSelected(true);
        txtChip.clear();
        dpFechaCompra.setValue(null);
        dpFechaCompra.getEditor().clear();
        txtDireccion.clear();
        cbEstado.setValue(Inmueble.ESTADOS_PERMITIDOS.get(0));
        cbPropietario.setValue(null);
        spNumeroPiso.getValueFactory().setValue(1);
        spCantidadPisos.getValueFactory().setValue(1);
        chkSotano.setSelected(false);
        txtAreaSotano.clear();
        cbTipoUso.setValue(null);
        modoEdicion(false);
        actualizarSecciones();
    }

    /** En edición el CHIP y el tipo no se pueden cambiar (son la identidad del inmueble). */
    private void modoEdicion(boolean editando) {
        txtChip.setDisable(editando);
        rbApartamento.setDisable(editando);
        rbCasa.setDisable(editando);
        btnGuardar.setDisable(editando);
        btnActualizar.setDisable(!editando);
        btnEliminar.setDisable(!editando);
        lblModo.setText(editando ? "EDITANDO " + txtChip.getText() : "NUEVO INMUEBLE");
        lblModo.getStyleClass().removeAll("modo-nuevo", "modo-edicion");
        lblModo.getStyleClass().add(editando ? "modo-edicion" : "modo-nuevo");
    }

    private void actualizarSecciones() {
        boolean esCasa = rbCasa.isSelected();
        mostrar(seccionApartamento, !esCasa);
        mostrar(seccionCasa, esCasa);
        mostrar(gridSotano, esCasa && chkSotano.isSelected());
    }

    private static void mostrar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    // =================================================================== apoyo

    private void cargarPropietarios() {
        ejecutar(() -> {
            List<Propietario> lista = propietariosUseCase.listarPropietarios();
            cbPropietario.setItems(FXCollections.observableArrayList(lista));
            cbFiltroPropietario.setItems(FXCollections.observableArrayList(lista));
        });
    }

    private void recargarManteniendoFiltro() {
        if (cbFiltroPropietario.getValue() != null) {
            filtrarPorPropietario();
        } else {
            txtBuscarChip.clear();
            mostrarTodos();
        }
    }

    private void seleccionar(String chip) {
        filas.stream().filter(i -> i.getChip().equals(chip)).findFirst().ifPresent(i -> {
            tabla.getSelectionModel().select(i);
            tabla.scrollTo(i);
        });
    }

    private void exito(String mensaje) {
        lblMensaje.getStyleClass().removeAll("error");
        lblMensaje.getStyleClass().add("exito");
        lblMensaje.setText("✔ " + mensaje);
    }

    /**
     * Ejecuta una acción y traduce las excepciones a mensajes para el usuario:
     * las reglas de negocio en un aviso, los errores técnicos en un diálogo de error.
     */
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (DominioException e) {
            lblMensaje.getStyleClass().removeAll("exito");
            lblMensaje.getStyleClass().add("error");
            lblMensaje.setText("✖ " + e.getMessage());
        } catch (RuntimeException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Ocurrió un error inesperado");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }
}
