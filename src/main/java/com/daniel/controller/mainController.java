package com.daniel.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class mainController {
    @FXML private ListView<String> listLibros;

    @FXML private TextField textNombre;
    @FXML private TextField textAutor;
    @FXML private TextField textPuntuacion;
    @FXML private TextField textDescripcion;

    @FXML private ComboBox<String> comboEstados;

    @FXML private RadioButton radioLeido;
    @FXML private RadioButton radioPendiente;
    @FXML private RadioButton radioEmpezado;
    @FXML private ToggleGroup grupoEstado;

    @FXML
    private void initialize() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/com/daniel/libros/datos/estados.json");
            JsonNode rootNode = mapper.readTree(inputStream);

            List<String> generos = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode nodo : rootNode) {
                    generos.add(nodo.get("nombre").asText());
                }
            }

            comboEstados.getItems().addAll(generos);

        } catch (IOException e) {
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
        return generos;
    }
}
