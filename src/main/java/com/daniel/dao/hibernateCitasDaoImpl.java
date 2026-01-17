package com.daniel.dao;

import com.daniel.models.Autor;
import com.daniel.models.Libro;
import com.daniel.models.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class hibernateCitasDaoImpl implements hibernateCitasDao{
    @Override
    public Usuario login(Session session, Usuario usuario) {

        Query<Usuario> query = session.createQuery(
                "FROM Usuario WHERE username = :user AND password = :pass",
                Usuario.class
        );

        query.setParameter("user", usuario.getUsername());
        query.setParameter("pass", usuario.getPasswordHash());

        return query.uniqueResult();
    }

    @Override
    public void addLibro(Session session, Libro libro) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Si el autor ya es persistente, no hace nada.
            // Si es nuevo, cascade persist lo guardará junto con el libro
            session.persist(libro);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void addAutor(Session session, Autor autor) {
        session.beginTransaction();
        session.merge(autor);
        session.getTransaction().commit();
    }

    @Override
    public List<Libro> obtenerTodosLosLibros(Session session) {
        return session.createQuery(
                "SELECT l FROM Libro l JOIN FETCH l.autor", Libro.class
        ).list();
    }

    @Override
    public Autor getAutorPorNombre(Session session, String nombre) {
        try {
            String hql = "FROM Autor WHERE nombre = :nombre";
            Query<Autor> query = session.createQuery(hql, Autor.class);
            query.setParameter("nombre", nombre);
            return query.uniqueResult(); // devuelve null si no existe
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void borrarLibro(Session session, Libro libro) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(libro);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void modificarLibro(Session session, Libro libro) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(libro);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    /*
    public static  void modificarCitas(Session session,Citas f)
    {
        session.beginTransaction();
        session.merge(f);
        session.getTransaction().commit();
    }

    public static  void borrarCitas(Session session,Citas f)
    {

        session.beginTransaction();

        session.remove(f);

        session.getTransaction().commit();
    }
     */
}