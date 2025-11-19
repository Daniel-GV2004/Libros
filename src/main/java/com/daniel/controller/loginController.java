package com.daniel.controller;

import com.daniel.dao.hibernateCitasDao;
import com.daniel.models.Usuario;
import com.daniel.util.Alertas;
import com.daniel.util.Cifrar;
import com.daniel.util.HibernateUtil;
import com.daniel.util.Validate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Objects;

public class loginController {
    @FXML TextField textUser;
    @FXML TextField textPassword;

    SessionFactory factory = HibernateUtil.getSessionFactory();
    Session session = HibernateUtil.getSession();

    @FXML
    private void initialize() {
        textPassword.setOnAction(event -> {
            try {
                login(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @FXML
    public void login(ActionEvent actionEvent) throws IOException {
        if (textUser.getText() == null){
            Alertas.mostrarError("Error Usuario", "Introduce un usuario en el campo correspondiente");
        } else if (textPassword.getText() == null) {
            Alertas.mostrarError("Error Contraseña", "Introduce una contraseña en el campo correspondiente");
        } else if (!Validate.password(textPassword.getText()) && !Objects.equals(textPassword.getText(), "admin")) {
            Alertas.mostrarError("Error Formato", "La contraseña debe tener al menos un caracter especial, una mayusculas, un numero y 8 caracteres de longitud");
        } else{
            Usuario u = new Usuario();
            u.setUsername(textUser.getText());
            u.setPasswordHash(Cifrar.cifrar(textPassword.getText()));

            Usuario resultado = hibernateCitasDao.login(session, u);

            if (resultado != null) {
                    Parent nuevaVista = FXMLLoader.load(getClass().getResource("/com/daniel/libros/UI/main.fxml"));

                    Scene escenaActual = ((Node) actionEvent.getSource()).getScene();
                    Stage ventana = (Stage) escenaActual.getWindow();

                    ventana.setScene(new Scene(nuevaVista));
                    ventana.show();
            } else {
                Alertas.mostrarError("Error identificacion", "Usuario no encontrado");
            }

        }
    }
}