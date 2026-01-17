package com.daniel.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "libro_papelera")
public class LibroPapelera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "json_contenido", columnDefinition = "TEXT")
    private String jsonContenido;

    @Column(name = "fecha_borrado")
    private LocalDateTime fechaBorrado;

    public LibroPapelera() {}

    public LibroPapelera(String jsonContenido) {
        this.jsonContenido = jsonContenido;
        this.fechaBorrado = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJsonContenido() {
        return jsonContenido;
    }

    public void setJsonContenido(String jsonContenido) {
        this.jsonContenido = jsonContenido;
    }

    public LocalDateTime getFechaBorrado() {
        return fechaBorrado;
    }

    public void setFechaBorrado(LocalDateTime fechaBorrado) {
        this.fechaBorrado = fechaBorrado;
    }
}
