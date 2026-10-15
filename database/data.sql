-- =====================================================================
--  Datos iniciales
--  - Propietarios predeterminados (la app no tiene CRUD de propietarios).
--  - Algunos inmuebles de ejemplo. María Fernanda Rojas tiene VARIOS
--    inmuebles (caso pedido en el enunciado).
--  Ejecutar después de schema.sql:  mysql -u root -p < database/data.sql
-- =====================================================================

SET NAMES utf8mb4;
USE inmuebles_db;

INSERT INTO propietario (identificacion, nombre, correo_electronico) VALUES
    ('1020304050', 'María Fernanda Rojas', 'maria.rojas@example.com'),
    ('79856321',   'Carlos Andrés Méndez', 'carlos.mendez@example.com'),
    ('52147896',   'Laura Patricia Gómez', 'laura.gomez@example.com'),
    ('1032456789', 'Andrés Felipe Castro', 'andres.castro@example.com');

-- Inmuebles de María Fernanda Rojas (3)
INSERT INTO inmueble VALUES ('AAA0101ABCD', '2018-03-15', 'Calle 72 # 10-34, Apto 501', 'Arrendado', '1020304050');
INSERT INTO apartamento VALUES ('AAA0101ABCD', 5);

INSERT INTO inmueble VALUES ('AAA0102EFGH', '2020-07-01', 'Carrera 15 # 93-60, Apto 1203', 'Disponible', '1020304050');
INSERT INTO apartamento VALUES ('AAA0102EFGH', 12);

INSERT INTO inmueble VALUES ('AAA0103JKLM', '2015-11-20', 'Calle 140 # 7-15', 'Ocupado', '1020304050');
INSERT INTO casa VALUES ('AAA0103JKLM', 2);
INSERT INTO sotano VALUES ('AAA0103JKLM', 45.50, 'Parqueadero');

-- Inmueble de Carlos Andrés Méndez (1)
INSERT INTO inmueble VALUES ('AAA0201NPQR', '2021-02-10', 'Transversal 23 # 45-12', 'En remodelación', '79856321');
INSERT INTO casa VALUES ('AAA0201NPQR', 3);

-- Laura y Andrés quedan sin inmuebles para crearlos durante la demostración.
