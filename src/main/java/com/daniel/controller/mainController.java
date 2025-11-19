package com.daniel.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML private ComboBox<String> comboGenero;

    @FXML private RadioButton radioLeido;
    @FXML private RadioButton radioPendiente;
    @FXML private RadioButton radioEmpezado;
    @FXML private ToggleGroup grupoEstado;

    @FXML
    private void initialize() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/com/daniel/libros/datos/generos.json");
            JsonNode rootNode = mapper.readTree(inputStream);

            List<String> generos = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode nodo : rootNode) {
                    generos.add(nodo.get("nombre").asText());
                }
            }

            comboGenero.getItems().addAll(generos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
