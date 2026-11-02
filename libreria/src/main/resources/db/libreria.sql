CREATE DATABASE IF NOT EXISTS biblioteca_db CHARACTER SET utf8mb4;
USE biblioteca_db;
CREATE TABLE autor (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(100) NOT NULL,
                       nacionalidad VARCHAR(50) NULL) ENGINE=InnoDB;
CREATE TABLE categoria (
                           id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(50) NOT NULL UNIQUE) ENGINE=InnoDB;
CREATE TABLE libro (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       isbn CHAR(13) NOT NULL UNIQUE,
                       titulo VARCHAR(150) NOT NULL,
                       anio_publicacion SMALLINT NOT NULL,
                       copias INT NOT NULL,
                       autor_id BIGINT NOT NULL,
                       CONSTRAINT ck_libro_copias CHECK (copias >= 0),
                       CONSTRAINT ck_libro_anio CHECK (anio_publicacion >= 1450),
                       CONSTRAINT fk_libro_autor FOREIGN KEY (autor_id) REFERENCES autor(id),
                       INDEX idx_libro_titulo (titulo)) ENGINE=InnoDB;
CREATE TABLE libro_categoria (
                                 libro_id BIGINT NOT NULL, categoria_id BIGINT NOT NULL,
                                 PRIMARY KEY (libro_id, categoria_id),
                                 FOREIGN KEY (libro_id) REFERENCES libro(id),
                                 FOREIGN KEY (categoria_id) REFERENCES categoria(id)) ENGINE=InnoDB;
INSERT INTO autor (nombre, nacionalidad) VALUES
                                             ('Gabriel García Márquez', 'Colombiana'),
                                             ('J. R. R. Tolkien', 'Británica'),
                                             ('George Orwell', 'Británica');
INSERT INTO categoria (nombre) VALUES
                                   ('Fantasía'),
                                   ('Ciencia ficción'),
                                   ('Novela'),
                                   ('Distopía');
INSERT INTO libro (
    isbn,
    titulo,
    anio_publicacion,
    copias,
    autor_id
) VALUES
      ('9780307474728', 'Cien años de soledad', 1967, 5, 1),
      ('9780062315007', 'El amor en los tiempos del cólera', 1985, 3, 1),
      ('9780547928227', 'El Señor de los Anillos', 1954, 8, 2),
      ('9780618640157', 'El Hobbit', 1937, 6, 2),
      ('9780451524935', '1984', 1949, 4, 3),
      ('9780451526342', 'Rebelión en la granja', 1945, 2, 3);
INSERT INTO libro_categoria (libro_id, categoria_id) VALUES
-- Cien años de soledad
(1, 3),
-- El amor en los tiempos del cólera
(2, 3),
-- El Señor de los Anillos
(3, 1),
-- El Hobbit
(4, 1),
-- 1984
(5, 2),
(5, 4),
-- Rebelión en la granja
(6, 3),
(6, 4);