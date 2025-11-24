package com.daniel.dao;

import com.daniel.models.Usuario;
import org.hibernate.Session;

public interface hibernateCitasDao {
    Usuario login(Session session, Usuario usuario);
}