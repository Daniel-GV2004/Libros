package com.daniel.dao;

import com.daniel.models.Autor;
import com.daniel.models.Libro;
import com.daniel.models.Usuario;
import org.hibernate.Session;

import java.util.List;

public interface hibernateCitasDao {
    Usuario login(Session session, Usuario usuario);

    void addLibro(Session session, Libro libro);

    void addAutor(Session session, Autor autor);

    List<Libro> obtenerTodosLosLibros(Session session);

    Autor getAutorPorNombre(Session session, String nombre);


    void borrarLibro(Session session, Libro libro);

    void modificarLibro(Session session, Libro libro);

    void guardarEnPapelera(Session session, String jsonLibro);

    String recuperarUltimoBorrado(Session session);

    void eliminarDePapelera(Session session, Long id);
}