package com.daniel.controller;

import com.daniel.dao.hibernateCitasDao;
import com.daniel.dao.hibernateCitasDaoImpl;
import com.daniel.models.Autor;
import com.daniel.models.Genero;
import com.daniel.models.Libro;
import com.daniel.util.HibernateUtil;
import com.daniel.util.Validate;
import com.daniel.util.Alertas;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class mainController {
    @FXML private ListView<String> listLibros;

    @FXML private TextField textNombre;
    @FXML private TextField textAutor;
    @FXML private TextField textPuntuacion;
    @FXML private TextField textDescripcion;

    @FXML private ComboBox<String> comboEstados;

    private List<String> generosSeleccionados;
    private List<Genero> generosJSON;

    SessionFactory factory = HibernateUtil.getSessionFactory();
    Session session = HibernateUtil.getSession();

    hibernateCitasDao hibernate = new hibernateCitasDaoImpl();

    @FXML
    private void initialize() {
        try{
            cargarLibrosEnListView();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/com/daniel/libros/datos/estados.json");
            JsonNode rootNode = mapper.readTree(inputStream);

            List<String> estados = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode nodo : rootNode) {
                    estados.add(nodo.get("nombre").asText());
                }
            }

            comboEstados.getItems().addAll(estados);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/com/daniel/libros/datos/generos.json");

            generosJSON = mapper.readValue(is, new TypeReference<List<Genero>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void AbrirGeneros(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/daniel/libros/UI/selectorGeneros.fxml"));
            Parent root = loader.load();

            generosController controller = loader.getController();

            controller.setCallback(this::recibirGeneros);

            Stage stage = new Stage();
            stage.setTitle("Seleccionar géneros");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> recibirGeneros(List<String> generos) {
        generosSeleccionados = generos;
        return generos;
    }

    public void crearLibro(ActionEvent actionEvent) {
        // Validaciones
        if (Validate.esVacio(textNombre.getText())) {
            Alertas.mostrarError("Error", "El nombre no puede estar vacío.");
            return;
        }

        if (Validate.esVacio(textAutor.getText())) {
            Alertas.mostrarError("Error", "El autor no puede estar vacío.");
            return;
        }

        if (Validate.esVacio(textPuntuacion.getText()) || Validate.puntuacion((Double.parseDouble(textPuntuacion.getText())))) {
            Alertas.mostrarError("Error", "La puntuación no puede estar vacía o ser mayor de 10.");
            return;
        }

        if (Validate.esVacio(textDescripcion.getText())) {
            Alertas.mostrarError("Error", "La descripción no puede estar vacía.");
            return;
        }

        if (comboEstados.getValue() == null) {
            Alertas.mostrarError("Error", "Debe seleccionar un estado.");
            return;
        }

        if (generosSeleccionados == null || generosSeleccionados.isEmpty()) {
            Alertas.mostrarError("Error", "Debe seleccionar al menos un género.");
            return;
        }

        // Validar puntuación numérica
        double puntuacionValue;
        try {
            puntuacionValue = Double.parseDouble(textPuntuacion.getText());
        } catch (NumberFormatException e) {
            Alertas.mostrarError("Error", "La puntuación debe ser un número válido.");
            return;
        }

        // Crear libro
        Libro libro = new Libro();
        libro.setNombre(textNombre.getText());
        libro.setDescripcion(textDescripcion.getText());
        libro.setEstado(comboEstados.getValue());
        libro.setPuntuacion(Double.parseDouble(textPuntuacion.getText()));

        // Manejo del autor
        String nombreAutor = textAutor.getText();
        Autor autor = hibernate.getAutorPorNombre(session, nombreAutor); // Buscar autor existente
        if (autor == null) {
            // Autor no existe → crear nuevo
            autor = new Autor();
            autor.setNombre(nombreAutor);
            // Cascade en Libro se encargará de persistirlo
        }
        libro.setAutor(autor);

        // Manejo de géneros (igual que antes)
        List<Genero> generos = generosSeleccionados.stream()
                .map(nombre -> generosJSON.stream()
                        .filter(g -> g.getNombre().equalsIgnoreCase(nombre))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(g -> session.load(Genero.class, g.getId()))
                .collect(Collectors.toList());
        libro.setGeneros(generos);

        // Guardar libro (autor se guarda automáticamente si es nuevo)
        hibernate.addLibro(session, libro);

        // Refrescar lista
        cargarLibrosEnListView();
    }

    public void cargarLibrosEnListView() {
        List<Libro> libros = hibernate.obtenerTodosLosLibros(session);

        listLibros.getItems().clear();

        for (Libro libro : libros) {
            String linea = libro.getNombre() + " | " + libro.getAutor().getNombre();

            listLibros.getItems().add(linea);
        }
    }
}
