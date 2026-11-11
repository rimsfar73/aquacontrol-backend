package com.example.aquacontrol_backend.foto.entity;

import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class FotoEntity {

    // Compartimos el identificador con el flushing.
    // Así guardamos como máximo una foto por registro.
    @Id
    @Column(name = "flushing_id")
    private Long id;

    // Vinculamos la foto con un flushing existente.
    // Borrar una foto no debe eliminar el flushing.
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flushing_id", nullable = false)
    private FlushingEntity flushing;

    // Indicamos el formato que usaremos al devolver la imagen por HTTP.
    @Column(name = "tipo_contenido", nullable = false, length = 50)
    private String tipoContenido;

    // Guardamos la imagen en la columna bytea de PostgreSQL.
    // La consultamos por separado para mantener livianos los historiales.
    @Column(name = "contenido", nullable = false, columnDefinition = "bytea")
    private byte[] contenido;

    // JPA necesita este constructor para recuperar los registros.
    public FotoEntity() {
    }

    public Long getId() {
        return id;
    }

    public FlushingEntity getFlushing() {
        return flushing;
    }

    public void setFlushing(FlushingEntity flushing) {
        this.flushing = flushing;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }
}