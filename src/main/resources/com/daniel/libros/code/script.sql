drop database if exists Libreria;
CREATE DATABASE Libreria;
use Libreria;

CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nacionalidad VARCHAR(255) NOT NULL DEFAULT 'Desconocida'
);

CREATE TABLE genero (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE libro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    puntuacion DECIMAL(3,2),
    estado VARCHAR(50),

    idAutor INT,
    FOREIGN KEY (idAutor) REFERENCES autor(id)
);

-- Tabla intermedia para relación N:M entre libro y género
CREATE TABLE libro_genero (
    idLibro INT,
    idGenero INT,

    PRIMARY KEY (idLibro, idGenero),

    FOREIGN KEY (idLibro) REFERENCES libro(id)
        ON DELETE CASCADE,

    FOREIGN KEY (idGenero) REFERENCES genero(id)
        ON DELETE CASCADE
);

-- Tabla para login
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password CHAR(64) NOT NULL
);

CREATE TABLE libro_papelera (
    id INT AUTO_INCREMENT PRIMARY KEY,
    json_contenido TEXT NOT NULL,
    fecha_borrado DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO usuario (username, password) VALUES ('admin',SHA2('admin', 256));

INSERT INTO genero (id, nombre) VALUES
(1, 'Terror'),
(2, 'Fantasía'),
(3, 'Novela negra'),
(4, 'Ciencia ficción'),
(5, 'Ficción distópica'),
(6, 'Ficción utópica'),
(7, 'Romance'),
(8, 'Novela histórica'),
(9, 'Misterio'),
(10, 'Novela policiaca'),
(11, 'Suspense'),
(12, 'Thriller psicológico'),
(13, 'Aventura'),
(14, 'Realismo mágico'),
(15, 'Novela costumbrista'),
(16, 'Drama'),
(17, 'Novela social'),
(18, 'Humor'),
(19, 'Novela epistolar'),
(20, 'Novela gótica'),
(21, 'Novela filosófica'),
(22, 'Novela de viajes'),
(23, 'Ficción posapocalíptica'),
(24, 'Fábula'),
(25, 'Novela erótica'),
(26, 'Novela de espías'),
(27, 'Novela política'),
(28, 'Novela judicial');
