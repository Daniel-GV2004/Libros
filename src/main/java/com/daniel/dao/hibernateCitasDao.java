package com.daniel.dao;

import com.daniel.models.Usuario;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class hibernateCitasDao {
    public static Usuario login(Session session, Usuario usuario) {

        Query<Usuario> query = session.createQuery(
                "FROM Usuario WHERE username = :user AND password = :pass",
                Usuario.class
        );

        query.setParameter("user", usuario.getUsername());
        query.setParameter("pass", usuario.getPasswordHash());

        return query.uniqueResult();
    }

    /*
    public static  void insertarCitas(Session session, Citas f)
    {
        session.beginTransaction();
        session.merge(f);

        session.getTransaction().commit();
    }

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