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
                "FROM Usuario WHERE username = :user AND passwordHash = :pass",
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

    @Override
    public void guardarEnPapelera(Session session, String jsonLibro) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            com.daniel.models.LibroPapelera papelera = new com.daniel.models.LibroPapelera(jsonLibro);
            session.persist(papelera);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public String recuperarUltimoBorrado(Session session) {
        try {
            // Obtener el último añadido
            com.daniel.models.LibroPapelera papelera = session.createQuery(
                    "FROM LibroPapelera ORDER BY fechaBorrado DESC", com.daniel.models.LibroPapelera.class
            ).setMaxResults(1).uniqueResult();

            if (papelera != null) {
                // Eliminar de la papelera tras recuperar
                eliminarDePapelera(session, papelera.getId());
                return papelera.getJsonContenido();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void eliminarDePapelera(Session session, Long id) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            com.daniel.models.LibroPapelera papelera = session.get(com.daniel.models.LibroPapelera.class, id);
            if (papelera != null) {
                session.remove(papelera);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
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