package com.daniel.controller;

import com.daniel.models.Genero;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class generosController {

    @FXML
    private GridPane grid;

    private final List<CheckBox> checkBoxes = new ArrayList<>();

    private Consumer<List<String>> callback; // 👈 para devolver los datos

    public void initialize() {
        cargarGeneros();
    }

    private void cargarGeneros() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 🟢 Cargar JSON desde resources
            InputStream is = getClass().getResourceAsStream("/com/daniel/libros/datos/generos.json");

            List<Genero> generos = mapper.readValue(is, new TypeReference<List<Genero>>() {});

            int col = 0;
            int row = 0;

            for (Genero g : generos) {
                CheckBox cb = new CheckBox(g.getNombre());
                checkBoxes.add(cb);

                grid.add(cb, col, row);

                // Control de columnas
                col++;
                if (col > 1) {  // 0 y 1 → dos columnas
                    col = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recibir callback desde ventana principal
    public void setCallback(Consumer<List<String>> callback) {
        this.callback = callback;
    }

    @FXML
    private void accept() {
        List<String> seleccionados = checkBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        // 👉 DEVOLVER RESULTADO A LA VENTANA PRINCIPAL
        if (callback != null) callback.accept(seleccionados);

        // Cerrar ventana
        Stage stage = (Stage) grid.getScene().getWindow();
        stage.close();
    }
}
