# Gestión de Inmuebles — Arquitectura Hexagonal

Aplicación de escritorio para **gestionar inmuebles** (apartamentos y casas) con las cuatro operaciones **CRUD** sobre una base de datos **MySQL**, una interfaz gráfica en **JavaFX** y el código organizado con **arquitectura hexagonal** (puertos y adaptadores).

> Proyecto académico — Politécnico Grancolombiano · Ingeniería de Software
> Autor: **Julian Larrarte**

![Interfaz de la aplicación](docs/capturas/app-principal.png)

---

## Contenido

1. [¿Qué hace?](#qué-hace)
2. [Tecnologías](#tecnologías)
3. [Arquitectura](#arquitectura)
4. [Modelo de dominio](#modelo-de-dominio)
5. [Estructura del proyecto](#estructura-del-proyecto)
6. [Instalación](#instalación)
7. [Configuración de la base de datos](#configuración-de-la-base-de-datos)
8. [Ejecución](#ejecución)
9. [Operaciones CRUD](#operaciones-crud)
10. [Pruebas y Javadoc](#pruebas-y-javadoc)
11. [Diagramas y video](#diagramas-y-video)

---

## ¿Qué hace?

- Registra **apartamentos** (número de piso) y **casas** (cantidad de pisos y sótano opcional con área y tipo de uso).
- Cada inmueble tiene **CHIP**, **fecha de compra**, **dirección**, **estado** y un **propietario**.
- Permite **crear, consultar, actualizar y eliminar** inmuebles desde la interfaz.
- Consulta por CHIP, lista todos o **filtra por propietario** (un propietario puede tener varios inmuebles).
- Los **propietarios son datos predeterminados**: se cargan con el script SQL y no tienen CRUD.
- Valida las reglas de negocio en el dominio: CHIP único y alfanumérico, fecha de compra no futura, piso entre 1 y 200, pisos entre 1 y 10, área de sótano mayor que 0, etc.

## Tecnologías

| Herramienta | Versión | Uso |
|---|---|---|
| Java (JDK) | 21 | Lenguaje |
| JavaFX (`javafx-controls`) | 21.0.5 | Interfaz gráfica |
| MySQL Server | 8.x | Base de datos relacional |
| MySQL Connector/J | 9.1.0 | Driver JDBC |
| Maven | 3.9+ | Construcción y dependencias |
| JUnit | 5.11 | Pruebas unitarias |
| Javadoc | JDK 21 | Documentación del código |
| PlantUML / SVG | — | Diagramas |
| VS Code + Extension Pack for Java | — | IDE |
| Git + GitHub | — | Control de versiones |
| Claude (Anthropic) | — | Asistente de IA usado durante el desarrollo |

## Arquitectura

La aplicación sigue la **arquitectura hexagonal**: el dominio está en el centro y no depende de ninguna tecnología. La interfaz JavaFX y MySQL son **adaptadores** intercambiables que se conectan a través de **puertos** (interfaces).

![Arquitectura hexagonal](docs/diagramas/arquitectura-hexagonal.png)

| Capa | Paquete | Contenido |
|---|---|---|
| Dominio | `domain.model`, `domain.exception` | `Inmueble` (abstracta), `Apartamento`, `Casa`, `Sotano`, `Propietario` y excepciones de negocio |
| Puertos de entrada | `application.port.in` | Un caso de uso por interfaz: `CrearInmuebleUseCase`, `ConsultarInmueblesUseCase`, `ActualizarInmuebleUseCase`, `EliminarInmuebleUseCase`, `ConsultarPropietariosUseCase` |
| Casos de uso | `application.service` | `InmuebleService`, `PropietarioService` |
| Puertos de salida | `application.port.out` | `InmuebleRepositoryPort`, `PropietarioRepositoryPort` |
| Adaptador de entrada | `infrastructure.adapter.in.ui` | `InmueblesApp`, `InmuebleView` (JavaFX) |
| Adaptador de salida | `infrastructure.adapter.out.persistence` | `MySqlInmuebleRepository`, `MySqlPropietarioRepository`, `ConexionMySQL` (JDBC) |
| Configuración | `infrastructure.config` | `ContenedorDependencias` (composition root), `DatabaseConfig` |

**Regla de dependencia:** las flechas siempre apuntan hacia el centro. La interfaz solo conoce los puertos de entrada; los servicios solo conocen los puertos de salida; el dominio no conoce a nadie. Por eso las pruebas de los casos de uso corren **sin base de datos**, con adaptadores en memoria.

## Modelo de dominio

![Modelo de clases UML](docs/diagramas/modelo-clases-dominio.png)

- `Inmueble` es **abstracta**; `Apartamento` y `Casa` son sus especializaciones (generalización).
- `Casa` **compone** un `Sotano` opcional (multiplicidad `0..1`).
- Cada `Inmueble` pertenece a **un** `Propietario`; un propietario tiene **0..\*** inmuebles.
- El modelo tiene solo las cinco entidades del enunciado. `estado` y `tipoUso` son atributos de texto que la propia entidad valida contra sus valores permitidos (por ejemplo, `estado` solo acepta Disponible, Arrendado, Ocupado o En remodelación).

En la base de datos la herencia se guarda con **una tabla por subclase**: `inmueble` (datos comunes), `apartamento`, `casa` y `sotano`, unidas por el CHIP con `ON DELETE CASCADE`.

Diagrama de clases completo con todas las capas: [docs/diagramas/diagrama-clases-completo.png](docs/diagramas/diagrama-clases-completo.png)

## Estructura del proyecto

```
gestion-inmuebles-hexagonal/
├── pom.xml
├── README.md
├── database/
│   ├── schema.sql                  # Tablas, restricciones y usuario de la app
│   └── data.sql                    # Propietarios predeterminados + inmuebles de ejemplo
├── docs/
│   ├── diagramas/                  # PNG + fuentes (.svg, .puml)
│   ├── capturas/                   # Capturas de la aplicación
│   ├── wiki/                       # Páginas para la Wiki de GitHub
│   └── GUIA_VIDEO.md               # Guion para grabar la demostración
└── src/
    ├── main/
    │   ├── java/co/edu/poli/inmuebles/
    │   │   ├── Launcher.java
    │   │   ├── domain/
    │   │   │   ├── model/          # Inmueble, Apartamento, Casa, Sotano, Propietario
    │   │   │   └── exception/      # Excepciones de negocio
    │   │   ├── application/
    │   │   │   ├── port/in/        # Casos de uso (puertos de entrada)
    │   │   │   ├── port/out/       # Repositorios (puertos de salida)
    │   │   │   └── service/        # Implementación de los casos de uso
    │   │   └── infrastructure/
    │   │       ├── adapter/in/ui/          # JavaFX
    │   │       ├── adapter/out/persistence/ # MySQL + JDBC
    │   │       └── config/                 # Ensamblaje de dependencias
    │   └── resources/
    │       ├── db.properties       # Conexión a MySQL
    │       └── estilos.css         # Estilos de la interfaz
    └── test/java/co/edu/poli/inmuebles/
        ├── domain/InmuebleTest.java
        └── application/InmuebleServiceTest.java
```

## Instalación

**Requisitos:** JDK 21, Maven 3.9+, MySQL Server 8 y Git.

En Ubuntu se pueden instalar así (un comando a la vez):

```bash
sudo apt update
```

```bash
sudo apt install openjdk-21-jdk maven mysql-server git
```

Comprobar las versiones:

```bash
java -version
```

```bash
mvn -version
```

Clonar el repositorio:

```bash
git clone https://github.com/TU_USUARIO/gestion-inmuebles-hexagonal.git
```

```bash
cd gestion-inmuebles-hexagonal
```

> En Windows: instalar el JDK 21 (Temurin), Maven y MySQL Installer; los comandos de `mysql` y `mvn` son los mismos desde PowerShell, usando `mysql -u root -p` en lugar de `sudo mysql`.

## Configuración de la base de datos

1. Verificar que MySQL esté encendido:

```bash
sudo systemctl status mysql
```

2. Crear la base de datos, las tablas y el usuario de la aplicación:

```bash
sudo mysql < database/schema.sql
```

3. Cargar los propietarios predeterminados y los inmuebles de ejemplo:

```bash
sudo mysql < database/data.sql
```

4. Comprobar los datos:

```bash
sudo mysql -e "SELECT nombre FROM inmuebles_db.propietario;"
```

El script crea el usuario **`inmuebles_app`** con contraseña **`Inmuebles2026*`** y permisos solo sobre `inmuebles_db`. La aplicación lee la conexión de [`src/main/resources/db.properties`](src/main/resources/db.properties):

```properties
db.url=jdbc:mysql://localhost:3306/inmuebles_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota
db.user=inmuebles_app
db.password=Inmuebles2026*
```

Para usar otro usuario sin modificar el archivo, se pueden definir las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`, que tienen prioridad sobre `db.properties`.

> Para dejar la base como al inicio en cualquier momento, basta con volver a ejecutar `schema.sql` y `data.sql`.

## Ejecución

Desde la raíz del proyecto:

```bash
mvn clean javafx:run
```

Desde **VS Code**: abrir la carpeta, esperar a que la extensión de Java cargue el proyecto Maven y ejecutar `Launcher.java` con el botón **Run**.

Si MySQL no está disponible, la aplicación muestra un mensaje explicando qué revisar.

## Operaciones CRUD

| Operación | Cómo se hace en la interfaz | Caso de uso |
|---|---|---|
| **Crear** | Elegir *Apartamento* o *Casa*, llenar el formulario (para casa, marcar *Tiene sótano* si aplica) y pulsar **Guardar nuevo** | `CrearInmuebleUseCase.crear` |
| **Consultar todos** | Al abrir la app o con **Ver todos** | `ConsultarInmueblesUseCase.listarTodos` |
| **Consultar por CHIP** | Escribir el CHIP y pulsar **Buscar** (o Enter) | `ConsultarInmueblesUseCase.buscarPorChip` |
| **Consultar por propietario** | Elegir un propietario en *Filtrar por propietario* | `ConsultarInmueblesUseCase.listarPorPropietario` |
| **Actualizar** | Seleccionar una fila, cambiar los datos y pulsar **Actualizar** (el CHIP y el tipo no cambian porque son la identidad del inmueble) | `ActualizarInmuebleUseCase.actualizar` |
| **Eliminar** | Seleccionar una fila, pulsar **Eliminar** y confirmar | `EliminarInmuebleUseCase.eliminar` |

**Propietario con varios inmuebles:** en los datos de ejemplo, *María Fernanda Rojas* tiene 3 inmuebles (dos apartamentos y una casa con sótano). Al filtrarla, la app muestra "María Fernanda Rojas tiene 3 inmuebles."; se le puede asignar un cuarto inmueble para verlo crecer.

Mensajes de validación de ejemplo: CHIP repetido, fecha de compra futura, área de sótano vacía o no numérica, propietario sin seleccionar.

## Pruebas y Javadoc

Pruebas unitarias del dominio y de los casos de uso (no necesitan MySQL):

```bash
mvn test
```

Generar el Javadoc de dominio, casos de uso, puertos y adaptadores:

```bash
mvn javadoc:javadoc
```

Abrir `target/reports/apidocs/index.html` en el navegador (en versiones anteriores del plugin la ruta es `target/site/apidocs/index.html`):

```bash
xdg-open target/reports/apidocs/index.html
```

## Diagramas y video

| Recurso | Enlace |
|---|---|
| Diagrama de arquitectura hexagonal | [Wiki · Arquitectura](https://github.com/TU_USUARIO/gestion-inmuebles-hexagonal/wiki/Arquitectura) · [PNG](docs/diagramas/arquitectura-hexagonal.png) |
| Modelo de clases UML 2.5 | [Wiki · Modelo de clases](https://github.com/TU_USUARIO/gestion-inmuebles-hexagonal/wiki/Modelo-de-clases) · [PNG](docs/diagramas/modelo-clases-dominio.png) |
| Diagrama de clases por capas | [PNG](docs/diagramas/diagrama-clases-completo.png) |
| Video de desarrollo y demostración | [Wiki · Video](https://github.com/TU_USUARIO/gestion-inmuebles-hexagonal/wiki/Video-de-demostracion) · [YouTube](https://youtu.be/ENLACE_DEL_VIDEO) |

Las fuentes editables de los diagramas están en `docs/diagramas/` (`.svg` y `.puml`). Para regenerar los UML:

```bash
plantuml -tpng docs/diagramas/*.puml
```
