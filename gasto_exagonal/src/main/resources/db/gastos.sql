-- Ejecutar manualmente en MySQL 8 antes del primer arranque.
-- Base independiente: no modifica los datos del proyecto gastos.
CREATE DATABASE IF NOT EXISTS gasto_exagonal_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE gasto_exagonal_db;

CREATE TABLE gasto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descripcion VARCHAR(100) NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    fecha DATE NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_gasto_importe CHECK (importe > 0),
    CONSTRAINT ck_gasto_categoria CHECK (categoria IN
        ('ALIMENTACION','TRANSPORTE','VIVIENDA','OCIO','SALUD','OTROS')),
    INDEX idx_gasto_fecha (fecha),
    INDEX idx_gasto_categoria (categoria)
) ENGINE=InnoDB;

INSERT INTO gasto (descripcion, importe, fecha, categoria) VALUES
    ('Compra semanal', 54.30, '2026-10-03', 'ALIMENTACION'),
    ('Abono transporte', 40.00, '2026-10-01', 'TRANSPORTE'),
    ('Cine', 18.50, '2026-10-10', 'OCIO'),
    ('Farmacia', 12.75, '2026-10-12', 'SALUD');
