package com.daniel.util;

import com.daniel.models.Autor;
import com.daniel.models.Genero;
import com.daniel.models.Libro;
import com.daniel.models.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {

    static SessionFactory factory = null;

    static {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");

        cfg.addAnnotatedClass(Autor.class);
        cfg.addAnnotatedClass(Genero.class);
        cfg.addAnnotatedClass(Libro.class);
        cfg.addAnnotatedClass(Usuario.class);

        factory = cfg.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }

    public static Session getSession() {
        return factory.openSession();
    }
}