-- =====================================================================
--  Gestión de Inmuebles - Esquema de base de datos (MySQL 8+)
--  Estrategia de herencia: una tabla por subclase (table-per-subclass).
--    inmueble      -> datos comunes (Inmueble)
--    apartamento   -> datos propios de Apartamento
--    casa          -> datos propios de Casa
--    sotano        -> sótano opcional de una Casa (0..1)
--    propietario   -> datos predeterminados (no tienen CRUD)
--  Ejecutar:  mysql -u root -p < database/schema.sql
-- =====================================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS inmuebles_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inmuebles_db;

DROP TABLE IF EXISTS sotano;
DROP TABLE IF EXISTS casa;
DROP TABLE IF EXISTS apartamento;
DROP TABLE IF EXISTS inmueble;
DROP TABLE IF EXISTS propietario;

CREATE TABLE propietario (
    identificacion     VARCHAR(20)  NOT NULL,
    nombre             VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(120) NOT NULL,
    CONSTRAINT pk_propietario PRIMARY KEY (identificacion),
    CONSTRAINT uq_propietario_correo UNIQUE (correo_electronico)
);

CREATE TABLE inmueble (
    chip                     VARCHAR(20)  NOT NULL,
    fecha_compra             DATE         NOT NULL,
    direccion                VARCHAR(150) NOT NULL,
    estado                   VARCHAR(20)  NOT NULL,
    propietario_identificacion VARCHAR(20) NOT NULL,
    CONSTRAINT pk_inmueble PRIMARY KEY (chip),
    CONSTRAINT ck_inmueble_estado
        CHECK (estado IN ('Disponible', 'Arrendado', 'Ocupado', 'En remodelación')),
    CONSTRAINT fk_inmueble_propietario FOREIGN KEY (propietario_identificacion)
        REFERENCES propietario (identificacion)
);

CREATE INDEX ix_inmueble_propietario ON inmueble (propietario_identificacion);

CREATE TABLE apartamento (
    chip        VARCHAR(20) NOT NULL,
    numero_piso INT         NOT NULL,
    CONSTRAINT pk_apartamento PRIMARY KEY (chip),
    CONSTRAINT ck_apartamento_piso CHECK (numero_piso BETWEEN 1 AND 200),
    CONSTRAINT fk_apartamento_inmueble FOREIGN KEY (chip)
        REFERENCES inmueble (chip) ON DELETE CASCADE
);

CREATE TABLE casa (
    chip           VARCHAR(20) NOT NULL,
    cantidad_pisos INT         NOT NULL,
    CONSTRAINT pk_casa PRIMARY KEY (chip),
    CONSTRAINT ck_casa_pisos CHECK (cantidad_pisos BETWEEN 1 AND 10),
    CONSTRAINT fk_casa_inmueble FOREIGN KEY (chip)
        REFERENCES inmueble (chip) ON DELETE CASCADE
);

CREATE TABLE sotano (
    chip     VARCHAR(20)   NOT NULL,
    area_m2  DECIMAL(8, 2) NOT NULL,
    tipo_uso VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_sotano PRIMARY KEY (chip),
    CONSTRAINT ck_sotano_area CHECK (area_m2 > 0),
    CONSTRAINT ck_sotano_uso
        CHECK (tipo_uso IN ('Parqueadero', 'Bodega', 'Habitacional', 'Servicios')),
    CONSTRAINT fk_sotano_casa FOREIGN KEY (chip)
        REFERENCES casa (chip) ON DELETE CASCADE
);

-- Usuario propio de la aplicación (en Ubuntu, root de MySQL no acepta
-- contraseña por JDBC). Debe coincidir con src/main/resources/db.properties.
CREATE USER IF NOT EXISTS 'inmuebles_app'@'localhost' IDENTIFIED BY 'Inmuebles2026*';
GRANT SELECT, INSERT, UPDATE, DELETE ON inmuebles_db.* TO 'inmuebles_app'@'localhost';
FLUSH PRIVILEGES;
