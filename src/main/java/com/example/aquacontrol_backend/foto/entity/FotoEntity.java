
package com.example.aquacontrol_backend.foto.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class FotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flushing_id", nullable = false)
    private Long flushingId;

    @Column(name = "ruta", nullable = false, length = 500)
    private String ruta;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public FotoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlushingId() {
        return flushingId;
    }

    public void setFlushingId(Long flushingId) {
        this.flushingId = flushingId;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
