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
import javafx.stage.FileChooser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class mainController {
    @FXML private ListView<Libro> listLibros;

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
        // Configurar CellFactory antes de cargar los libros
        listLibros.setCellFactory(param -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " | " + (item.getAutor() != null ? item.getAutor().getNombre() : ""));
                }
            }
        });

        // Listener para cargar datos en el formulario al seleccionar
        listLibros.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarDatosLibro(newValue);
            }
        });

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
            controller.setGenerosSeleccionados(generosSeleccionados);

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
        listLibros.getItems().addAll(libros);
    }

    private void cargarDatosLibro(Libro libro) {
        textNombre.setText(libro.getNombre());
        textDescripcion.setText(libro.getDescripcion());
        textPuntuacion.setText(String.valueOf(libro.getPuntuacion()));
        textAutor.setText(libro.getAutor() != null ? libro.getAutor().getNombre() : "");
        comboEstados.setValue(libro.getEstado());
        
        // Cargar géneros y marcarlos
        if (libro.getGeneros() != null) {
            generosSeleccionados = libro.getGeneros().stream()
                    .map(Genero::getNombre)
                    .collect(Collectors.toList());
        } else {
            generosSeleccionados = new ArrayList<>();
        }
    }

    public void borrarLibro(ActionEvent actionEvent) {
        Libro libroSeleccionado = listLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            Alertas.mostrarError("Error", "Debes seleccionar un libro para borrar.");
            return;
        }

        try {
            hibernate.borrarLibro(session, libroSeleccionado);
            // Alertas.mostrarInfo("Éxito", "Libro borrado correctamente."); // Removed as requested
            cargarLibrosEnListView();
            // Limpiar formulario opcional
            textNombre.clear();
            textAutor.clear();
            textPuntuacion.clear();
            textDescripcion.clear();
            comboEstados.setValue(null);
            generosSeleccionados = null;
        } catch (Exception e) {
            Alertas.mostrarError("Error", "No se pudo borrar el libro.");
            e.printStackTrace();
        }
    }

    public void modificarLibro(ActionEvent actionEvent) {
        Libro libroSeleccionado = listLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            Alertas.mostrarError("Error", "Debes seleccionar un libro para modificar.");
            return;
        }

        // Validaciones (Reusing validation logic would be better, but copying for now as per minimal changes)
        if (Validate.esVacio(textNombre.getText()) || Validate.esVacio(textAutor.getText()) ||
            Validate.esVacio(textPuntuacion.getText()) || Validate.esVacio(textDescripcion.getText()) ||
            comboEstados.getValue() == null) {
            Alertas.mostrarError("Error", "Todos los campos obligatorios deben estar rellenos.");
            return;
        }

        // Actualizar datos del objeto libroSeleccionado
        libroSeleccionado.setNombre(textNombre.getText());
        libroSeleccionado.setDescripcion(textDescripcion.getText());
        libroSeleccionado.setEstado(comboEstados.getValue());
        libroSeleccionado.setPuntuacion(Double.parseDouble(textPuntuacion.getText()));

        // Manejo del autor
        String nombreAutor = textAutor.getText();
        if (!libroSeleccionado.getAutor().getNombre().equalsIgnoreCase(nombreAutor)) {
             Autor autor = hibernate.getAutorPorNombre(session, nombreAutor);
             if (autor == null) {
                 autor = new Autor();
                 autor.setNombre(nombreAutor);
             }
             libroSeleccionado.setAutor(autor);
        }
        
        // Manejo de géneros
        if (generosSeleccionados != null) {
             List<Genero> generos = generosSeleccionados.stream()
                .map(nombre -> generosJSON.stream()
                        .filter(g -> g.getNombre().equalsIgnoreCase(nombre))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(g -> session.load(Genero.class, g.getId()))
                .collect(Collectors.toList());
             libroSeleccionado.setGeneros(generos);
        }

        try {
            hibernate.modificarLibro(session, libroSeleccionado);
            Alertas.mostrarInfo("Éxito", "Libro modificado correctamente.");
            cargarLibrosEnListView();
        } catch (Exception e) {
            Alertas.mostrarError("Error", "No se pudo modificar el libro.");
            e.printStackTrace();
        }
    }

    public void exportarJSON(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar libros como JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        java.io.File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                List<Libro> libros = hibernate.obtenerTodosLosLibros(session);
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(file, libros);
                Alertas.mostrarInfo("Éxito", "Libros exportados correctamente.");
            } catch (IOException e) {
                Alertas.mostrarError("Error", "No se pudo exportar el archivo.");
                e.printStackTrace();
            }
        }
    }

    public void importarJSON(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar libros desde JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        java.io.File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Libro> librosImportados = mapper.readValue(file, new TypeReference<List<Libro>>() {});

                for (Libro libro : librosImportados) {
                    // Validar/Asignar Autor (para que no duplique si existe por nombre)
                    if (libro.getAutor() != null) {
                         Autor autorExistente = hibernate.getAutorPorNombre(session, libro.getAutor().getNombre());
                         if (autorExistente != null) {
                             libro.setAutor(autorExistente);
                         } else {
                             // Si es nuevo, asegurar ID null para que se cree
                             libro.getAutor().setId(null);
                         }
                    }

                    // Validar/Asignar Géneros (managed)
                    if (libro.getGeneros() != null) {
                         List<Genero> generosManaged = libro.getGeneros().stream()
                            .map(g -> {
                                // Buscar por ID o Nombre si es posible, aquí asumimos que el JSON trae IDs o Nombres compatibles
                                // Si importamos de nuestra propia export, trae IDs.
                                // Para robustez, idealmente buscaríamos por nombre, pero el JSON de generos es fijo.
                                // Simplificación: re-asociar por ID si existe, o buscar por nombre.
                                // Como la lista generosJSON está en memoria:
                                return generosJSON.stream()
                                    .filter(gj -> gj.getNombre().equalsIgnoreCase(g.getNombre())) // Asumiendo que el nombre viene en el JSON
                                    .findFirst()
                                    .map(gj -> session.load(Genero.class, gj.getId()))
                                    .orElse(null);
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                         libro.setGeneros(generosManaged);
                    }

                    // Asegurar ID libro null para que se cree uno nuevo (o mantener si queremos update?? Asumo importar = añadir nuevos)
                    libro.setId(null); 
                    
                    hibernate.addLibro(session, libro);
                }
                
                cargarLibrosEnListView();
                Alertas.mostrarInfo("Éxito", "Libros importados correctamente.");

            } catch (IOException e) {
                Alertas.mostrarError("Error", "No se pudo importar el archivo.");
                e.printStackTrace();
            }
        }
    }
}
