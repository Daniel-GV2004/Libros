drop database if exists Libreria;
CREATE DATABASE Libreria;
use Libreria;

CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
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

INSERT INTO usuario (username, password) VALUES ('admin',SHA2('admin', 256));